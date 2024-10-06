package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import model.User;
import org.apache.lucene.queryparser.classic.ParseException;
import view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.*;

import static controller.MenuController.GuestMainMenuOption.LOGIN;

public class MenuController extends Controller{

    public enum GuestMainMenuOption {
        LOGIN("Login"),
        CONSULT_FAQ("Consult Faq"),
        SEARCH_PAGES("Search Pages"),
        CONTACT_STAFF("Contact Staff");

        private String option;

        GuestMainMenuOption(String option){
            this.option = option;
        }

        public String getGuestMenuOption(){
            return option;
        }

    }

    public enum StudentMainMenuOption {
        LOGOUT("Logout"),
        CONSULT_FAQ ("Consult Faq"),
        SEARCH_PAGES("Search Pages"),
        CONTACT_STAFF("Contact Staff");

        private String option;

        StudentMainMenuOption(String option){
            this.option = option;
        }

        public String getStudentMenuOption(){
            return option;
        }
    }

    public enum TeachingStaffMainMenuOption {
        LOGOUT("Logout"),
        MANAGE_RECEIVED_INQUIRIES("Manage Received Inquiries");

        private String option;

        TeachingStaffMainMenuOption(String option){
            this.option = option;
        }

        public String getTeachingStaffmenuOption() {
            return option;
        }
    }

    public enum AdminStaffMainMenuOption {
        LOGOUT("Logout"),
        MANAGE_INQUIRIES("Manage Inquiries"),
        ADD_PAGE("Add Page"),
        SEE_ALL_PAGES("See All Pages"),
        MANAGE_FAQ("Manage Faq");

        private String option;

        AdminStaffMainMenuOption(String option){
            this.option = option;
        }

        public String getAdminStaffMenuOption(){
            return option;
        }
    }

    //private User currentUser;



