package no.ntnu.bachelor.voicepick.smtp;

import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.smtp.models.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailTest {

    @Test
    @DisplayName("Test creating invalid emails")
    void createInvalidEmail(){
        EmailDto invalidEmailDto1 = new EmailDto("");
        try {
            new Email(invalidEmailDto1, Email.Subject.COMPLETE_REGISTRATION);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        EmailDto invalidEmailDto2 = new EmailDto("wrong-email");
        try {
            new Email(invalidEmailDto2, Email.Subject.COMPLETE_REGISTRATION);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Test creating valid emails")
    void createValidEmail(){
        EmailDto validEmailDto1 = new EmailDto("john@gmail.com");
        Email email1 = new Email(validEmailDto1, Email.Subject.COMPLETE_REGISTRATION);
        assertEquals("john@gmail.com", email1.getRecipient());
        assertEquals("Complete registration - Voice Pick", email1.getEmailSubject().getText());

        EmailDto validEmailDto2 = new EmailDto("kyle@gmail.com");
        Email email2 = new Email(validEmailDto2, Email.Subject.INVITE_CODE);
        assertEquals("kyle@gmail.com", email2.getRecipient());
        assertEquals("Invite code - Voice Pick", email2.getEmailSubject().getText());

        EmailDto validEmailDto3 = new EmailDto("adam@gmail.com");
        Email email3 = new Email(validEmailDto3, Email.Subject.RESET_PASSWORD);
        assertEquals("adam@gmail.com", email3.getRecipient());
        assertEquals("Reset password - Voice Pick", email3.getEmailSubject().getText());
    }
}

