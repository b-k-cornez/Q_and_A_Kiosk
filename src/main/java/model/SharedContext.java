package model;
import java.io.IOException;
import java.util.*;

import external.EmailService;
import org.apache.lucene.store.Directory;
import view.View;

public class SharedContext {

    //Honestly don't really know what to do here
    //I think this is for people doing FAQ and model.Page tasks
    public static String ADMIN_STAFF_EMAIL;
    private Map<String, Collection<String>> faqTopicsUpdateSubscribers = new HashMap<>();
    private Map<String, Page> pages;
    private View view;
    private EmailService emailService;
    private User currentUser;
    private PageSearch pageSearch;
    private FAQ faq;

    private Directory index;


    public SharedContext(){
        this.faqTopicsUpdateSubscribers = new HashMap<>();
        this.pages = new HashMap<>();
        this.faq = new FAQ();
        currentUser = new Guest();
    }
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void CurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isUserAuthenticated() {
        return getCurrentUser() instanceof AuthenticatedUser;
    }
    public boolean isUserGuest(){
        return getCurrentUser() instanceof Guest;
    }

    public void addPage(Page page) {
        String title = page.getTitle();
        if (pages == null) {
            pages = new HashMap<>();
        }
        // if title exists
        if (pages.containsKey(title)) {
            boolean overwrite = view.getYesNoInput("Page " + title + " already exists. Overwrite with new page?");
            if (!overwrite) {
                view.displayInfo("Cancelled adding new page.");
                return;
            }
        }
        pages.put(title, page);


        //send email
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) getCurrentUser();
        String senderEmail = authenticatedUser.getEmail();
        String emailContent = "A new page titled '" + title + "' has been added.";
        int status = emailService.sendEmail(senderEmail, ADMIN_STAFF_EMAIL, "New Page Added", emailContent);
        //success message
        if (status == EmailService.STATUS_SUCCESS) {
            view.displaySuccess("Added page '" + title + "'");
        } else {
            view.displayWarning("Added page '" + title + "' but failed to send email notification.");
        }
    }

    public Collection<Page> getPages() {
        return pages.values();
    }

    public Page getPageByTitle(String title) {
        return pages.get(title);
    }

    public void setPageSearch(PageSearch pageSearch) {
        this.pageSearch = pageSearch;
    }

    public PageSearch getPageSearch() {
        return this.pageSearch;
    }


    public User getCurrentUser() {
        return this.currentUser;
    }
    public void setCurrentUser(User user) {this.currentUser= user;}


    public FAQ getFAQ() {
        return this.faq;
    }
    public void setFAQ(FAQ faq) {this.faq = faq;}

    public Collection<String> usersSubscribedToFAQTopics(String topic) {
        return this.faqTopicsUpdateSubscribers.getOrDefault(topic, new ArrayList<>());
    }

    public Boolean registerForFAQUpdates(String email, String topic) {
        if (!this.faqTopicsUpdateSubscribers.containsKey(topic)) {
            this.faqTopicsUpdateSubscribers.put(topic, new ArrayList<>());
        }
        Collection<String> subscribers = this.faqTopicsUpdateSubscribers.get(topic);
        if (!subscribers.contains(email)) {
            subscribers.add(email);
            return true;
        }
        return false;
    }

    public boolean unregisterForFAQUpdates(String email, String topic) {
        if (this.faqTopicsUpdateSubscribers.containsKey(topic)) {
            Collection<String> subscribers = this.faqTopicsUpdateSubscribers.get(topic);
            return subscribers.remove(email);
        }
        return false;
    }

    public void setPages(Map<String, Page> pages) {
        this.pages = pages;
    }
}
