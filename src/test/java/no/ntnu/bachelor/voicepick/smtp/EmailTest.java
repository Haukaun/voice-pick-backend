package no.ntnu.bachelor.voicepick.smtp;

import no.ntnu.bachelor.voicepick.features.smtp.dtos.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailTest {

    @Test
    @DisplayName("Test creating invalid emails")
    void createInvalidEmail(){
        try {
            new Email("", Email.Subject.COMPLETE_REGISTRATION);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new Email("wrong-email", Email.Subject.COMPLETE_REGISTRATION);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Test creating valid emails")
    void createValidEmail(){
        Email email1 = new Email("john@gmail.com", Email.Subject.COMPLETE_REGISTRATION);
        assertEquals("john@gmail.com", email1.getRecipient());
        assertEquals("Complete registration - Voice Pick", email1.getEmailSubject().getText());

        Email email2 = new Email("kyle@gmail.com", Email.Subject.INVITE_CODE);
        assertEquals("kyle@gmail.com", email2.getRecipient());
        assertEquals("Invite code - Voice Pick", email2.getEmailSubject().getText());

        Email email3 = new Email("adam@gmail.com", Email.Subject.RESET_PASSWORD);
        assertEquals("adam@gmail.com", email3.getRecipient());
        assertEquals("Reset password - Voice Pick", email3.getEmailSubject().getText());
    }
}
