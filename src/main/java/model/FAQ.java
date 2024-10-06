package model;
import java.util.ArrayList;
import java.util.Collection;

public class FAQ {
    public static Collection<FAQSection> sections;
    public static Collection<FAQSection> getSections() {
        return sections;
    }
    public void addSection(FAQSection faqSection){
        this.sections.add(faqSection);
    }
    public FAQ(){
        this.sections = new ArrayList<>();
    }
}
