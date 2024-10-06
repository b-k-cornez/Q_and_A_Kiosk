package SystemTests;

import controller.*;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import extra.StoreInquiry;
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import view.View;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ViewReceivedInquiriesSystemTests {

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
    public void setup() throws IOException, ParseException, URISyntaxException, org.json.simple.parser.ParseException {

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
    public void testWhenThereIsOneInquiry() throws IOException, ParseException {

        //simulate admin user getting all the inquiries
        ArrayList<String> inquiry_list = staffController.getInquiryTitles();

        //if the inquiry is present and is able to be responded to then success
        Assertions.assertTrue(inquiry_list.contains("Why"));
    }

    @Test
    public void testWhenThereAreMultipleInquiries() throws IOException, ParseException {

        //adding test inquiries
        Inquiry inquiry_test = new Inquiry("Test","Test","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test);

        Inquiry inquiry_test1 = new Inquiry("Test1","Test1","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test1);

        Inquiry inquiry_test2 = new Inquiry("Pick Me","Pick Me","theRealOne@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test2);

        Inquiry inquiry_test3 = new Inquiry("Test2","Test2","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test3);

        Inquiry inquiry_test4 = new Inquiry("Test3","Test3","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test4);

        //simulate admin user getting all the inquiries
        ArrayList<String> inquiry_list = staffController.getInquiryTitles();

        //verify that the list contains the sent in inquiry and the added inquiries
        Assertions.assertTrue(inquiry_list.contains("Test"));
        Assertions.assertTrue(inquiry_list.contains("Test1"));
        Assertions.assertTrue(inquiry_list.contains("Pick Me"));
        Assertions.assertTrue(inquiry_list.contains("Test2"));
        Assertions.assertTrue(inquiry_list.contains("Test3"));
        Assertions.assertTrue(inquiry_list.contains("Why"));
    }

    @Test
    public void testForWhenInquiryIsNotThere() throws IOException, ParseException{

        //test inquiries
        Inquiry inquiry_test = new Inquiry("Test","Test","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test);

        Inquiry inquiry_test1 = new Inquiry("Test","Test","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test1);

        Inquiry inquiry_test2 = new Inquiry("Pick Me","Pick Me","theRealOne@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test2);

        Inquiry inquiry_test3 = new Inquiry("Test","Test","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test3);

        Inquiry inquiry_test4 = new Inquiry("Test","Test","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry_test4);

        //simulate admin user getting all the inquiries
        ArrayList<String> inquiry_list = staffController.getInquiryTitles();

        //when inquiry title is not present and therefore cannot be interacted with then success
        Assertions.assertFalse(inquiry_list.contains("Not Here"));
    }

}
