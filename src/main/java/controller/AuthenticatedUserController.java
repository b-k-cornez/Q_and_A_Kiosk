package controller;

import external.AuthenticationService;
import external.EmailService;
import model.AuthenticatedUser;
import model.Guest;
import model.SharedContext;
import view.View;

import java.util.Collection;

public class AuthenticatedUserController extends Controller{

    public AuthenticatedUserController(SharedContext sharedContext, View view, AuthenticationService authenticationService, EmailService emailService){
        super(sharedContext, view, authenticationService, emailService);
    }

    public void logout(){
        sharedContext.CurrentUser(new Guest());
        view.displaySuccess("Logout successful");

    }
}
