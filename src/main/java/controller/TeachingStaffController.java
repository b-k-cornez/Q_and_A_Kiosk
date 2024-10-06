package controller;

import external.AuthenticationService;
import external.EmailService;
import extra.StoreInquiry;
import model.AuthenticatedUser;
import model.Inquiry;
import model.SharedContext;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

import java.io.IOException;
import java.util.ArrayList;

public class TeachingStaffController extends StaffController {

    public TeachingStaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);

    }

    public void manageReceivedInquiries() throws IOException, ParseException {

        StoreInquiry storeInquiry = StoreInquiry.getInstance();
        MenuController menuController = new MenuController(sharedContext, view, authenticationService, emailService);

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) sharedContext.getCurrentUser();

        ArrayList<String> inquiryTitles = getInquiryTitles();
        StringBuilder titlesString = new StringBuilder();

        Inquiry selectedInquiry = null;
        int num = 1;
        for (String title : inquiryTitles) {
            titlesString.append(String.format("%d: %s\n", num++, title));
        }
        view.displayInfo(titlesString.toString());

        ArrayList<Inquiry> list = storeInquiry.getRedirectedInquiries();
        ArrayList<Inquiry> filteredList = new ArrayList<>();

        //filter the inquiry list to only show inquiries that are assigned to current user
        for (Inquiry inquiries : list){
            if (inquiries.getAssignedTo().equals(authenticatedUser.getEmail())){
                filteredList.add(inquiries);
            }
        }

        String input = view.getInput("Please enter the subject of the inquiry: ");

        //get list of
        for (Inquiry subject : filteredList){
            if (subject.getSubject().equals(input) && subject.getAssignedTo().equals(authenticatedUser.getEmail())){
                selectedInquiry = subject;
                break;
            }
        }

        if (selectedInquiry == null){
            view.displayError("Inquiry not found");

        } else {

            //display the inquiry
            view.displayInquiry(selectedInquiry);

            boolean yes_or_no = view.getYesNoInput("Would you like to respond to this?");

            if(yes_or_no == true){

                //if they respond to the inquiry then remove from list of unanswered inquiries
                respondToInquiry(selectedInquiry);
                storeInquiry.removeInquiry(selectedInquiry);

                if(view.getYesNoInput("Would you like to do something else?")){
                    menuController.mainMenu();
                } else{
                    System.exit(0);
                }

            } else {

                if(view.getYesNoInput("Would you like to do something else?")){
                    menuController.mainMenu();
                } else{
                    System.exit(0);
                }

            }
        }


    }
}
