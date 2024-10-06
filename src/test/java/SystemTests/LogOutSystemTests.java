package SystemTests;
import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.*;
import controller.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.View;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogOutSystemTests {


    private View mockView;

    private ByteArrayOutputStream testOut;


    private AuthenticationService mockAuthService;

    private EmailService mockEmailService;

    private SharedContext mockSharedContext;

    private GuestController guestController;
    private AuthenticatedUserController authController;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException, ParseException, org.json.simple.parser.ParseException {
        mockAuthService = new MockAuthenticationService();
        mockEmailService = new MockEmailService();
        mockSharedContext = new SharedContext();
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        mockView = mock(View.class);

        guestController = new GuestController(mockSharedContext,mockView,mockAuthService,mockEmailService);
        authController = new AuthenticatedUserController(mockSharedContext,mockView,mockAuthService,mockEmailService);
    }
    @Test
    public void logout(){
        String username = "Barbie";
        String password = "I like pink muffs and I cannot lie";
        String email = "barb78916@hindenburg.ac.uk";
        String role = "Student";
        //inputting username and password each time for logging process
        when(mockView.getInput("Enter username: ")).thenReturn(username);
        when(mockView.getInput("Enter password: ")).thenReturn(password);
        //login with those details
        guestController.login();
        authController.logout();
        //checking if login was successful and then logout successful as well
        verify(mockView, atLeastOnce()).displaySuccess("Login successful");
        verify(mockView, atLeastOnce()).displaySuccess("Logout successful");
        //checking if current user is authenticated

        assertEquals(mockSharedContext.getCurrentUser() instanceof Guest,true);
    }
}