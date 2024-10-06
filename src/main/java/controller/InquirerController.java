package controller;

import external.AuthenticationService;
import external.EmailService;
import extra.StoreInquiry;
import model.*;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InquirerController extends Controller{
    private FAQSection currentSection;
    private PageSearch pageSearch;

    public InquirerController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService); //same for all controllers, it just inherits everything from the controller class
    }

public int getInputOption(String prompt) {
        while (true) {
            try {
                // successful case
                String input = view.getInput(prompt);
                int optionNo = Integer.parseInt(input);
                return optionNo;
                // catch errors
            } catch (NumberFormatException e) {
                view.displayError("Invalid input. Please enter a numeric value.");
            }
        }
    }
    private String requestUserEmail() {
        view.displayInfo("Please enter your email address:");
        return view.getInput("");
    }
    public void consultFAQ() {
        currentSection = null;
        User currentUser = sharedContext.getCurrentUser();
        String userEmail = null;
        Collection<String> subscribers;
        FAQ faq = sharedContext.getFAQ();

        int optionNo = 0;
        if (currentUser instanceof AuthenticatedUser) {
            userEmail = ((AuthenticatedUser) currentUser).getEmail();
        }

        do {
            if (currentSection == null) {
                view.displayFAQ(faq, currentUser instanceof Guest);
                view.displayInfo("[-1] to return to main menu");

                optionNo = getInputOption("Please choose an option:");

                if (optionNo == -1) {
                    break;
                } else if (optionNo < 1 || optionNo > faq.getSections().size()) {
                    view.displayError("Invalid option: " + optionNo);
                } else {
                    currentSection = new ArrayList<>(faq.getSections()).get(optionNo - 1);
                }

                if (optionNo != -1 && optionNo != -2 && optionNo != -3) {
                    List<FAQSection> faqSections;
                    faqSections = new ArrayList<>(faq.getSections());
                }


            } else {
                view.displayFAQSection(currentSection, currentUser instanceof Guest);
                FAQSection parent = currentSection.getParent();
                if (parent == null) {
                    view.displayInfo("[-1] to return to FAQ");
                } else {
                    String topic = parent.getTopic();
                    view.displayInfo("[-1] to return to" + topic);
                }
                subscribers = sharedContext.usersSubscribedToFAQTopics(currentSection.getTopic());

                if (!subscribers.contains(userEmail)) {
                    view.displayInfo("[-2] to request updates for this topic");
                } else if (subscribers.contains(userEmail)) {
                    view.displayInfo("[-2] to stop receiving updates for this topic");
                }

                optionNo = getInputOption("Please choose an option:");
                if (optionNo >= 1) {
                    List<FAQSection> faqSections = new ArrayList<>(currentSection.getSubsections());
                    if (optionNo <= faqSections.size()) {
                        currentSection = faqSections.get(optionNo - 1);
                    } else {
                        view.displayError("Invalid option: " + optionNo);
                        optionNo = 0;
                    }
                } else if (optionNo == -1) {
                    currentSection = currentSection.getParent();
                } else if (optionNo == -2) {
                    if (userEmail == null) {
                        userEmail = requestUserEmail();
                    }
                    if (!(subscribers.contains(userEmail))) {
                        requestFAQUpdates(userEmail, currentSection.getTopic());
                    } else {
                        stopFAQUpdates(userEmail, currentSection.getTopic());
                    }
                }
            }
        } while (optionNo != -1 || currentSection != null) ; // Ensure we can navigate back up the FAQ sections
    }

    public void searchPages() throws IOException, ParseException {
        String searchQuery = view.getInput("Enter your search query: ");
        PageSearch pageSearch = new PageSearch(sharedContext, view);
        Collection<PageSearchResult> searchResults = pageSearch.search(searchQuery, sharedContext.isUserAuthenticated());
        view.displaySearchResults(searchResults);
    }
    
    public void contactStaff(){

        User currentUser = sharedContext.getCurrentUser();

        //get inquiry subject and content from enquirer
        String subject = view.getInput("Enter subject of inquiry:");
        String content = view.getInput("Enter content of inquiry:");
        String email = null;

        //i think getRole()  will be better so it can see if they're a logged in user or not

        if (currentUser instanceof AuthenticatedUser authenticatedUser) {
            // User is authenticated
            email = authenticatedUser.getEmail();
        } else {
            // User is not authenticated (guest user)
            email = view.getInput("Enter your email address: ");
        }


        //inquiry is saved
        Inquiry inquiry = new Inquiry(subject, content, email);

        //success massage is displayed
        view.displaySuccess("Inquiry successfully submitted");

        //admin staff email
        String adminStaff = SharedContext.ADMIN_STAFF_EMAIL;

        //send inquiry to general admin staff
        emailService.sendEmail(email, adminStaff, subject, content);

        //notification
        String other_subject = "New inquiry received";
        String notification = "Please log in to see inquiry";
        emailService.sendEmail(email, adminStaff, other_subject, notification);

        //assign inquiry to admin staff
        inquiry.setAssignedTo("Admin Staff");

        //stores the inquiries in ArrayList
        StoreInquiry storeInquiry = StoreInquiry.getInstance();
        storeInquiry.storeUnansweredInquiries(inquiry);
    }

    private void requestFAQUpdates(String email, String topic){
        String userEmail = email;
        Boolean success = sharedContext.registerForFAQUpdates(userEmail, topic);

        if (success) {
            view.displaySuccess("Successfully registered " + userEmail + " for updates on " + topic);
        } else {
            view.displayError("Failed to register " + userEmail + " for updates on " + topic + ". Perhaps this email was already registered?");
        }
    }


    private void stopFAQUpdates(String email, String topic){
        String userEmail = email;
        Boolean success = sharedContext.unregisterForFAQUpdates(userEmail, topic);

        if (success) {
            view.displaySuccess("Successfully unregistered " + userEmail + " for updates on " + topic);
        } else {
            view.displayError("Failed to unregister " + userEmail + " for updates on " + topic + ". Perhaps this email was already not registered?");
        }
    }

}

