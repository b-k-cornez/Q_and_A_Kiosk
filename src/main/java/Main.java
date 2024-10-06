import external.AuthenticationService;
import external.EmailService;
import external.MockAuthenticationService;
import external.MockEmailService;
import controller.*;
import extra.StoreInquiry;
import model.Inquiry;
import model.PageSearch;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import view.TextUserInterface;
import view.View;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args){

        try {


            View view = new TextUserInterface();
            AuthenticationService authenticationService = new MockAuthenticationService();
            EmailService emailService = new MockEmailService() ;
            SharedContext sharedContext = new SharedContext();
            PageSearch pageSearch = new PageSearch(sharedContext,view);
            sharedContext.setEmailService(emailService);
            sharedContext.setView(view);
            sharedContext.setPageSearch(pageSearch);

            //testing storing inquiries
            StoreInquiry storeInquiry = StoreInquiry.getInstance();
            String sender = "barb78916@hindenburg.ac.uk";
            String recipient = SharedContext.ADMIN_STAFF_EMAIL;
            String subject = "nothing";
            String content = "blank";
            Inquiry inquiry = new Inquiry(subject, content, sender);
            emailService.sendEmail(sender, recipient, subject, content);
            storeInquiry.storeUnansweredInquiries(inquiry);


            MenuController menuController = new MenuController(sharedContext, view, authenticationService, emailService);
            menuController.mainMenu();


        } catch (IOException | URISyntaxException e) {

        } catch (ParseException | org.apache.lucene.queryparser.classic.ParseException e) {
            throw new RuntimeException(e);
        }


    }


}
