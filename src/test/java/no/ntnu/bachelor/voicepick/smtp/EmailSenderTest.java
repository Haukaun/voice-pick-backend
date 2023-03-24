package no.ntnu.bachelor.voicepick.smtp;
import no.ntnu.bachelor.voicepick.features.smtp.services.EmailSender;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EmailSenderTest {
    EmailSender emailSender = new EmailSender();

    @Test
    @DisplayName("Test trying to get results from a invalid future object")
    public void getResultFromInvalidFuture(){
        Future<String> exceptionFuture = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("An error occurred during execution");
        });
        String result = emailSender.getResultFromFuture(exceptionFuture);
        assertEquals("Error: An error occurred during execution", result);
    }

    @Test
    @DisplayName("Test interrupting the future")
    public void getResultFromInterruptedFuture() throws InterruptedException {
        Future<String> interruptedFuture = new CompletableFuture<>();
        Thread testThread = new Thread(() -> {
            interruptedFuture.cancel(true);
        });
        testThread.start();
        testThread.join();
        String result = emailSender.getResultFromFuture(interruptedFuture);
        assertTrue(result.startsWith("Error: The email sending operation was cancelled"));
    }


    @Test
    @DisplayName("Test trying to get results from a valid future object")
    public void getResultFromValidFuture(){
        Future<String> successfulFuture = CompletableFuture.completedFuture("Mail Sent Successfully...");
        String result = emailSender.getResultFromFuture(successfulFuture);
        assertEquals("Mail Sent Successfully...", result);
    }
}