    public MenuController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService); //same for all controllers, it just inherits everything from the controller class
        //this.currentUser = sharedContext.getCurrentUser();
    }

    public void mainMenu() throws IOException, ParseException {

        //check if user is authenticated
        if (sharedContext.isUserAuthenticated()) {
            AuthenticatedUser authenticatedUser = (AuthenticatedUser) sharedContext.getCurrentUser();
            switch (authenticatedUser.getRole()) {
                case "AdminStaff":
                    handleAdminStaffMainMenu();
                    break;
                case "TeachingStaff":
                    handleTeachingStaffMainMenu();
                    break;
                case "Student":
                    handleStudentMainMenu();
                    break;
            }
        } else {
            // if user is not authenticated, open guest menu
            handleGuestMainMenu();
        }
    }

    //i used array lists to store the menu options but it would need to be cleared after each use

    private boolean handleGuestMainMenu() throws IOException, ParseException {

        //switch case for each option i think
        ArrayList<String> guestOptionList = new ArrayList<>();

        //initialise the controller
        GuestController guestController = new GuestController(sharedContext, view, authenticationService, emailService);
        InquirerController inquirerController = new InquirerController(sharedContext, view, authenticationService, emailService);

        //print options and store all option in array list
        for(GuestMainMenuOption option : GuestMainMenuOption.values()){
            String optionString = option.getGuestMenuOption();
            guestOptionList.add(optionString);
        }

        boolean continueRunning = true;
        while (continueRunning) {
            int selected_option = selectFromMenu(guestOptionList, "Exit");


        switch (selected_option) {
            case 1:
                guestController.login();
                mainMenu();
                break;
            case 2:
                inquirerController.consultFAQ();
                break;
            case 3:
                inquirerController.searchPages();
                break;
            case 4:
                inquirerController.contactStaff();
                break;
            case 99:
                if (view.getYesNoInput("Are you sure you want to exit?")) {
                    continueRunning = false; // Set the flag to false to exit the loop
                }
                break;
            default:
                view.displayInfo("Invalid option selected. Please try again.");
                break;
            }
        }
        return !continueRunning;
    }

    private boolean handleStudentMainMenu() throws IOException, ParseException {

        ArrayList<String> studentOptionList = new ArrayList<>();

        //initialise the controller
        InquirerController inquirerController = new InquirerController(sharedContext, view, authenticationService, emailService);
        AuthenticatedUserController authenticatedUserController = new AuthenticatedUserController(sharedContext, view, authenticationService, emailService);

        //adds all the options to an array and displays them
        for(StudentMainMenuOption option : StudentMainMenuOption.values()){
            String optionString = option.getStudentMenuOption();
            studentOptionList.add(optionString);
        }

        boolean continueRunning = true;
        while (continueRunning) {
            int selected_option = selectFromMenu(studentOptionList, "Exit");

            switch (selected_option) {
                case 1:
                    authenticatedUserController.logout();
                    continueRunning = false;
                    break;
                case 2:
                    inquirerController.consultFAQ();
                    break;
                case 3:
                    inquirerController.searchPages();
                    break;
                case 4:
                    inquirerController.contactStaff();
                    break;
                case 99:
                    if (view.getYesNoInput("Are you sure you want to exit?")) {
                        continueRunning = false;
                    }
                    break;
                default:
                    view.displayInfo("Invalid option selected. Please try again.");
                    break;
            }
        }
        return !continueRunning;
    }

    private boolean handleTeachingStaffMainMenu() throws IOException, ParseException {

        ArrayList<String> teachingStaffOptionList = new ArrayList<>();

        //initialise the controllers
        TeachingStaffController teachingStaffController = new TeachingStaffController(sharedContext, view, authenticationService, emailService);
        AuthenticatedUserController authenticatedUserController = new AuthenticatedUserController(sharedContext, view, authenticationService, emailService);
        StaffController staffController = new StaffController(sharedContext, view, authenticationService, emailService);

        for(TeachingStaffMainMenuOption option : TeachingStaffMainMenuOption.values()){
            String optionString = option.getTeachingStaffmenuOption();
            teachingStaffOptionList.add(optionString);
        }
        boolean continueRunning = true;
        while (continueRunning) {
            int selected_option = selectFromMenu(teachingStaffOptionList, "Exit");

            switch (selected_option){
                case 1:
                    authenticatedUserController.logout();
                    continueRunning = false;
                    break;
                case 2:
                    teachingStaffController.manageReceivedInquiries();
                    break;
                case 99:
                    if (view.getYesNoInput("Are you sure you want to exit?")){
                        continueRunning = false; // Exit the loop
                    }
                    break;
                default:
                    view.displayInfo("Invalid option selected. Please try again.");
                    break;
            }
        }
        return !continueRunning;
    }

    private boolean handleAdminStaffMainMenu() throws IOException, ParseException {

        ArrayList<String> adminStaffOptionList = new ArrayList<>();

        //initialise the controllers
        AdminStaffController adminStaffController = new AdminStaffController(sharedContext, view, authenticationService, emailService);
        AuthenticatedUserController authenticatedUserController = new AuthenticatedUserController(sharedContext, view, authenticationService, emailService);
        StaffController staffController = new StaffController(sharedContext, view, authenticationService, emailService);

        for(AdminStaffMainMenuOption option : AdminStaffMainMenuOption.values()){
            String optionString = option.getAdminStaffMenuOption();
            adminStaffOptionList.add(optionString);
        }

        boolean continueRunning = true;
        while (continueRunning) {
            int selected_option = selectFromMenu(adminStaffOptionList, "Exit");

            switch (selected_option) {
                case 1:
                    authenticatedUserController.logout();
                    continueRunning = false;
                    break;
                case 2:
                    adminStaffController.manageInquiries();
                    break;
                case 3:
                    adminStaffController.addPage();
                    break;
                case 4:
                    adminStaffController.viewAllPages();
                    break;
                case 5:
                    adminStaffController.manageFAQ();
                    break;
                case 99:
                    if (view.getYesNoInput("Are you sure you want to exit?")) {
                        continueRunning = false; // Set the flag to false to exit the loop
                    }
                    break;

                default:
                    view.displayInfo("Invalid option selected. Please try again.");
                    break;
            }
        }
        return !continueRunning;
    }
}
