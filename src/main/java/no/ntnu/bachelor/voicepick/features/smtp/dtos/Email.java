package no.ntnu.bachelor.voicepick.features.smtp.dtos;

import lombok.Getter;
import java.util.regex.Pattern;

@Getter
public class Email {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final String recipient;
    private final Subject emailSubject;
    private final String emailBody;

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
     * Constructs an email based on the subject chosen while creating the email object, also checks if the email recipient is valid at the start
     *
     * @param recipient email address of the recipient
     * @param emailSubject an enum of predefined values
     * @throws IllegalArgumentException if email does not match regex or when subject is invalid
     */
    public Email(String recipient, Subject emailSubject) {
        if (!isValidEmailAddress(recipient)) {
            throw new IllegalArgumentException("Invalid email address: " + recipient);
        }

        this.recipient = recipient;
        this.emailSubject = emailSubject;

        switch (emailSubject) {
            case RESET_PASSWORD -> emailBody = "<center><h1 style='color:#FED400; font-size:4rem;'>TRACE</h1><h2 style='margin-top:-3rem; margin-bottom:3rem;'>Voice Pick</h2><h2>Reset Password</h2><p style='margin-top:-1rem; margin-bottom:3rem;'>Your temporary password:</p><button onclick='navigator.clipboard.writeText('IBE4v!ck98@f0')' style='width:95%; height:5rem; background-color:unset; border-color:#FED400; border-style:dotted; border-width:0.2rem;  border-radius:1rem; padding:0.5rem; color:unset; font-size:1.5rem;'>IBE4v!ck98@f0</button><p style='color:#D3D3D3; margin-bottom:2rem; margin-top:-0.05rem;'>click to copy the password</p><img src='https://i.postimg.cc/Hs4LH202/Trace-favicon-2x.png' /></center>";
            case COMPLETE_REGISTRATION -> emailBody = "<center><h1 style='color:#FED400; font-size:4rem;'>TRACE</h1><h2 style='margin-top:-3rem; margin-bottom:3rem;'>Voice Pick</h2><h2>Confirm Email</h2><p style='margin-top:-1rem; margin-bottom:3rem;'>To finish registration enter this token in the app:</p><button onclick='navigator.clipboard.writeText('reg_9Kd!s0M')' style='width:95%; height:5rem; background-color:unset; border-color:#FED400; border-style:dotted; border-width:0.2rem;  border-radius:1rem; padding:0.5rem; color:unset; font-size:1.5rem;'>reg_9Kd!s0M</button><p style='color:#D3D3D3; margin-bottom:2rem; margin-top:-0.05rem;'>click to copy the token</p><img src='https://i.postimg.cc/Hs4LH202/Trace-favicon-2x.png' /></center>";
            case INVITE_CODE -> emailBody = "<center><h1 style='color:#FED400; font-size:4rem;'>TRACE</h1><h2 style='margin-top:-3rem; margin-bottom:3rem;'>Voice Pick</h2><h2>Invite code</h2><p style='margin-top:-1rem; margin-bottom:3rem;'>You have been invited to join x warehouse.</p><button onclick='navigator.clipboard.writeText('w_F8S1AC')' style='width:95%; height:5rem; background-color:unset; border-color:#FED400; border-style:dotted; border-width:0.2rem;  border-radius:1rem; padding:0.5rem; color:unset; font-size:1.5rem;'>w_F8S1AC</button><p style='color:#D3D3D3; margin-bottom:2rem; margin-top:-0.05rem;'>click to copy the invite code</p><img src='https://i.postimg.cc/Hs4LH202/Trace-favicon-2x.png' /></center>";
            default -> throw new IllegalArgumentException("Invalid email subject: " + emailSubject.getText());
        }
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
