package SystemTests;
import controller.AdminStaffController;
import external.AuthenticationService;
import external.EmailService;
import external.MockEmailService;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import view.View;

public class AddWebpageSystemTests {
    @Mock
    private View mockView;
    @Mock
    private AuthenticationService mockAuthService;
    @Mock
    private MockEmailService mockEmailService;
    @Mock
    private EmailService emailService;
    @Mock
    private SharedContext mockSharedContext;
    @Mock
    private AdminStaffController adminStaffController;
    @Mock
    private AuthenticatedUser testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmailService = mock(MockEmailService.class);
        SharedContext.ADMIN_STAFF_EMAIL = "admin@example.com";
        testUser = new AuthenticatedUser("user@example.com","AdministrativeStaff");
        SharedContext realSharedContext = new SharedContext();
        realSharedContext.setEmailService(mockEmailService);
        realSharedContext.setView(mockView);
        mockSharedContext = Mockito.spy(realSharedContext);
        when(mockSharedContext.getCurrentUser()).thenReturn(testUser);
        doCallRealMethod().when(mockSharedContext).addPage(any(Page.class));
        adminStaffController = new AdminStaffController(mockSharedContext, mockView, mockAuthService, mockEmailService);
    }
    @Test
    public void testAddPage_Success() {
        // Assume the page details are received from the view
        when(mockView.getInput("Enter page title:")).thenReturn("Test Title");
        when(mockView.getInput("Enter page content:")).thenReturn("Test Content");
        when(mockView.getYesNoInput(anyString())).thenReturn(true);

        // Call the method under test
        adminStaffController.addPage();

        // Verify that emailService.sendEmail() was called with the expected arguments
        verify(mockEmailService).sendEmail(
                eq(testUser.getEmail()),
                eq(SharedContext.ADMIN_STAFF_EMAIL),
                eq("New Page Added"),
                contains("A new page titled 'Test Title' has been added.")
        );
        // Verify that the success message was displayed
        verify(mockView).displaySuccess("Added page 'Test Title'");
        verify(mockSharedContext, times(1)).addPage(any(Page.class));

    }
}
