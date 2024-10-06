package SystemTests;

import external.EmailService;
import model.*;
import view.TextUserInterface;
import external.MockAuthenticationService;
import external.MockEmailService;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import controller.InquirerController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class ConsultFAQSystemTests {
    private SharedContext sharedContext;
    private TextUserInterface view;
    private InquirerController controller;
    private ByteArrayOutputStream outputStream;
    private EmailService emailservice;

    @BeforeEach
    public void setup() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        sharedContext = new SharedContext();
        FAQ faq = new FAQ();
        FAQSection generalQuestions = new FAQSection("General Questions");
        faq.addSection(generalQuestions);
        sharedContext.setFAQ(faq);
    }

    @Test
    public void testSectionFAQTopics()
            throws NullPointerException, URISyntaxException, IOException, ParseException {
        String input = "1\n-1\n-1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface();
        controller = new InquirerController(sharedContext, view, new MockAuthenticationService(),
                new MockEmailService()) {
        };
        controller.consultFAQ();
        assertTrue(outputStream.toString().contains("General Questions"));
        assertTrue(outputStream.toString().contains("[-1] to return to main menu"));
    }

    @Test
    public void testPublicTopicsAndQAs() throws NullPointerException, URISyntaxException, IOException, ParseException {
        SharedContext localSharedContext = new SharedContext();

        FAQ faq = new FAQ();
        FAQSection publicSection = new FAQSection("Public Section");
        faq.addSection(publicSection);
        localSharedContext.setFAQ(faq);

        String input = "-1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        TextUserInterface localView = new TextUserInterface();
        InquirerController localController = new InquirerController(localSharedContext, localView,
                new MockAuthenticationService(), new MockEmailService()) {
        };

        localController.consultFAQ();
        assertTrue(outputStream.toString().contains("Public Section"));
    }

    @Test
    public void testRequestFAQUpdatesAsGuest()
            throws NullPointerException, URISyntaxException, IOException, ParseException {
        String input = "1\n-2\ntest@example.com\n-1\n-1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface();
        controller = new InquirerController(sharedContext, view, new MockAuthenticationService(),
                new MockEmailService()) {
        };
        controller.consultFAQ();
        assertTrue(outputStream.toString().contains("Please enter your email address:"));
        assertTrue(outputStream.toString()
                .contains("Successfully registered test@example.com for updates on General Questions"));
    }

    @Test
    public void testRequestFAQUpdatesAsAuthenticatedUser()
            throws NullPointerException, URISyntaxException, IOException, ParseException {
        String input = "1\n-2\n-1\n-1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface();
        controller = new InquirerController(sharedContext, view, new MockAuthenticationService(),
                new MockEmailService()) {
        };
        sharedContext.setCurrentUser(new AuthenticatedUser("user@example.com", "Student"));
        controller.consultFAQ();
        assertTrue(outputStream.toString()
                .contains("Successfully registered user@example.com for updates on General Questions"));
    }

    @Test
    public void testStopFAQUpdatesAsGuest()
            throws NullPointerException, URISyntaxException, IOException, ParseException {
        String input = "1\n-2\ntest@example.com\n-1\n-1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface();
        controller = new InquirerController(sharedContext, view, new MockAuthenticationService(),
                new MockEmailService()) {
        };
        sharedContext.registerForFAQUpdates("test@example.com", "General Questions");
        controller.consultFAQ();
        assertTrue(outputStream.toString().contains("Please enter your email address:"));
        assertTrue(outputStream.toString()
                .contains("Successfully unregistered test@example.com for updates on General Questions"));
    }

    @Test
    public void testStopFAQUpdatesAsAuthenticatedUser()
            throws NullPointerException, URISyntaxException, IOException, ParseException {
        String input = "1\n-2\n-1\n-1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        view = new TextUserInterface();
        controller = new InquirerController(sharedContext, view, new MockAuthenticationService(),
                new MockEmailService()) {
        };
        sharedContext.setCurrentUser(new AuthenticatedUser("user@example.com", "Student"));
        sharedContext.registerForFAQUpdates("user@example.com", "General Questions");
        controller.consultFAQ();
        assertTrue(outputStream.toString()
                .contains("Successfully unregistered user@example.com for updates on General Questions"));
    }

}