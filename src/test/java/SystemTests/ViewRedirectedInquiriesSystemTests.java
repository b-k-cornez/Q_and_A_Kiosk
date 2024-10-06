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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ViewRedirectedInquiriesSystemTests {

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
    private MenuController menuController;
    @Mock
    private InquirerController inquirerController;
    @Mock
    private AuthenticatedUser testUser;
    @Mock
    private ByteArrayOutputStream outputStream;
    @Mock
    private SharedContext sharedContext;
    @Mock
    private StoreInquiry mockStoreInquiry;
    @Mock
    private AuthenticatedUser admin;
    @Mock
    private AuthenticatedUser staff;
    @Mock
    private Inquiry mockInquiry;

    @BeforeEach
    public void setup() throws IOException, ParseException, URISyntaxException, org.json.simple.parser.ParseException {

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

        mockStoreInquiry = StoreInquiry.getInstance();




        //simulate student logging in
        String username = "Barbie";
        String password = "I like pink muffs and I cannot lie";

        guestController.loginTest(username,password);
        User C_user =  mockSharedContext.getCurrentUser();
        Assertions.assertEquals(C_user instanceof Guest, false);

        //write an inquiry
        when(mockView.getInput("Enter subject of inquiry:")).thenReturn("L");
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

        //simulate admin getting the unanswered inquiry titles
        ArrayList<String> temp_list = staffController.getInquiryTitles();
        Assertions.assertTrue(temp_list.contains("L"));

        //simulate admin redirecting the inquiry to a member of staff
        Inquiry get_inquiry = mockStoreInquiry.getInquiryBySubject("L");
        when(mockView.getInput("Please enter email address: ")).thenReturn("json.d@hindenburg.ac.uk");
        adminStaffController.redirectInquiry(get_inquiry);
        mockStoreInquiry.storeRedirectedEnquiries(get_inquiry); //redirect the inquiry
        mockStoreInquiry.removeInquiry(get_inquiry); //store the inquiry in the redirected inquiries

        verify(mockView).displaySuccess("Inquiry successfully redirected!");

        //verify inquiry is not there
        Inquiry temp = mockStoreInquiry.getRedirectedBySubject("L");
        temp.setAssignedTo("json.d@hindenburg.ac.uk");
        Assertions.assertFalse(mockStoreInquiry.getUnansweredInquiries().contains(temp));

        //logout simulation
        authController.logout();
        Assertions.assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true );


        //simulate teaching staff logging in
        String username_teach = "JSON Derulo";
        String pw_teach = "Desrouleaux";

        guestController.loginTest(username_teach,pw_teach);
        User T_user =  mockSharedContext.getCurrentUser();
        Assertions.assertEquals(T_user instanceof Guest, false);

    }

    @Test
    public void testWhenThereIsOneInquiry() throws IOException, ParseException {

        //simulate teaching staff getting the list of redirected inquiries assigned to them
        ArrayList<String> inquiry_list = staffController.getInquiryTitles();

        //check that the inquiry is present
        Assertions.assertTrue(inquiry_list.contains("L"));

    }

    @Test
    public void testWhenThereAreMultipleInquiries() throws IOException, ParseException {

        //adding test inquiries
        Inquiry inquiry_testp = new Inquiry("Test","Test","inquiry@test.com");
        inquiry_testp.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_testp);

        //System.out.println(inquiry_testp.getAssignedTo());

        Inquiry inquiry_test5 = new Inquiry("Test1","Test1","inquiry@test.com");
        inquiry_test5.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test5);

        Inquiry inquiry_test6 = new Inquiry("Test2","Test2","inquiry@test.com");
        inquiry_test6.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test6);

        Inquiry inquiry_test7 = new Inquiry("Test3","Test3","inquiry@test.com");
        inquiry_test7.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test7);

        Inquiry inquiry_test8 = new Inquiry("Pick","Me","inquiry@test.com");
        inquiry_test8.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test8);

        Inquiry inquiry_test9 = new Inquiry("Test4","Test4","inquiry@test.com");
        inquiry_test9.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test9);

        ArrayList<String> inquiry_list = staffController.getInquiryTitles();

        //check the inquiry is present among the added ones
        Assertions.assertTrue(inquiry_list.contains("L"));

    }

    @Test
    public void testForWhenInquiryIsNotThere() throws IOException, ParseException{

        //adding test inquiries
        Inquiry inquiry_testp = new Inquiry("Test","Test","inquiry@test.com");
        inquiry_testp.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_testp);

        Inquiry inquiry_test5 = new Inquiry("Test1","Test1","inquiry@test.com");
        inquiry_test5.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test5);

        Inquiry inquiry_test6 = new Inquiry("Test2","Test2","inquiry@test.com");
        inquiry_test6.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test6);

        Inquiry inquiry_test7 = new Inquiry("Test3","Test3","inquiry@test.com");
        inquiry_test7.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test7);

        Inquiry inquiry_test8 = new Inquiry("Pick","Me","inquiry@test.com");
        inquiry_test8.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test8);

        Inquiry inquiry_test9 = new Inquiry("Test4","Test4","inquiry@test.com");
        inquiry_test9.setAssignedTo("json.d@hindenburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test9);

        //not present inquiry
        Inquiry not_present = new Inquiry("Not Here","Not Here","inquiry@test.com");
        not_present.setAssignedTo("thetickfan@hindeburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(not_present);

        //run the method
        ArrayList<String> inquiry_list = staffController.getInquiryTitles();

        //when inquiry is not present and therefore cannot be interacted with then success
        Assertions.assertFalse(inquiry_list.contains("Not Here"));
    }
}
