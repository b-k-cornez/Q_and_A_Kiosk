package SystemTests;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import model.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.lucene.queryparser.classic.ParseException;
import java.io.IOException;
import java.util.*;

import model.PageSearch;
import model.SharedContext;
import model.PageSearchResult;
import view.View;


public class ConsultWebpagesSystemTests {
    private SharedContext mocksharedContext;
    private PageSearch mockPageSearch;
    private View mockView;
    private View view;
    private PageSearch pageSearch;
    private SharedContext sharedContext;
    private Map<String, Page> pages;

    @BeforeEach
    public void setUp() throws IOException, ParseException {
        mockPageSearch = mock(PageSearch.class);
        mockView = mock(View.class);
        view = mock(View.class);
        mocksharedContext = mock(SharedContext.class);
        sharedContext = new SharedContext();

        Map<String, Page> pages = new HashMap<>();
        pages.put("Page 1", new Page("Page 1", "This is content for test page 1", false));
        pages.put("Page 2", new Page("Page 2", "This is content for test page 2", true));

        sharedContext.setPages(pages);

        pageSearch = new PageSearch(sharedContext, view);

        when(mocksharedContext.getPageSearch()).thenReturn(mockPageSearch);

        mocksharedContext.setPageSearch(mockPageSearch);
        mocksharedContext.setView(mockView);


        //A private page and a public page
        when(mocksharedContext.getPages()).thenReturn(Arrays.asList(
                new Page("Page 1", "This is content for test page 1", false),
                new Page("Page 2", "This is content for test page 2", true)
        ));
    }

    @Test
    public void testMockSearchResults() throws IOException, ParseException {
        // when user is not authenticated
        when(mockPageSearch.search("Page", false)).thenReturn(Collections.singletonList(new PageSearchResult("Page 1")));
        Collection<PageSearchResult> results = mockPageSearch.search("Page", false);

        assertEquals(1, results.size(), "only 1 search result");
        for (PageSearchResult result : results) {
            assertEquals("Page 1", result.getFormattedContent());
        }

        // when user is authenticated

        List<PageSearchResult> searchResults = Arrays.asList(
                new PageSearchResult("Page 1"),
                new PageSearchResult("Page 2") // Assuming "Page 2" is the title of the second result
        );
        when(mockPageSearch.search("Page", true)).thenReturn(searchResults);
        Collection<PageSearchResult> authResults = mockPageSearch.search("Page", true);
        assertEquals(2, authResults.size(), "2 search results");
    }

    @Test
    public void testSearchFunctions() throws IOException, ParseException {
        // When user is not authenticated
        Collection<PageSearchResult> results = pageSearch.search("Page 1", false);
        assertEquals(1, results.size(), "only 1 search result");
        System.out.println(results);


        // When user is authenticated
        results = pageSearch.search("Page", true);
        assertEquals(2, results.size(), "2 search results");
        System.out.println(results);

        // When only search page 1
        results = pageSearch.search("1", true);
        assertEquals(1, results.size(), "only 1 search result");
        System.out.println(results);

        // No results
        results = pageSearch.search("abc", true);
        assertEquals(0, results.size(), "0 result");
        System.out.println(results);
    }
}
