package SystemTests;

import controller.AdminStaffController;
import external.EmailService;
import model.AuthenticatedUser;
import model.FAQItem;
import model.FAQSection;
import model.SharedContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.View;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrowseFAQAdminSystemTests {
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
        sharedContext.CurrentUser(authenticatedUser); // Ensure sharedContext allows setting the current user

        mockView = mock(View.class);
        mockEmailService = mock(EmailService.class);
        when(mockView.getInput(anyString())).thenReturn("add", "new Subtopic", "y", "Subtopic Question?", "Subtopic Answer.", "-1");
        controller = new AdminStaffController(sharedContext, mockView, null, mockEmailService);
        SharedContext.ADMIN_STAFF_EMAIL = "mockemail@gmail.com";
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
