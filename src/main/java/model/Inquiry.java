package model;

import java.time.LocalDateTime; //might need to import through maven idk
import java.util.Optional;

public class Inquiry {

    private LocalDateTime createdAt;
    private String inquirerEmail;
    private String subject;
    private String content;
    private String assignedTo;

    //need to add getter methods for all the attributes (dunno how strict I have to follow the uml)
    public String getSubject(){
        return subject;
    }
    public String getContent(){
        return content;
    }
    public String getInquirerEmail(){
        return inquirerEmail;
    }

    public String getAssignedTo(){
        return assignedTo;
    }

    public void setAssignedTo(String assignment){
        //this.assignedTo = String.valueOf(Optional.ofNullable(assignedTo));
        this.assignedTo = assignment;

    }

    public Inquiry(String subject, String content, String inquirerEmail){
        this.subject = subject;
        this.content = content;
        this.inquirerEmail = inquirerEmail;
    }
}
