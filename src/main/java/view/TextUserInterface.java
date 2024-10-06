package view;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collection;

public class TextUserInterface implements View{
    private Scanner scanner;
    public TextUserInterface() {
        this.scanner = new Scanner(System.in);
    }
    public String getInput(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }
    @Override
    public boolean getYesNoInput(String prompt) {
        String input;
        do {
            System.out.println(prompt + " (y/n)");
            input = scanner.nextLine().trim().toLowerCase();
        } while (!input.equals("y") && !input.equals("n"));
        return input.equals("y");
    }
    @Override
    public void displayInfo(String info) {
        System.out.println(info);
    }
    @Override
    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }
    @Override
    public void displayWarning(String warning) {
        System.out.println("WARNING: " + warning);
    }
    @Override
    public void displayError(String error) {
        System.out.println("ERROR: " + error);
    }
    @Override
    public void displayException(Exception exception) {
        System.out.println("EXCEPTION: " + exception.getMessage());
        exception.printStackTrace();
    }
    @Override
    public void displayDivider() {
        System.out.println("--------------------------------------------------");
    }
    @Override
    public void displaySearchResults(Collection<PageSearchResult> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            System.out.println("No results found.");
            return;
        }
        System.out.println("Search Results:");
        for (PageSearchResult result : searchResults) {
            System.out.println(result.getFormattedContent());
            displayDivider();
        }
    }


    @Override
    public void displayFAQ(FAQ faq, Boolean isGuest) {
        this.displayDivider();
        this.displayInfo("FAQ:");
        if (faq != null && faq.getSections() != null) {
            List<FAQSection> faqSections = new ArrayList<>(faq.getSections());
            for (int i = 0; i < faqSections.size(); i++) {
                FAQSection faqSection = faqSections.get(i);
                this.displayInfo("[" + (i + 1) + "] " + faqSection.getTopic());
            }
        }
        this.displayDivider();
    }


    @Override
    public void displayFAQSection(FAQSection faqSection, Boolean isGuest) {
        System.out.println("Current Topic: " + faqSection.getTopic());

        List<FAQSection> subsections = new ArrayList<>(faqSection.getSubsections());

        List<FAQItem> faqItems = new ArrayList<>(faqSection.getItems());
        if (!faqItems.isEmpty()) {
            System.out.println("Questions and Answers:");
            for (FAQItem faqItem : faqItems) {
                System.out.println("Q: " + faqItem.getQuestion());
                System.out.println("A: " + faqItem.getAnswer());
                System.out.println();
            }
        }

        if (!subsections.isEmpty()) {
            System.out.println("Subtopics:");
            for (int i = 0; i < subsections.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + subsections.get(i).getTopic());
            }
        }

    }


    @Override
    public void displayInquiry(Inquiry inquiry){
       String subject = inquiry.getSubject();
       String content =  inquiry.getContent();
       String email = inquiry.getInquirerEmail();

        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);
        System.out.println("Email: " + email);

    }


}
