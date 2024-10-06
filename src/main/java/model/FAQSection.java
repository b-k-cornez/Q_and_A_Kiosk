package model;
import java.util.ArrayList;
import java.util.List;

public class FAQSection {
    private final String topic;
    private FAQSection parent;
    private ArrayList<FAQSection> subSections;
    public ArrayList<FAQItem> FAQItems;
    public List<FAQSection> sections;

    public FAQSection(String topic){
        this.topic = topic;
        this.subSections = new ArrayList<FAQSection>();
        this.FAQItems = new ArrayList<FAQItem>();
        this.sections = new ArrayList<FAQSection>();
    }
    public String getTopic() {
        return topic;
    }
    public FAQSection getParent() {
        return parent;
    }
    public ArrayList<FAQSection> getSubsections() {
        return subSections;
    }
    public void addSubsection(FAQSection faqSection) {
    }
    public List<FAQItem> getItems() {
        return new ArrayList<>(this.FAQItems);
    }
    public void addItem(String item, String answer) {
        FAQItem newItem = new FAQItem(item, answer);
        this.FAQItems.add(newItem);
    }
}
