package view;

import model.FAQ;
import model.FAQSection;
import model.Inquiry;
import model.PageSearchResult;

import java.util.Collection;

public interface View {
    //Part of other tasks
    //Fill as needed
    String getInput(String prompt);
    boolean getYesNoInput(String prompt);
    void displayInfo(String message);
    void displaySuccess(String message);
    void displayWarning(String message);
    void displayError(String message);
    void displayException(Exception exception);
    void displayDivider();
    void displaySearchResults(Collection<PageSearchResult> searchResults);

    void displayFAQ(FAQ faq, Boolean includeSection);

    void displayFAQSection(FAQSection faqSection, Boolean isGuest);

    void displayInquiry(Inquiry inquiry);
}
