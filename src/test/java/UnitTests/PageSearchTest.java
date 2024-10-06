package UnitTests;

import static org.junit.jupiter.api.Assertions.*;

import model.Page;
import model.PageSearch;
import model.PageSearchResult;
import model.SharedContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

public class PageSearchTest {

    private PageSearch pageSearch;
    private SharedContext sharedContext;
    private View view;

    @BeforeEach
    public void setUp() throws IOException {
        // Set up the context with pages
        sharedContext = Mockito.mock(SharedContext.class);
        view = Mockito.mock(View.class);

        // Creating a list of pages
        List<Page> pages = new ArrayList<>();
        pages.add(new Page("Page1", "Content for Page1", false));
        pages.add(new Page("PrivatePage", "Content for Private Page", true));

        Mockito.when(sharedContext.getPages()).thenReturn(pages);

        Mockito.when(sharedContext.getPageByTitle("Page1")).thenReturn(pages.get(0));
        Mockito.when(sharedContext.getPageByTitle("PrivatePage")).thenReturn(pages.get(1));

        pageSearch = new PageSearch(sharedContext, view);
    }

    @Test
    @DisplayName("Searching for a public page should return results")
    public void testSearchPublicPage() throws IOException, ParseException {
        Collection<PageSearchResult> results = pageSearch.search("Page1", true);

        assertEquals(1, results.size(), "Should return exactly 1 result for a public page");
    }

    @Test
    @DisplayName("Searching for a private page as an unauthenticated user should return no results")
    public void testSearchPrivatePageUnauthenticated() throws IOException, ParseException {
        Collection<PageSearchResult> results = pageSearch.search("PrivatePage", false);

        assertTrue(results.isEmpty(), "Should not return results for a private page when unauthenticated");
    }

    @Test
    @DisplayName("Searching for a private page as an authenticated user should return results")
    public void testSearchPrivatePageAuthenticated() throws IOException, ParseException {
        Collection<PageSearchResult> results = pageSearch.search("PrivatePage", true);

        assertEquals(1, results.size(), "Should return results for a private page when authenticated");
    }

}