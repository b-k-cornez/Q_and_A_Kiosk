package extra;

import model.Inquiry;

import java.util.ArrayList;

//used the singleton method to allow for all classes to use it
public class StoreInquiry {

    private static StoreInquiry storeInquiry = new StoreInquiry();
    private ArrayList<Inquiry> unansweredInquiries = new ArrayList<>();

    private ArrayList<Inquiry> redirectedInquiries = new ArrayList<>();

    public static StoreInquiry getInstance(){
        return storeInquiry;
    }
    public void storeUnansweredInquiries(Inquiry inquiry){
        unansweredInquiries.add(inquiry);
    }
    public void storeRedirectedEnquiries(Inquiry inquiry){
        redirectedInquiries.add(inquiry);
    }
    public ArrayList<Inquiry> getUnansweredInquiries(){
        return unansweredInquiries;
    }
    public ArrayList<Inquiry> getRedirectedInquiries(){return redirectedInquiries;}

    public void removeInquiry(Inquiry inquiry){
        unansweredInquiries.remove(inquiry);
    }
    public void removeRedirectedInquiry(Inquiry inquiry){redirectedInquiries.remove(inquiry);}

    public Inquiry getInquiryBySubject(String subject) {
        for (Inquiry inquiry : unansweredInquiries) {
            if (inquiry.getSubject().equals(subject)) {
                return inquiry;
            }
        }
        return null; // Return null if no inquiry with the specified subject is found
    }

    public Inquiry getRedirectedBySubject(String subject){
        for (Inquiry inquiry : redirectedInquiries){
            if (inquiry.getSubject().equals(subject)){
                return inquiry;
            }
        }
        return null;
    }
}
