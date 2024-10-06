package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.Inquiry;
import model.SharedContext;
import extra.StoreInquiry;
import view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class StaffController extends Controller{

    public StaffController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);
    }

    //may need to edit as the UML lays it out differently
    public ArrayList<String> getInquiryTitles(){

        StoreInquiry storeInquiry = StoreInquiry.getInstance();

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) sharedContext.getCurrentUser();

        if (authenticatedUser.getRole().equals("AdminStaff")){

            ArrayList<Inquiry> inquiries = storeInquiry.getUnansweredInquiries();

            ArrayList<String> subjectList = new ArrayList<>();

            for (Inquiry inquiry: inquiries) {
                String subject = inquiry.getSubject();
                subjectList.add(subject);
            }

            return subjectList;

        } else {

            ArrayList<Inquiry> inquiries = storeInquiry.getRedirectedInquiries();

            ArrayList<String> subjectList = new ArrayList<>();

            for (Inquiry inquiry: inquiries) {
                if (inquiry.getAssignedTo().equals(authenticatedUser.getEmail())){
                    String subject = inquiry.getSubject();
                    subjectList.add(subject);
                }

            }

            return subjectList;
        }

    }

    public void respondToInquiry(Inquiry inquiry) {

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) sharedContext.getCurrentUser();
        StoreInquiry storeInquiry = StoreInquiry.getInstance();

        if (authenticatedUser.getRole().equals("AdminStaff")) {

            String senderEmail = SharedContext.ADMIN_STAFF_EMAIL;
            String recipientEmail = inquiry.getInquirerEmail();
            String subject = inquiry.getInquirerEmail();
            String content = view.getInput("Enter content of response: ");

            if (content.isBlank()){
                view.displayError("Please enter a response");
                respondToInquiry(inquiry);
            }
            emailService.sendEmail(senderEmail, recipientEmail, subject, content);
            view.displaySuccess("Email successfully sent!");

            storeInquiry.removeInquiry(inquiry);

        } else {

            String senderEmail = authenticatedUser.getEmail();
            String recipientEmail = inquiry.getInquirerEmail();
            String subject = inquiry.getInquirerEmail();
            String content = view.getInput("Enter content of response: ");

            if (content.isBlank()){
                view.displayError("Please enter a response");
                respondToInquiry(inquiry);
            }

            emailService.sendEmail(senderEmail, recipientEmail, subject, content);
            view.displaySuccess("Email successfully sent!");

            storeInquiry.removeRedirectedInquiry(inquiry);
        }
    }

}
