package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import view.View;

import java.util.Collection;
import java.util.Locale;

public class GuestController extends Controller {

    public GuestController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);
    }

    public void login(){
        //getting credentials for login
        String username = view.getInput("Enter username: ");
        String password = view.getInput("Enter password: ");
        String status = authenticationService.login(username,password);
        //getting the status of our login
        JSONParser parser = new JSONParser();

        try{
            JSONObject jsonAnswer = (JSONObject) parser.parse(status);
            if (jsonAnswer.containsKey("error")){
                view.displayError((String) jsonAnswer.get("error"));

            }
            else {
                String email = (String) jsonAnswer.get("email");
                String role = (String) jsonAnswer.get("role");
                if (email == null){
                    throw new IllegalArgumentException("User email can not be null");
                } else if ( !role.toLowerCase().equals("student") &&
                            !role.toLowerCase().equals("adminstaff") &&
                            !role.toLowerCase().equals("teachingstaff")) {
                    throw new IllegalArgumentException("Unsupported user role");
                }
                AuthenticatedUser authenticateduser = new AuthenticatedUser(email,role);
                sharedContext.CurrentUser(authenticateduser);
                view.displaySuccess("Login successful");
            }

        }
        catch (ParseException e){
            view.displayError("Unable to login, please try again");
        }
        catch (IllegalArgumentException e){
            view.displayException(e);
        }


    }
    public void loginTest(String usernameI,String passwordI){
        //getting credentials for login
        String username = usernameI;
        String password = passwordI;
        String status = authenticationService.login(username,password);
        //getting the status of our login
        JSONParser parser = new JSONParser();

        try{
            JSONObject jsonAnswer = (JSONObject) parser.parse(status);
            if (jsonAnswer.containsKey("error")){
                view.displayError((String) jsonAnswer.get("error"));

            }
            else {
                String email = (String) jsonAnswer.get("email");
                String role = (String) jsonAnswer.get("role");
                if (email == null){
                    throw new IllegalArgumentException("User email can not be null");
                } else if ( !role.toLowerCase().equals("student") &&
                        !role.toLowerCase().equals("adminstaff") &&
                        !role.toLowerCase().equals("teachingstaff")) {
                    throw new IllegalArgumentException("Unsupported user role");
                }
                AuthenticatedUser authenticateduser = new AuthenticatedUser(email,role);
                sharedContext.CurrentUser(authenticateduser);
                view.displaySuccess("Login successful");
            }

        }
        catch (ParseException e){
            view.displayError("Unable to login, please try again");
        }
        catch (IllegalArgumentException e){
            view.displayException(e);
        }


    }
}
