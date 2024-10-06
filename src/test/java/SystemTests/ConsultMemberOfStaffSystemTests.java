package SystemTests;

import controller.InquirerController;
import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.Guest;
import model.Inquiry;
import model.SharedContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import view.View;

public class ConsultMemberOfStaffSystemTests {
    @Mock
    private View mockView;
    @Mock
    private AuthenticationService mockAuthService;
    @Mock
    private EmailService mockEmailService;
    @Mock
    private SharedContext mockSharedContext;
    @Mock
    private InquirerController inquirerController;
    @Mock
    private Inquiry inquiry;
    @Mock
    private AuthenticatedUser student;
    @Mock
    private Guest guest;

    @BeforeEach
    public void setup() {
        mockSharedContext = mock(SharedContext.class);
        mockView = mock(View.class);
        mockEmailService = mock(EmailService.class);
        mockAuthService = mock(AuthenticationService.class);
        inquirerController = new InquirerController(mockSharedContext, mockView, mockAuthService, mockEmailService);
        SharedContext.ADMIN_STAFF_EMAIL = "admin@example.com";
    }

    @Test
    public void contactStaffGuest(){
        guest = new Guest();
        mockSharedContext.setCurrentUser(guest);

        when(mockView.getInput("Enter subject of inquiry:")).thenReturn("Test");
        when(mockView.getInput("Enter content of inquiry:")).thenReturn("Test");
        when(mockView.getInput("Enter your email address:")).thenReturn("test@email.com");

        inquirerController.contactStaff();

        verify(mockView).displaySuccess(eq("Inquiry successfully submitted"));


    }

    @Test
    public void contactStaffStudent(){
        student = new AuthenticatedUser("test@email","Student");
        mockSharedContext.setCurrentUser(student);

        when(mockView.getInput("Enter subject of inquiry:")).thenReturn("Test");
        when(mockView.getInput("Enter content of inquiry:")).thenReturn("Test");

        inquirerController.contactStaff();

        verify(mockView).displaySuccess(eq("Inquiry successfully submitted"));
    }

}
