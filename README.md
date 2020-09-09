# WebDriverWrapper

__WebDriverWrapper is a Selenium Webdriver Wrapper helps to build robust & adaptive test automation project in a fast pace.__ It contains:
- __WebDriverWrapper:__ A Webdriver Factory lets you easily create webdrivers with different capabilities. 
- __Operation:__ A high-level abstraction of basic webdriver methods makes you manipulate and interact with DOM elements in a convenient and robust way.
- __BasePage:__ Adopt the concept of Page-Object to implement your automation project, and it has Operation object as a member variable, so dealing with DOM elements in this Page-Object is a piece of cake. 
- __Utility Tools:__ Some tools you could find it helpful when you're writing your automation project. Like dealing with String, Number, Files and so on.

## Contents

- [Getting Started](#getting-started)
- [Basic Examples](#basic-examples)
- [Detailed API Docs](#detailed-api-docs)
- [Demo Projects](#demo-projects)
- [License](#license)

## Getting Started

To get a Git project into your build:

#### Step 1. Add the JitPack repository to your build file

Take Gradle for example. Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2. Add the dependency
```
dependencies {
    implementation 'com.github.deersheep330:WebDriverWrapper:-SNAPSHOT'
}
```

## Basic Examples

### WebDriverWrapper

- __Get WebDriverWrapper instance__

WebDriverWrapper is a singleton.

```java
WebDriverWrapper wrapper = WebDriverWrapper.getInstance();
```

- __Get Chrome driver runs on your local machine__

WebDriverWrapper has built-in Chrome driver.

```java
WebDriver driver = wrapper.getWebDriver("Chrome");
```

- __Get Chrome Mobile Emulation driver runs on your local machine__

WebDriverWrapper has built-in ChromeMobileEmulation driver.

```java
WebDriver driver = wrapper.getWebDriver("ChromeMobileEmulation");
```

- __Get IE driver runs on your local machine__

WebDriverWrapper has built-in IE driver.

```java
WebDriver driver = wrapper.getWebDriver("IE");
```

- __Get a customized driver runs on your local machine__

If you'd like to create your own customized driver, you can implement the WebDriverSettingAbility interface, and override the getCapabilities() method to setup the capabilities (options) you want, and override the getName() method to specify a name to this customized driver so you can create this driver by this name later.

```java
wrapper.addWebDriverSetting(new WebDriverSettingAbility() {
    @Override
    public Capabilities getCapabilities() {
        SafariOptions options = new SafariOptions();
        options.setCapability("your_custom_setting", yourCustomSetting);
        return options;
    }

    @Override
    public String getName() {
        return "CustomSafari";
    }
});

WebDriver driver = wrapper.getWebDriver("CustomSafari");
```

- __Get a (remote) driver runs on a remote node__

The { node-name: node-address } mappings should be added into WebDriverWrapper so it can create the remote driver for you.

```java
wrapper.addRemoteNode("remote-safari-on-mac", "http://192.168.30.40:4040/wd/hub");
wrapper.addRemoteNode("remote-chrome-on-win", "http://50.60.70.80:4040/wd/hub");

WebDriver remoteSafariDriver = wrapper.getWebDriver("CustomSafari", "remote-safari-on-mac");
WebDriver remoteChromeDriver = wrapper.getWebDriver("Chrome", "remote-chrome-on-win");
```

- __Get a driver using BrowserMob to intercept http requests and running on your local machine__

For more details about BrowserMob, please visit [its github](https://github.com/lightbody/browsermob-proxy).
Currently, WebDriverWrapper only create BrowserMob driver running on a local machine, if you know how to setup BrowserMob so it can run on a remote node, please kindly let me know :)

```java
WebDriver driver = wrapper.getHttpRequestsInterceptionChromeDriver();
```

### Operation

- __Create a new Operation object__

You can explicitly create an Operation object, but actually it's not necessary. If you let your page object extend [BasePage](#basepage), it already has Operation member variable built-in. 

Explicitly create a new Operation:

```java
Operation op = new Operation(driver);
```

Or just extends [BasePage](#basepage):

```java
public abstract class BasePage {

    protected Operation op;

    public BasePage(WebDriver driver) {
        this.op = new Operation(driver);
    }
}

public class MainPage extends BasePage {
    // ......
}
```

- __Use methods provided by Operation__

To use methods provided by Operation, you should wrap the DOM elements you'd like to interact with into an Element object. Element class has only 2 fields: the identifier (name) and the Xpath of this DOM element.

```java
public class Element {

    ...

    public Element(String name, String xpath) {
        this.name = name;
        this.xpath = xpath;
    }

    ...

}
```

It could be convenient to store all the Elements you will use into a HashMap.

```java
Map<String, Element> elements = new HashMap<>();
elements.put(elementName, new Element(elementName, elementXpath)));
```

Also, [BasePage](#basepage) has built-in map to store Elements and provide methods to easily set/access Elements in this map.

```java
public abstract class BasePage {
    
    ...

    protected Map<String, Element> elements = new HashMap<>();

    protected void addElement(String name, String xpath) {
        elements.put(name, new Element(name, xpath));
    }

    protected Element getElement(String name) { return elements.get(name); }

    ...

}
```

Examples below assume your page objects extend BasePage so you can use the Operation object and Element-related methods provided by it. 

- __Click an element and wait for another element__

Click an element and expect another element to be displayed, if the expected element isn't present, an exception would be thrown.  

```java
op.clickAndWait(getElement("MyButton"), getElement("TriggeredElement"));
```

- __Click an element with offset and wait for another element__

Click an element with offset and expect another element to be displayed, if the expected element isn't present, an exception would be thrown. It could be useful if you're dealing with SVG elements.

```java
op.clickWithOffsetAndWait(getElement("MySvgElement"), 10, 30, getElement("TriggeredSvgElement"));
```

- __Hover an element and wait for another element__

Hover an element and expect another element to be displayed, if the expected element isn't present, an exception would be thrown.  

```java
op.hoverAndWait(getElement("MyText"), getElement("TriggeredTooltip"));
```

- __Hover an element with offset and wait for another element__

Hover an element with offset and expect another element to be displayed, if the expected element isn't present, an exception would be thrown. It could be useful if you're dealing with SVG elements.

```java
op.hoverWithOffsetAndWait(getElement("MySvgElement"), 10, 30, getElement("TriggeredSvgElement"));
```

- __Test if an element is present or not__

Test if an element is present or not for a certain timeout. Which means if the target element not found, it would retry several times before return.

```java
boolean found = op.tryToFind(getElement("GDPRModal"));
```




### BasePage

## Detailed API Docs

## Demo Projects

__TODO__

## License
