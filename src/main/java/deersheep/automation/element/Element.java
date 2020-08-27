package deersheep.automation.element;

public class Element {

    protected String name;
    protected String xpath;

    public Element(String name, String xpath) {
        this.name = name;
        this.xpath = xpath;
    }

    public String getName() {
        return name;
    }

    public String getXpath() {
        return xpath;
    }

}
