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

public class AdminStaffController extends StaffController {

    public AdminStaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService) {
        super(sharedContext, view, authenticationService, emailService);
    }
    public void addPage() {
        String title = view.getInput("Enter page title:");
        String content = view.getInput("Enter page content:");
        boolean isPrivate = view.getYesNoInput("Should this page be private?");
        Page page = new Page(title, content, isPrivate);
        sharedContext.addPage(page);
    }
    
    public void manageFAQ() {
        FAQ faq = sharedContext.getFAQ();
        FAQSection currentSection = null;
        String input;
        int option;

        do {
            if (currentSection == null) {
                displayRootFAQSections(faq);
                input = view.getInput("Choose a section or action:");

                if (input == null || input.trim().isEmpty()) {
                    view.displayError("No input received. Exiting.");
                    break;
                }

                // check if the user wants to add a new FAQ item at the root level
                input = input.trim();
                if ("add".equalsIgnoreCase(input)) {
                    addFAQItem(null);
                    continue;
                }

                try {
                    List<FAQSection> sections = new ArrayList<>(faq.getSections());
                    option = Integer.parseInt(input) - 1;
                    if (option >= 0 && option < sections.size()) {
                        currentSection = sections.get(option);
                    } else if (option == -2) {
                        break;
                    } else {
                        view.displayError("Invalid option. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    view.displayError("Invalid input. Please enter a valid number.");
                }
            } else {
                // display subsections of the current section for navigation
                displayFAQSubsections(currentSection);
                input = view.getInput("Choose an option or action:");

                if (input == null || input.trim().isEmpty()) {
                    view.displayError("No input received. Exiting.");
                    break;
                }

                input = input.trim();
                switch (input) {
                    case "0":
                        // navigate back to parent section
                        currentSection = currentSection.getParent();
                        break;
                    case "-1":
                        // to exit
                        return;
                    case "add":
                        //inset the new FAQ item under current section
                        addFAQItem(currentSection);
                        break;
                    default:
                        option = parseOption(input, currentSection.getSubsections().size());
                        if (option >= 0) {
                            currentSection = currentSection.getSubsections().get(option);
                        }
                        break;
                }
            }
        } while (true);
    }

    private int parseOption(String input, int size) {
        try {
            int option = Integer.parseInt(input) - 1;
            if (option >= 0 && option < size) {
                return option;
            } else if (input.equals("-1")) {
                // special case to exit
                return -2;
            } else {
                view.displayError("Invalid option. Please try again.");
                return -1;
            }
        } catch (NumberFormatException e) {
            view.displayError("Invalid input. Please enter a valid number.");
            return -1;
        }
    }
    
    private void displayRootFAQSections(FAQ faq) {
        List<FAQSection> sections = new ArrayList<>(faq.getSections());
        for (int i = 0; i < sections.size(); i++) {
            view.displayInfo("[" + (i + 1) + "] " + sections.get(i).getTopic());
        }
        view.displayInfo("[-1] Exit");
        view.displayInfo("[add] Add new Q&A pair at the root level or create a new topic");
    }

    private void displayFAQSubsections(FAQSection currentSection) {
        view.displayFAQSection(currentSection, false);
        view.displayInfo("[0] Go up");
        view.displayInfo("[-1] Exit");
        view.displayInfo("[add] Add new Q&A pair to this topic");
    }

    private void addFAQItem(FAQSection section) {
        // get input from admin
        String question = view.getInput("Enter the question:");
        String answer = view.getInput("Enter the answer:");

        // neither fields can be empty, check
        if (question.trim().isEmpty() || answer.trim().isEmpty()) {
            view.displayError("FAQ item not added as no input was given.");
            return;
        }

        FAQSection currentSection;
        // create a new section if no section is indicated
        if (section != null) {
            boolean makeSubtopic = view.getYesNoInput("Create new subtopic? (yes/no): \n");
            if (makeSubtopic) {
                String subtopic = view.getInput("Enter the subtopic name for the FAQ:");
                if (subtopic.trim().isEmpty()) {
                    view.displayError("Subtopic name cannot be empty. FAQ item not added.");
                    return;
                }

                // if subtopic is present, overwrite it
                FAQSection sameSubtopic = section.getSubsections().stream()
                        .filter(s -> s.getTopic().equalsIgnoreCase(subtopic)).findFirst().orElse(null);

                if (sameSubtopic != null) {
                    view.displayWarning("This subtopic already exists. Editing a present subtopic.");
                    currentSection = sameSubtopic;
                } else {
                    currentSection = new FAQSection(subtopic);
                    section.addSubsection(currentSection);
                }
            } else {
                currentSection = section;
            }
        } else {
            // get input about the big topics
            String topic = view.getInput("Enter the topic for the new FAQ:");

            if (topic.trim().isEmpty()) {
                view.displayError("FAQ item not added as there is no topic name.");
                return;
            }

            // if same topic is present, overwrite it
            FAQSection sameTopic = sharedContext.getFAQ().getSections().stream()
                    .filter(s -> s.getTopic().equalsIgnoreCase(topic)).findFirst().orElse(null);

            if (sameTopic == null) {
                currentSection = new FAQSection(topic);
                sharedContext.getFAQ().addSection(currentSection);
            } else {
                view.displayWarning("This topic already exists. Editing a present FAQ");
                currentSection = sameTopic;
            }
        }

        currentSection.addItem(question, answer);
        view.displaySuccess("FAQ item added successfully.");

        // prepare email notifications
        if (sharedContext.getCurrentUser() instanceof AuthenticatedUser) {
            String FAQs = currentSection.getItems().stream()
                    .map(item -> "Question: " + item.getQuestion() + "\nAnswer: " + item.getAnswer())
                    .reduce("", (acc, pair) -> acc + pair + "\n\n");

            String userEmail = ((AuthenticatedUser) sharedContext.getCurrentUser()).getEmail();

            String emailContent = "A new FAQ entry was added to the topic: " + currentSection.getTopic() + "\n" + FAQs;

            // send to admin
            emailService.sendEmail(userEmail, sharedContext.ADMIN_STAFF_EMAIL, "FAQ Update Notification", emailContent);

            // send to subscribers
            sharedContext.usersSubscribedToFAQTopics(currentSection.getTopic()).forEach(subscriberEmail -> {
                emailService.sendEmail(sharedContext.ADMIN_STAFF_EMAIL, subscriberEmail,
                        "FAQ Topic Updated: " + currentSection.getTopic(), emailContent);
            });

        } else {
            view.displayError("Cannot send email notification as User is not authenticated.");
        }
    }


    public void viewAllPages() {
        Collection<Page> allPages = sharedContext.getPages();
        for (Page page : allPages) {
            view.displayInfo("Title: " + page.getTitle());
            view.displayInfo("Content: " + page.getContent());
        }
    }

    //no clue what to put here, but i think it has to do with the main menu controller
    public void manageInquiries() throws IOException, ParseException {

        StoreInquiry storeInquiry = StoreInquiry.getInstance();
        MenuController menuController = new MenuController(sharedContext, view, authenticationService, emailService);


        ArrayList<String> inquiryTitles = getInquiryTitles();
        StringBuilder titlesString = new StringBuilder();

        Inquiry selectedInquiry = null;
        int num = 1;
        for (String title : inquiryTitles) {
            titlesString.append(String.format("%d: %s\n", num++, title));
        }
        view.displayInfo(titlesString.toString());

        ArrayList<Inquiry> list = storeInquiry.getUnansweredInquiries();

        String input = view.getInput("Please enter the subject of the inquiry: ");

        for (Inquiry subject : list){ // select the inquiry you want
            if (subject.getSubject().equals(input)){
                selectedInquiry = subject;
                break;
            }
        }

        if (selectedInquiry == null){
            view.displayError("Inquiry not found");

            if(view.getYesNoInput("Would you like to do something else?")){
                menuController.mainMenu();
            } else{
                System.exit(0);
            }

        } else { //respond to inquiry

            //display the inquiry
            view.displayInquiry(selectedInquiry);

            boolean yes_or_no = view.getYesNoInput("Would you like to respond to this?");

            if(yes_or_no == true){

                //if they respond to the inquiry then remove from list of unanswered inquiries
                respondToInquiry(selectedInquiry);

                if(view.getYesNoInput("Would you like to do something else?")){
                    menuController.mainMenu();
                } else{
                    System.exit(0);
                }

            } else { //redirect inquiry

               boolean choice = view.getYesNoInput("Would you like to redirect this enquiry?");

               if (choice){
                   redirectInquiry(selectedInquiry);//redirect the inquiry
                   storeInquiry.removeInquiry(selectedInquiry);

                   if(view.getYesNoInput("Would you like to do something else?")){
                       menuController.mainMenu();
                   }
               } else { //do nothing

                   if(view.getYesNoInput("Would you like to do something else?")){
                       menuController.mainMenu();
                   } else{
                       System.exit(0);
                   }
               }
            }
        }
    }

    public void redirectInquiry(Inquiry inquiry){

        //params to send inquiry to staff member
        String recipient = view.getInput("Please enter email address: ");
        String sender = sharedContext.ADMIN_STAFF_EMAIL;
        String subject = inquiry.getInquirerEmail();
        String content = inquiry.getContent();

        //uses emailService to do so
        emailService.sendEmail(sender, recipient, subject, content);

        //assigns the inquiry to a member of staff
        inquiry.setAssignedTo(recipient);

        //notification email
        String notification_content = "New inquiry received. Please log in to view";
        String notification_sender = SharedContext.ADMIN_STAFF_EMAIL;

        emailService.sendEmail(notification_sender, recipient, sender, notification_content);

        //move to collection of teaching staff inquiries
        StoreInquiry storeInquiry = StoreInquiry.getInstance();
        storeInquiry.storeRedirectedEnquiries(inquiry);
        storeInquiry.removeInquiry(inquiry);

        view.displaySuccess("Inquiry successfully redirected!");
    }
}

