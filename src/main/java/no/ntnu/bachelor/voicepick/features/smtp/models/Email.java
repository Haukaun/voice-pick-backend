package no.ntnu.bachelor.voicepick.features.smtp.models;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.VerificationCodeInfo;
import no.ntnu.bachelor.voicepick.models.Warehouse;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
public class Email {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final String recipient;
    private final Subject emailSubject;
    private final String emailBody;
    private String randomPassword;

    private static final List<VerificationCodeInfo> verificationCodes = new ArrayList<>();

    private static final Map<VerificationCodeInfo, Long> joinCodes = new HashMap<>();
    private static final SecureRandom random = new SecureRandom();
    private static final int TOKEN_LENGTH = 8;
    private static final int TOKEN_EXPIRATION_MINUTES = 10;

    public enum Subject {
        RESET_PASSWORD("Reset password - Voice Pick"),
        COMPLETE_REGISTRATION("Complete registration - Voice Pick"),
        INVITE_CODE("Invite code - Voice Pick");

        private final String text;

        Subject(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
    
    /**
     * Checks if the array contains the given code.
     * 
     * @param verificationCode the code to check for
     */
    public static boolean containsVerificationCode(String verificationCode) {
        return verificationCodes.stream().anyMatch(verificationCodeInfo -> verificationCodeInfo.getVerificationCode().equals(verificationCode));
    }

    public static Long containsJoinCode(VerificationCodeInfo verificationCodeInfo) {
        return joinCodes.entrySet().stream()
            .filter(entry -> entry.getKey().getVerificationCode().equals(verificationCodeInfo.getVerificationCode()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);
    }
    /**
     * Checks the array for expired tokens and removes them.
     */
    private static void cleanupExpiredTokens() {
        verificationCodes.removeIf(VerificationCodeInfo::isExpired);
    }

    /**
     *  Generates a random token
     *
     * @return a random token
     */
    private static String generateRandomToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder verificationCode = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            verificationCode.append(chars.charAt(random.nextInt(chars.length())));
        }
        return verificationCode.toString();
    }

    /**
     * Constructs an email based on the subject chosen while creating the email
     * object, also checks if the email recipient is valid at the start
     *
     * @param recipient   email address of the recipient
     * @param emailSubject an enum of predefined values
     * @throws IllegalArgumentException if email does not match regex or when subject is invalid
     */
    public Email(EmailDto recipient, Subject emailSubject) {
        if (!isValidEmailAddress(recipient.getEmail())) {
            throw new IllegalArgumentException("Invalid email address: " + recipient);
        }

        this.recipient = recipient.getEmail();
        this.emailSubject = emailSubject;

        switch (emailSubject) {
            case RESET_PASSWORD -> {
                randomPassword = generateRandomToken();
                emailBody = "<center><h1 style='color:#FED400; font-size:4rem;'>TRACE</h1><h2 style='margin-top:-3rem; margin-bottom:3rem;'>Voice Pick</h2><h2>Reset Password</h2><p style='margin-top:-1rem; margin-bottom:3rem;'>Your temporary password:</p><button onclick='navigator.clipboard.writeText('" + randomPassword + "')' style='width:95%; height:5rem; background-color:unset; border-color:#FED400; border-style:dotted; border-width:0.2rem;  border-radius:1rem; padding:0.5rem; color:unset; font-size:1.5rem;'>" + randomPassword + "</button><p style='color:#D3D3D3; margin-bottom:2rem; margin-top:-0.05rem;'>click to copy the password</p><img src='https://i.postimg.cc/Hs4LH202/Trace-favicon-2x.png' /></center>";
            }
            case COMPLETE_REGISTRATION -> {

                cleanupExpiredTokens();

                String verificationCode = generateRandomToken();
                LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
                verificationCodes.add(new VerificationCodeInfo(verificationCode, recipient.getEmail(), expirationTime));

                emailBody = "<center><h1 style='color:#FED400; font-size:4rem;'>TRACE</h1><h2 style='margin-top:-3rem; margin-bottom:3rem;'>Voice Pick</h2><h2>Confirm Email</h2><p style='margin-top:-1rem; margin-bottom:3rem;'>To finish registration enter this token in the app:</p><button onclick='navigator.clipboard.writeText('" + verificationCode + "')' style='width:95%; height:5rem; background-color:unset; border-color:#FED400; border-style:dotted; border-width:0.2rem;  border-radius:1rem; padding:0.5rem; color:unset; font-size:1.5rem;'>" + verificationCode + "</button><p style='color:#D3D3D3; margin-bottom:2rem; margin-top:-0.05rem;'>click to copy the token</p><img src='https://i.postimg.cc/Hs4LH202/Trace-favicon-2x.png' /></center>";
            }
            case INVITE_CODE -> {
                cleanupExpiredTokens();
                String joinCode = generateRandomToken();
                LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
                verificationCodes.add(new VerificationCodeInfo(joinCode, recipient.getEmail(), expirationTime));
                emailBody = "<center><h1 style='color:#FED400; font-size:4rem;'>TRACE</h1><h2 style='margin-top:-3rem; margin-bottom:3rem;'>Voice Pick</h2><h2>Invite code</h2><p style='margin-top:-1rem; margin-bottom:3rem;'>You have been invited to join x warehouse.</p><button onclick='navigator.clipboard.writeText('" + joinCode +"')' style='width:95%; height:5rem; background-color:unset; border-color:#FED400; border-style:dotted; border-width:0.2rem;  border-radius:1rem; padding:0.5rem; color:unset; font-size:1.5rem;'>" + joinCode + "</button><p style='color:#D3D3D3; margin-bottom:2rem; margin-top:-0.05rem;'>click to copy the invite code</p><img src='https://i.postimg.cc/Hs4LH202/Trace-favicon-2x.png' /></center>";
            }
            default -> throw new IllegalArgumentException("Invalid email subject: " + emailSubject.getText());
        }
    }

    public Email(Long warehouseId, EmailDto recipient) {
        if (!isValidEmailAddress(recipient.getEmail())) {
            throw new IllegalArgumentException("Invalid recipient or inviter email");
        }

        this.recipient = recipient.getEmail();
        this.emailSubject = Subject.INVITE_CODE;

        cleanupExpiredTokens();
        String joinCode = generateRandomToken();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
        joinCodes.put(new VerificationCodeInfo(joinCode, recipient.getEmail(), expirationTime), warehouseId);
        emailBody = "<center><h1 style='color:#FED400; font-size:4rem;'>TRACE</h1><h2 style='margin-top:-3rem; margin-bottom:3rem;'>Voice Pick</h2><h2>Invite code</h2><p style='margin-top:-1rem; margin-bottom:3rem;'>You have been invited to join x warehouse.</p><button onclick='navigator.clipboard.writeText('" + joinCode +"')' style='width:95%; height:5rem; background-color:unset; border-color:#FED400; border-style:dotted; border-width:0.2rem;  border-radius:1rem; padding:0.5rem; color:unset; font-size:1.5rem;'>" + joinCode + "</button><p style='color:#D3D3D3; margin-bottom:2rem; margin-top:-0.05rem;'>click to copy the invite code</p><img src='https://i.postimg.cc/Hs4LH202/Trace-favicon-2x.png' /></center>";
    }

    /**
     * Checks if a string in a valid email
     *
     * @param emailAddress the recipient's email address
     * @return true/false
     */
    private boolean isValidEmailAddress(String emailAddress) {
        return EMAIL_PATTERN.matcher(emailAddress).matches();
    }
}
