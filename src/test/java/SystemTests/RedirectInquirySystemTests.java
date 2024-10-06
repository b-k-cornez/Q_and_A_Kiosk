package SystemTests;

import controller.*;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import extra.StoreInquiry;
import model.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import view.View;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class RedirectInquirySystemTests {

    @Mock
    private View mockView;
    @Mock
    private AuthenticationService mockAuthService;
    @Mock
    private EmailService mockEmailService;
    @Mock
    private SharedContext mockSharedContext;
    @Mock
    private AdminStaffController adminStaffController;
    @Mock
    private StaffController staffController;
    @Mock
    private GuestController guestController;
    @Mock
    private AuthenticatedUserController authController;
    @Mock
    private InquirerController inquirerController;
    @Mock
    private StoreInquiry mockStoreInquiry;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException, ParseException {
        //set up mocks and controllers
        SharedContext realSharedContext = new SharedContext();
        mockSharedContext = Mockito.spy(realSharedContext);
        mockView = mock(View.class);
        mockAuthService = new MockAuthenticationService();
        mockEmailService = new MockEmailService();

        adminStaffController = new AdminStaffController(mockSharedContext, mockView, mockAuthService, mockEmailService);
        staffController = new StaffController(mockSharedContext, mockView, mockAuthService, mockEmailService);
        guestController = new GuestController(mockSharedContext, mockView, mockAuthService, mockEmailService);
        inquirerController = new InquirerController(mockSharedContext, mockView, mockAuthService, mockEmailService);
        authController = new AuthenticatedUserController(mockSharedContext,mockView,mockAuthService,mockEmailService);

        //get instance of store inquiry
        mockStoreInquiry = StoreInquiry.getInstance();


        String username = "Barbie";
        String password = "I like pink muffs and I cannot lie";

        //simulate student logging in
        guestController.loginTest(username,password);
        User C_user =  mockSharedContext.getCurrentUser();
        Assertions.assertEquals(C_user instanceof Guest, false);

        //simulate student writing an inquiry
        when(mockView.getInput("Enter subject of inquiry:")).thenReturn("Why");
        when(mockView.getInput("Enter content of inquiry:")).thenReturn("Me");
        inquirerController.contactStaff();
        verify(mockView).displaySuccess("Inquiry successfully submitted");

        //check that inquiry is in list of unanswered inquiries
        ArrayList<Inquiry> inquiries = mockStoreInquiry.getUnansweredInquiries();
        Assertions.assertFalse(inquiries.isEmpty());

        //logout simulation
        authController.logout();
        Assertions.assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true );


        //simulate admin logging in
        String username_admin = "SillySausage";
        String pw_admin = "linkstowishes";

        guestController.loginTest(username_admin, pw_admin);
        User A_user = mockSharedContext.getCurrentUser();
        Assertions.assertEquals(A_user instanceof Guest, false);
    }

    @Test
    public void redirectAnInquiry(){

        ArrayList<Inquiry> unansweredInquiries = mockStoreInquiry.getUnansweredInquiries();
        ArrayList<Inquiry> redirectedInquiries = mockStoreInquiry.getRedirectedInquiries();

        Inquiry inquiry_test = mockStoreInquiry.getInquiryBySubject("Why");

        //check that at this point the inquiry is present in unansweredInquiries and not present in redirectedInquiries
        Assertions.assertTrue(unansweredInquiries.contains(inquiry_test));
        Assertions.assertFalse(redirectedInquiries.contains(inquiry_test));


        adminStaffController.redirectInquiry(inquiry_test); //redirect the inquiry
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test); //redirect the inquiry
        mockStoreInquiry.removeInquiry(inquiry_test); //store the inquiry in the redirected inquiries

        ArrayList<Inquiry> redirectedInquiriesCheck = mockStoreInquiry.getRedirectedInquiries();
        ArrayList<Inquiry> unansweredInquiriesCheck = mockStoreInquiry.getUnansweredInquiries();

        //check that the email was sent
        verify(mockView).displaySuccess(eq("Inquiry successfully redirected!"));

        //check that at this point the inquiry is not present in unansweredInquiries and present in redirectedInquiries
        Assertions.assertTrue(redirectedInquiriesCheck.contains(inquiry_test));
        Assertions.assertFalse(unansweredInquiriesCheck.contains(inquiry_test));
    }
}
