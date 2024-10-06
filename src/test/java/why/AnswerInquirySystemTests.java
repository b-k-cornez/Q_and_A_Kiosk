package why;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AnswerInquirySystemTests {

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
    private User guest;

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

        String username = "Barbie";
        String password = "I like pink muffs and I cannot lie";

        //login + verification test
        guestController.loginTest(username,password);
        User C_user =  mockSharedContext.getCurrentUser();
        assertEquals(C_user instanceof Guest, false);

        //write an inquiry
        when(mockView.getInput("Enter subject of inquiry:")).thenReturn("Why");
        when(mockView.getInput("Enter content of inquiry:")).thenReturn("Me");
        inquirerController.contactStaff();
        verify(mockView).displaySuccess("Inquiry successfully submitted");

        //check that inquiry is in list of unanswered inquiries
        ArrayList<Inquiry> inquiries = mockStoreInquiry.getUnansweredInquiries();
        Assertions.assertFalse(inquiries.isEmpty());

        //logout simulation
        authController.logout();
        assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true );
    }

    @Test
    public void adminAnswerInquiryWhenThereIsOne() {

        //simulate admin logging in
        String username_admin = "SillySausage";
        String pw_admin = "linkstowishes";

        guestController.loginTest(username_admin, pw_admin);
        User A_user = mockSharedContext.getCurrentUser();
        Assertions.assertEquals(A_user instanceof Guest, false);

        //mock response
        when(mockView.getInput("Enter content of response: ")).thenReturn("Gibberish");

        //respond to inquiry sent by the user
        Inquiry test = mockStoreInquiry.getInquiryBySubject("Why");
        staffController.respondToInquiry(test);

        //get the list of remaining inquiries
        ArrayList<Inquiry> unansweredInquiriesCheck = mockStoreInquiry.getUnansweredInquiries();

        //test that they responded to the inquiry and that it got removed from list of unanswered inquiries
        verify(mockView).displaySuccess("Email successfully sent!");
        Assertions.assertFalse(unansweredInquiriesCheck.contains(test));

    }

    @Test
    public void adminAnswerInquiryWhenThereAreMultiple(){

        //simulate admin logging in
        String username_admin = "SillySausage";
        String pw_admin = "linkstowishes";

        guestController.loginTest(username_admin, pw_admin);
        User A_user = mockSharedContext.getCurrentUser();
        Assertions.assertEquals(A_user instanceof Guest, false);

        //add some inquiries
        Inquiry inquiry1 = new Inquiry("Test 1", "Test 1", "other@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry1);

        Inquiry inquiry2 = new Inquiry("Test 2", "Test 2", "otherother@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry2);

        Inquiry inquiry3 = new Inquiry("Test 3", "Test 3", "otherotherother@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry3);

        Inquiry inquiry4 = new Inquiry("Test 4", "Test 4", "otherx4@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry4);

        Inquiry inquiry5 = new Inquiry("Test 5", "Test 5", "otherx5@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry5);

        Inquiry do_not_pick_this_one = new Inquiry("Test","Test","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(do_not_pick_this_one);

        //mock response
        when(mockView.getInput("Enter content of response: ")).thenReturn("Gibberish");

        //respond to inquiry sent by the user
        Inquiry test = mockStoreInquiry.getInquiryBySubject("Why");
        staffController.respondToInquiry(test);

        //get the list of remaining inquiries
        ArrayList<Inquiry> unansweredInquiriesCheck = mockStoreInquiry.getUnansweredInquiries();

        //test that they responded to the inquiry and that it got removed from list of unanswered inquiries
        verify(mockView).displaySuccess("Email successfully sent!");
        Assertions.assertFalse(unansweredInquiriesCheck.contains(test));

    }

    @Test
    public void adminTestIfResponseIsBlank(){

        //simulate admin logging in
        String username_admin = "SillySausage";
        String pw_admin = "linkstowishes";

        guestController.loginTest(username_admin, pw_admin);
        User A_user = mockSharedContext.getCurrentUser();
        Assertions.assertEquals(A_user instanceof Guest, false);

        //add some inquiries
        Inquiry inquiry1 = new Inquiry("Test 1", "Test 1", "other@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry1);

        Inquiry inquiry2 = new Inquiry("Test 2", "Test 2", "otherother@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry2);

        Inquiry inquiry3 = new Inquiry("Test 3", "Test 3", "otherotherother@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry3);

        Inquiry inquiry4 = new Inquiry("Test 4", "Test 4", "otherx4@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry4);

        Inquiry inquiry5 = new Inquiry("Test 5", "Test 5", "otherx5@inquiry.com");
        mockStoreInquiry.storeUnansweredInquiries(inquiry5);

        Inquiry do_not_pick_this_one = new Inquiry("Test","Test","inquiry@test.com");
        mockStoreInquiry.storeUnansweredInquiries(do_not_pick_this_one);

        //respond with a blank input (then a text input to stop looping)
        when(mockView.getInput("Enter content of response: ")).thenReturn("").thenReturn("Response");

        //respond to inquiry sent by the user
        Inquiry test = mockStoreInquiry.getInquiryBySubject("Why");
        staffController.respondToInquiry(test);

        //check that error message displays
        verify(mockView).displayError("Please enter a response");

    }

    @Test
    public void staffAnswerInquiryWhenThereIsOne() {

        //simulate admin logging in
        String username_admin = "SillySausage";
        String pw_admin = "linkstowishes";

        guestController.loginTest(username_admin, pw_admin);
        User A_user = mockSharedContext.getCurrentUser();
        Assertions.assertEquals(A_user instanceof Guest, false);

        //simulate admin getting the unanswered inquiry titles
        ArrayList<String> temp_list = staffController.getInquiryTitles();
        Assertions.assertTrue(temp_list.contains("Why"));

        //simulate admin redirecting the inquiry to a member of staff
        Inquiry get_inquiry = mockStoreInquiry.getInquiryBySubject("Why");
        when(mockView.getInput("Please enter email address: ")).thenReturn("superspud@hindeburg.ac.uk");
        adminStaffController.redirectInquiry(get_inquiry);
        verify(mockView).displaySuccess("Inquiry successfully redirected!");

        //verify inquiry is not there
        Inquiry temp = mockStoreInquiry.getInquiryBySubject("Why");
        Assertions.assertFalse(mockStoreInquiry.getUnansweredInquiries().contains(temp));

        //simulate admin logging out
        authController.logout();
        Assertions.assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true );



        //simulate teaching staff logging in
        String username_teach = "SuperSpud";
        String pw_teach = "mashattack123";

        guestController.loginTest(username_teach,pw_teach);
        User T_user =  mockSharedContext.getCurrentUser();
        Assertions.assertEquals(T_user instanceof Guest, false);

        //get the redirected inquiry
        Inquiry test = mockStoreInquiry.getRedirectedBySubject("Why");

        //check inquiry is redirected and not present in list of unanswered inquiries
        ArrayList<Inquiry> unansweredInquiriesCheck = mockStoreInquiry.getUnansweredInquiries();
        ArrayList<Inquiry> redirectedInquiriesCheck = mockStoreInquiry.getRedirectedInquiries();
        Assertions.assertFalse(unansweredInquiriesCheck.contains(test));
        Assertions.assertTrue(redirectedInquiriesCheck.contains(test));

        //simulate responding to inquiry
        when(mockView.getInput("Enter content of response: ")).thenReturn("Gibberish");
        staffController.respondToInquiry(test);

        //check inquiry got sent successfully
        verify(mockView).displaySuccess("Email successfully sent!");

        //check that inquiry is not present in redirected inquiries
        Assertions.assertFalse(redirectedInquiriesCheck.contains(test));

    }

    @Test
    public void staffAnswerInquiryWhenThereAreMultiple() {

        //simulate admin logging in
        String username_admin = "SillySausage";
        String pw_admin = "linkstowishes";

        guestController.loginTest(username_admin, pw_admin);
        User A_user = mockSharedContext.getCurrentUser();
        Assertions.assertEquals(A_user instanceof Guest, false);

        //simulate admin getting the unanswered inquiry titles
        ArrayList<String> temp_list = staffController.getInquiryTitles();
        Assertions.assertTrue(temp_list.contains("Why"));

        //simulate admin redirecting the inquiry to a member of staff
        Inquiry get_inquiry = mockStoreInquiry.getInquiryBySubject("Why");
        when(mockView.getInput("Please enter email address: ")).thenReturn("superspud@hindeburg.ac.uk");
        adminStaffController.redirectInquiry(get_inquiry);
        verify(mockView).displaySuccess("Inquiry successfully redirected!");

        //verify inquiry is not there
        Inquiry temp = mockStoreInquiry.getInquiryBySubject("Why");
        Assertions.assertFalse(mockStoreInquiry.getUnansweredInquiries().contains(temp));

        //simulate admin logging out
        authController.logout();
        Assertions.assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true );



        //adding redirected inquiries
        Inquiry inquiry_test = new Inquiry("Test","Test","inquiry@test.com");
        inquiry_test.setAssignedTo("superspud@hindeburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test);

        Inquiry inquiry_test1 = new Inquiry("Test1","Test1","inquiry@test.com");
        inquiry_test1.setAssignedTo("superspud@hindeburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test1);

        Inquiry inquiry_test2 = new Inquiry("Test2","Test2","inquiry@test.com");
        inquiry_test2.setAssignedTo("superspud@hindeburg.ac.uk");
        mockStoreInquiry.storeRedirectedEnquiries(inquiry_test2);


        //simulate teaching staff logging in
        String username_teach = "SuperSpud";
        String pw_teach = "mashattack123";

        guestController.loginTest(username_teach,pw_teach);
        User T_user =  mockSharedContext.getCurrentUser();
        Assertions.assertEquals(T_user instanceof Guest, false);

        //get the redirected inquiry
        Inquiry test = mockStoreInquiry.getRedirectedBySubject("Why");

        //check inquiry is redirected and not present in list of unanswered inquiries
        ArrayList<Inquiry> unansweredInquiriesCheck = mockStoreInquiry.getUnansweredInquiries();
        ArrayList<Inquiry> redirectedInquiriesCheck = mockStoreInquiry.getRedirectedInquiries();
        Assertions.assertFalse(unansweredInquiriesCheck.contains(test));
        Assertions.assertTrue(redirectedInquiriesCheck.contains(test));

        //simulate responding to inquiry
        when(mockView.getInput("Enter content of response: ")).thenReturn("Gibberish");
        staffController.respondToInquiry(test);

        //check inquiry got sent successfully
        verify(mockView).displaySuccess("Email successfully sent!");

    }

    @Test
    public void teachingTestIfResponseIsBlank(){

        //simulate admin logging in
        String username_admin = "SillySausage";
        String pw_admin = "linkstowishes";

        guestController.loginTest(username_admin, pw_admin);
        User A_user = mockSharedContext.getCurrentUser();
        Assertions.assertEquals(A_user instanceof Guest, false);

        //simulate admin getting the unanswered inquiry titles
        ArrayList<String> temp_list = staffController.getInquiryTitles();
        Assertions.assertTrue(temp_list.contains("Why"));

        //simulate admin redirecting the inquiry to a member of staff
        Inquiry get_inquiry = mockStoreInquiry.getInquiryBySubject("Why");
        when(mockView.getInput("Please enter email address: ")).thenReturn("superspud@hindeburg.ac.uk");
        adminStaffController.redirectInquiry(get_inquiry);
        verify(mockView).displaySuccess("Inquiry successfully redirected!");

        //verify inquiry is not there
        Inquiry temp = mockStoreInquiry.getInquiryBySubject("Why");
        Assertions.assertFalse(mockStoreInquiry.getUnansweredInquiries().contains(temp));

        //simulate admin logging out
        authController.logout();
        Assertions.assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true );



        //simulate teaching staff logging in
        String username_teach = "SuperSpud";
        String pw_teach = "mashattack123";

        guestController.loginTest(username_teach,pw_teach);
        User T_user =  mockSharedContext.getCurrentUser();
        Assertions.assertEquals(T_user instanceof Guest, false);

        //get the redirected inquiry
        Inquiry test = mockStoreInquiry.getRedirectedBySubject("Why");

        //check inquiry is redirected and not present in list of unanswered inquiries
        ArrayList<Inquiry> unansweredInquiriesCheck = mockStoreInquiry.getUnansweredInquiries();
        ArrayList<Inquiry> redirectedInquiriesCheck = mockStoreInquiry.getRedirectedInquiries();
        Assertions.assertFalse(unansweredInquiriesCheck.contains(test));
        Assertions.assertTrue(redirectedInquiriesCheck.contains(test));

        //respond with a blank input (then a text input to stop looping)
        when(mockView.getInput("Enter content of response: ")).thenReturn("").thenReturn("Response");

        //simulate inquiry response
        Inquiry redirected_test = mockStoreInquiry.getRedirectedBySubject("Why");
        staffController.respondToInquiry(redirected_test);

        //check error message gets displayed
        verify(mockView).displayError("Please enter a response");

    }

}
