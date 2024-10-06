package UnitTests;

import static org.junit.jupiter.api.Assertions.*;

import external.MockEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class MockEmailServiceTest {
    private static final int STATUS_SUCCESS = 0;
    private static final int STATUS_INVALID_SENDER_EMAIL = 1;
    private static final int STATUS_INVALID_RECIPIENT_EMAIL = 2;

    @InjectMocks
    private MockEmailService emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    //Sending email with valid emails should return success
    public void whenSendingEmailWithValidEmails_thenShouldReturnSuccess() {
        int status = emailService.sendEmail("valid.sender@example.com", "valid.recipient@example.com", "Subject", "Email content");
        assertEquals(STATUS_SUCCESS, status, "Sending email with valid emails should return success status code.");
    }

    @Test
    //Sending email with invalid sender email should return error
    public void whenSendingEmailWithInvalidSenderEmail_thenShouldReturnError() {
        int status = emailService.sendEmail("invalid_sender_email", "valid.recipient@example.com", "Subject", "Email content");
        assertEquals(STATUS_INVALID_SENDER_EMAIL, status, "Sending email with an invalid sender email should return error status code.");
    }

    @Test
    //Sending email with invalid recipient email should return error
    public void whenSendingEmailWithInvalidRecipientEmail_thenShouldReturnError() {
        int status = emailService.sendEmail("valid.sender@example.com", "invalid_recipient_email", "Subject", "Email content");
        assertEquals(STATUS_INVALID_RECIPIENT_EMAIL, status, "Sending email with an invalid recipient email should return error status code.");
    }

    @Test
    //Sending email with null emails should return error
    public void whenSendingEmailWithNullEmails_thenShouldReturnError() {
        int status = emailService.sendEmail(null, null, "Subject", "Email content");
        assertTrue(status == STATUS_INVALID_SENDER_EMAIL || status == STATUS_INVALID_RECIPIENT_EMAIL, "Sending email with null emails should return an error status code.");
    }

}