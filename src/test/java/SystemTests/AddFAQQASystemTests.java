package SystemTests;

import controller.AdminStaffController;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.View;
import external.EmailService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AddFAQQASystemTests {

    private SharedContext sharedContext;
    private AdminStaffController controller;
    private View mockView;
    private EmailService mockEmailService;

    @BeforeEach
    void setUp() {
        sharedContext = new SharedContext();
        FAQSection section = new FAQSection("Topics");
        sharedContext.getFAQ().addSection(section);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser("user@example.com", "AdminStaff");
        sharedContext.CurrentUser(authenticatedUser);

        mockView = mock(View.class);
        mockEmailService = mock(EmailService.class);
        when(mockView.getInput(anyString()))
                .thenReturn("add")
                .thenReturn("New Subtopic")
                .thenReturn("y") // confirms the creation of a new subtopic
                .thenReturn("Subtopic Question?")
                .thenReturn("Subtopic Answer.")
                .thenReturn("-1");

        controller = new AdminStaffController(sharedContext, mockView, null, mockEmailService);
        SharedContext.ADMIN_STAFF_EMAIL = "mockemail@gmail.com";
    }

    @Test
    void HierarchyTest() {
        // mocking input for view
        when(mockView.getInput(anyString()))
                .thenReturn("1")
                .thenReturn("-1");

        // calling the method for testing
        controller.manageFAQ();

        // confirm interactions with mockView
        verify(mockView, atLeastOnce()).displayInfo("[1] Topics");
        verify(mockView, atLeastOnce()).displayInfo("[-1] Exit");
    }

    @Test
    void EmailNotificationsTest() {
        String topic = "Subscribed Topic";
        FAQSection subscribedSection = new FAQSection(topic);
        sharedContext.getFAQ().addSection(subscribedSection);
        sharedContext.registerForFAQUpdates("subscriber@example.com", topic);

        //mocking view input
        when(mockView.getInput(anyString()))
                .thenReturn("add")
                .thenReturn(topic)
                .thenReturn("y")
                .thenReturn("Subscribed Question?")
                .thenReturn("Subscribed Answer.")
                .thenReturn("-1");

        AuthenticatedUser currentUser = new AuthenticatedUser("user@example.com", "AdminStaff");
        sharedContext.getCurrentUser();

        controller.manageFAQ();

        verify(mockEmailService, times(1)).sendEmail(
                eq("user@example.com"), eq(sharedContext.ADMIN_STAFF_EMAIL), eq("FAQ Update Notification"),
                contains("A new FAQ entry was added to the topic: Subscribed Question?"));
    }

    @Test
    void SubtopicCreationTest() {
        // mocking sequential input for view
        when(mockView.getInput(anyString()))
                .thenReturn("add")
                .thenReturn("New Subtopic")
                .thenReturn("y") // confirms the creation of a new subtopic
                .thenReturn("Subtopic Question?")
                .thenReturn("Subtopic Answer.")
                .thenReturn("-1");

        controller.manageFAQ();

        verify(mockEmailService, times(1)).sendEmail(anyString(), anyString(), anyString(), anyString());
        verify(mockView, times(1)).displaySuccess("FAQ item added successfully.");
    }

    @Test
    void FAQAdditionTest() {
        AuthenticatedUser testUser = new AuthenticatedUser("testUser@example.com", "AdminStaff");
        sharedContext.CurrentUser(testUser);
        sharedContext.getCurrentUser();

        when(mockView.getInput(anyString()))
                .thenReturn("Add")
                .thenReturn("New Question?")
                .thenReturn("New Answer.")
                .thenReturn("-1");

        controller.manageFAQ();
        sharedContext.registerForFAQUpdates("testUser@example.com","");

        verify(mockEmailService, atLeastOnce()).sendEmail(
                eq("testUser@example.com"), anyString(), anyString(), anyString());


        // check that an email is sent to the correct recipient
        verify(mockEmailService, times(1)).sendEmail(
                eq("testUser@example.com"), anyString(), anyString(), anyString());

        // no unexpected interactions
        verifyNoMoreInteractions(mockEmailService);
    }

    @Test
    void TopicDuplicationTest() {
        // since the editing feature was combined with the addition
        // creating a duplicate overwrites the original FAQ
        
        FAQSection originalFAQ = new FAQSection("Topics");
        FAQItem initialItem = new FAQItem("Initial Question", "Initial Answer");
        originalFAQ.addItem(initialItem.getQuestion(), initialItem.getAnswer());
        sharedContext.getFAQ().addSection(originalFAQ);

        // adding the duplicate FAQ
        String duplicateQuestion = "New Duplicate Question?";
        String duplicateAnswer = "New Duplicate Answer.";
        when(mockView.getInput(anyString()))
                .thenReturn("1")
                .thenReturn("add")
                .thenReturn(duplicateQuestion)
                .thenReturn(duplicateAnswer)
                .thenReturn("-1");

        controller.manageFAQ();

        boolean addedFAQ = sharedContext.getFAQ().getSections().stream()
                .filter(section -> section.getTopic().equals("Topics"))
                .flatMap(section -> section.getItems().stream())
                .anyMatch(item -> item.getQuestion().equals(duplicateQuestion) && item.getAnswer().equals(duplicateAnswer));
        assertTrue(addedFAQ, "No new FAQ additions.");
    }
}
