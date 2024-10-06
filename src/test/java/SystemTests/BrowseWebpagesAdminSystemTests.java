package SystemTests;

import controller.AdminStaffController;
import external.AuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.Page;
import model.SharedContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import view.View;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BrowseWebpagesAdminSystemTests {
    @Mock
    private View mockView;
    @Mock
    private AuthenticationService mockAuthService;
    @Mock
    private MockEmailService mockEmailService;
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
    void testViewAllPages() {
        //data of pages
        Page page1 = new Page("Title1", "Content1", false);
        Page page2 = new Page("Title2", "Content2", false);
        Collection<Page> pages = Arrays.asList(page1, page2);

        when(mockSharedContext.getPages()).thenReturn(pages);

        // Call the method
        adminStaffController.viewAllPages();

        // verify
        verify(mockView).displayInfo("Title: " + page1.getTitle());
        verify(mockView).displayInfo("Content: " + page1.getContent());
        verify(mockView).displayInfo("Title: " + page2.getTitle());
        verify(mockView).displayInfo("Content: " + page2.getContent());

        // verify times
        verify(mockView, times(2)).displayInfo(startsWith("Title: "));
        verify(mockView, times(2)).displayInfo(startsWith("Content: "));
    }
}
