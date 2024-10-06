package controller;

import external.AuthenticationService;
import external.EmailService;
import model.SharedContext;
import view.View;

import java.text.NumberFormat;
import java.util.Collection;

public abstract class Controller {

    //Here so you don't need to put all the methods from other classes here
    protected SharedContext sharedContext;
    protected View view;
    protected AuthenticationService authenticationService;
    protected EmailService emailService;

    public Controller(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){

        this.sharedContext = sharedContext;
        this.view = view;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }


    protected <T> int selectFromMenu(Collection<T> menuOptions, String goBack) {

        int cancel = 99; //option to cancel (mostly set to exit the system in menu controller)

        StringBuilder option_str = new StringBuilder();

        int option_num = 1;

        //get menu options
        for (T option : menuOptions) {

            option_str.append(String.format("%d: %s\n", option_num, option));
            option_num++;
        }

        //"goBack" in this case would be whatever option you want to set it to
        if (goBack != null) {
            option_str.append(String.format("%d: %s\n", option_num, goBack));
        }

        String input = view.getInput(option_str + "Please select an option (type in num): ");

        try {
            int selected_num = Integer.parseInt(input); //get numerical input

            if (selected_num >= 1 && selected_num < option_num) {
                return selected_num; //if the num matches an option that isn't the final one then select it
            }
            if (selected_num == option_num) {
                return cancel; //if not then select that option
            }
        } catch (NumberFormatException e) {
            view.displayError("Please enter a number"); //error if you did not input a number
            throw new IndexOutOfBoundsException();
        }
        view.displayError("Please enter a number in range"); //error ir you put in a number out of range
        throw new IndexOutOfBoundsException();
    }
}
