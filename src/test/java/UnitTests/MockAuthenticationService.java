package UnitTests;
import controller.AuthenticatedUserController;
import controller.GuestController;
import external.AuthenticationService;
import external.EmailService;
import external.MockEmailService;
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;
import view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MockAuthenticationService {


    private View mockView;

    private ByteArrayOutputStream testOut;


    private AuthenticationService mockAuthService;

    private EmailService mockEmailService;

    private SharedContext mockSharedContext;

    private GuestController guestController;
    private AuthenticatedUserController authController;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException, ParseException, org.json.simple.parser.ParseException {
        mockAuthService = new external.MockAuthenticationService();
        mockEmailService = new MockEmailService();
        mockSharedContext = new SharedContext();
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        mockView = new TextUserInterface() {
            @Override
            public String getInput(String prompt) {
                return null;
            }

            @Override
            public boolean getYesNoInput(String prompt) {
                return false;
            }

            @Override
            public void displayInfo(String message) {

            }

            @Override
            public void displaySuccess(String message) {

            }

            @Override
            public void displayWarning(String message) {

            }

            @Override
            public void displayError(String message) {

            }

            @Override
            public void displayException(Exception exception) {

            }

            @Override
            public void displayDivider() {

            }

            @Override
            public void displaySearchResults(Collection<PageSearchResult> searchResults) {

            }

            public void displayFAQSection(String s, boolean b) {

            }

            public void displayFAQ(FAQ faq, boolean b) {

            }

            @Override
            public void displayInquiry(Inquiry inquiry) {

            }
        };
        guestController = new GuestController(mockSharedContext,mockView,mockAuthService,mockEmailService);
        authController = new AuthenticatedUserController(mockSharedContext,mockView,mockAuthService,mockEmailService);

    }


    @Test
    void studentLogin(){
        String username = "Barbie";
        String password = "I like pink muffs and I cannot lie";
        String email = "barb78916@hindenburg.ac.uk";
        String role = "Student";
        String status = "{\"email\": \"" + email + "\", \"role\": \"" + role + "\"}";


        guestController.loginTest(username,password);
        AuthenticatedUser C_user = (AuthenticatedUser) mockSharedContext.getCurrentUser();

        assertEquals(C_user.getEmail(),email);
        assertEquals(C_user.getRole(),role);
        assertEquals(mockSharedContext.getCurrentUser() instanceof AuthenticatedUser,true);
    }

    @Test
    void LogoutSuccess(){
        String username = "Barbie";
        String password = "I like pink muffs and I cannot lie";
        String email = "barb78916@hindenburg.ac.uk";
        String role = "Student";
        String status = "{\"email\": \"" + email + "\", \"role\": \"" + role + "\"}";


        guestController.loginTest(username,password);
        AuthenticatedUser C_user = (AuthenticatedUser) mockSharedContext.getCurrentUser();
        authController.logout();
        assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true );
    }
    @Test
    void FailedPassword(){
        String username = "Barbie";
        String password = "I like pink muffs and I lie";
        String email = "barb78916@hindenburg.ac.uk";
        String role = "Student";
        String status = "{\"email\": \"" + email + "\", \"role\": \"" + role + "\"}";


        guestController.loginTest(username,password);
        User C_user =  mockSharedContext.getCurrentUser();

        assertEquals(C_user instanceof Guest, true);
    }
    @Test
    void Logout_successful(){
        String username = "Barbie";
        String password = "I like pink muffs and I lie";
        String email = "barb78916@hindenburg.ac.uk";
        String role = "Student";
        String status = "{\"email\": \"" + email + "\", \"role\": \"" + role + "\"}";


        guestController.loginTest(username,password);
        authController.logout();
        assertTrue(mockSharedContext.getCurrentUser() instanceof Guest);
    }
    @Test
    void FailedUsername()
    {
        String username = "Bar";
        String password = "I like pink muffs and I cannot lie";
        String email = "barb78916@hindenburg.ac.uk";
        String role = "Student";
        String status = "{\"email\": \"" + email + "\", \"role\": \"" + role + "\"}";


        guestController.loginTest(username,password);
        User C_user =  mockSharedContext.getCurrentUser();

        assertEquals(C_user instanceof Guest, true);
    }
    @Test
    void ValidTeacher()
    {
        String username = "JSON Derulo";
        String password = "Desrouleaux";


        guestController.loginTest(username,password);
        AuthenticatedUser C_user = (AuthenticatedUser) mockSharedContext.getCurrentUser();

        assertEquals(C_user.getEmail(), "json.d@hindenburg.ac.uk");
        assertEquals(C_user.getRole(),"TeachingStaff");
    }
}
