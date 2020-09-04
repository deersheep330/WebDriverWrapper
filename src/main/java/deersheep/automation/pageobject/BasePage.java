package deersheep.automation.pageobject;

import deersheep.automation.element.Element;
import deersheep.automation.operation.Operation;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public abstract class BasePage {

    protected WebDriver driver;
    protected Operation op;
    protected Map<String, Element> elements = new HashMap<>();
    protected String url;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.op = new Operation(this.driver);
    }

    protected void addElement(String name, String xpath) {
        elements.put(name, new Element(name, xpath));
    }

    protected Element getElement(String name) { return elements.get(name); }

    public void setUrl(String url) { this.url = url; }

    public String getUrl() { return url; }

    public void navigate() {
        if (url == null) throw new RuntimeException("url is null! please set it in constructor or using setUrl method.");
        op.navigateTo(url);
    }

}
