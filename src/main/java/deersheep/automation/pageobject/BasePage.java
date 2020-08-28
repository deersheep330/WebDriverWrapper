package deersheep.automation.pageobject;

import deersheep.automation.element.Element;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class BasePage {

    protected WebDriver driver;
    protected Map<String, Element> elements = new HashMap<>();

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected void addElement(String name, String xpath) {
        elements.put(name, new Element(name, xpath));
    }

}
