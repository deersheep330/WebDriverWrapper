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

- __Click an element__

Click an element. If the element isn't clickable because it's not present, it would retry several times. If the element still not present so not clickable, an exception would be thrown.  

```java
op.click(getElement("MyButton"));
```

- __Click an element and wait for another element__

Click an element and expect another element to be displayed, if the expected element isn't present, it would retry several times. If the expected element still not present, an exception would be thrown.  

```java
op.clickAndWait(getElement("MyButton"), getElement("TriggeredElement"));
```

- __Click an element with offset and wait for another element__

Click an element with offset and expect another element to be displayed, if the expected element isn't present, it would retry several times. If the expected element still not present, an exception would be thrown. It could be useful if you're dealing with SVG elements.

```java
op.clickWithOffsetAndWait(getElement("MySvgElement"), 10, 30, getElement("TriggeredSvgElement"));
```

- __Hover an element and wait for another element__

Hover an element and expect another element to be displayed, if the expected element isn't present, it would retry several times. If the expected element still not present, an exception would be thrown.  

```java
op.hoverAndWait(getElement("MyText"), getElement("TriggeredTooltip"));
```

- __Hover an element with offset and wait for another element__

Hover an element with offset and expect another element to be displayed, if the expected element isn't present, it would retry several times. If the expected element still not present, an exception would be thrown. It could be useful if you're dealing with SVG elements.

```java
op.hoverWithOffsetAndWait(getElement("MySvgElement"), 10, 30, getElement("TriggeredSvgElement"));
```

- __Test if an element is present or not__

Test if an element is present or not for a certain timeout. Which means if the target element not found, it would retry several times before return.

```java
boolean found = op.tryToFind(getElement("GDPRModal"));
```

Test of an element is present or not. This method would return immediately. 

```java
boolean found = op.isExist(getElement("GDPRModal"));
```

- __Wait for an element__

Wait for an element being present, if it's not present, an exception would be thrown.

```java
op.waitFor(getElement("GDPRModal"));
```

- __Send text to an input box or textarea__

```java
op.sendText(getElement("MyInput"));
```

### BasePage

- __BasePage is the parent page object for other page objects to extend__

It has Operation object as a member variable, HashMap for storing web elements, and convenient methods to set/get elements in the HashMap.

```java
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

    public void navigate() {
        if (url == null) throw new RuntimeException("url is null! please set it in constructor or using setUrl method.");
        op.navigateTo(url);
    }

    ...

}
```

- __Implement your own page object by extending BasePage__

```java
public class LoginPage extends Basepage {

    LoginPage(WebDriver driver) {
        super(driver);

        url = "http://my-site.com/login";

        addElement("LoginButton", "xpath-for-login-button");
        addElement("LoginForm", "xpath-for-login-form");
        addElement("EmailInput", "xpath-for-email-input");
        addElement("PasswordInput", "xpath-for-password-input");
        addElement("Submit", "xpath-for-submit");
    }

    public void login(String email, String password) {
        op.clickAndWait(getElement("LoginButton"), getElement("LoginForm"));
        op.sendText(getElement("EmailInput"), email);
        op.sendText(getElement("PasswordInput"), password);
        op.click(getElement("Submit"));
    }

}
```

## Detailed API Docs

#### WebDriverWrapper

| API | Description |
| --- | ----------- |
| getInstance | Get WebDriverWrapper singleton |
| setPageLoadTimeoutInSec | Change webdriver page load timeout. It's 60 sec by default.  |
| addWebDriverSetting | Add a customized webdriver setting to create a customized webdriver later |
| addRemoteNode | Add a remote node setting into the WebDriverWrapper to create a remote webdriver for this remote node later |
| getCurrentActiveWebDriverSettingList | Get the list of WebDriverSettings in this WebDriverWrapper |
| getCurrentActiveRemoteNodeList | Get the list of remote nodes in this WebDriverWrapper |
| getWebDriver(String webDriverSettingName, String remoteNodeName) | Get RemoteWebDriver for the specific setting and connect to the remote node |
| getWebDriver(String webDriverSettingName) | Get WebDriver runs on local machine without standalone selenium server |
| getHttpRequestsInterceptionChromeDriver | Use BrowserMob to intercept http requests so we can get details of the headers/payloads of every requests. Only work when browser = Chrome and Machine = Local |

#### Operation

- __Check browser type__

| API | Description |
| --- | ----------- |
| isIE | Test current webdriver is an IEDriver or not |

- __Change default setting__

| API | Description |
| --- | ----------- |
| setTargetElementWaitTimeoutInSec | Set target element wait timeout |
| setWaitForElementWaitTimeoutInSec | Set waitFor element wait timeout |

- __"Click and hold" method group__

| API | Description |
| --- | ----------- |
| clickAndHold | Click and hold "target" element and no need to wait for anything |
| clickAndHoldWithOffset | Click and hold "target" element with offset and no need to wait for anything |
| clickAndHoldAndWait | Click and hold "target" element and wait for "waitFor" element |
| clickAndHoldWithOffsetAndWait | Click and hold "target" element with offset and wait for "waitFor" element |
| clickAndHoldAndWait | Click and hold "target" element and wait for "waitFor" element with customized timeout |
| clickAndHoldWithOffsetAndWait | Click and hold "target" element with offset and wait for "waitFor" element with customized timeout |
| release | Release "target" element after "click and hold" |

- __"Hover" method group__

| API | Description |
| --- | ----------- |
| hover | Hover "target" element and no need to wait for anything |
| hoverWithOffset | Hover "target" element with offset and no need to wait for anything |
| hoverAndWait | Hover "target" element and wait for "waitFor" element |
| hoverWithOffsetAndWait | Hover "target" element with offset and wait for "waitFor" element |
| hoverAndWait | Hover "target" element and wait for "waitFor" element with customized timeout |
| hoverWithOffsetAndWait | Hover "target" element with offset and wait for "waitFor" element with customized timeout |

- __"Right click" method group__

| API | Description |
| --- | ----------- |
| contextClick | Right click "target" element and no need to wait for anything |
| contextClickWithOffset | Right click "target" element with offset and no need to wait for anything |
| contextClickAndWait | Right click "target" element and wait for "waitFor" element |
| contextClickWithOffsetAndWait | Right click "target" element with offset and wait for "waitFor" element |
| contextClickAndWait | Right click "target" element and wait for "waitFor" element with customized timeout |
| contextClickWithOffsetAndWait | Right click "target" element with offset and wait for "waitFor" element with customized timeout |

- __"Click" method group__

| API | Description |
| --- | ----------- |
| click | Click "target" element and no need to wait for anything |
| clickWithOffset | Click "target" element with offset and no need to wait for anything |
| clickAndWait | Click "target" element and wait for "waitFor" element |
| clickWithOffsetAndWait | Click "target" element with offset and wait for "waitFor" element |
| clickAndWait | Click "target" element and wait for "waitFor" element with customized timeout |
| clickWithOffsetAndWait | Click "target" element with offset and wait for "waitFor" element with customized timeout |

- __Check for existence__

| API | Description |
| --- | ----------- |
| isExist | Check "target" element is displayed or not |
| tryToFind | Test "target" element is exist or not. If "target" is exist, return true, else if "target" isn't exist, return false. No exception would be thrown |
| waitFor | Wait for "waitFor" element. If it's not found after timeout, an exception would be thrown |

- __Get WebElement(s)__

| API | Description |
| --- | ----------- |
| findElement | Get single WebElement |
| findElements | Get list of WebElements |

- __Send text or select__

| API | Description |
| --- | ----------- |
| sendText | Send text to InputBox or TextArea |
| selectDropdownMenuOptionByValue | Select dropdown menu option by value |

- __WebDriver navigation or reload__

| API | Description |
| --- | ----------- |
| navigateTo | Webdriver navigate to url |
| reloadPage | Webdriver reload page |

- __Tab-related operations__

| API | Description |
| --- | ----------- |
| IsNewTabBeingOpened | Check if a new tab being opened |
| switchToFirstNewlyOpenedTab | Switch to the first newly opened tab |
| closeNewlyOpenedTabs | close newly opened tab |
| getCurrentOpenedTabsCount | Get current opened tabs count |
| getCurrentOpenedTabsSet | Get current opened tabs handles |
| switchToTab | Switch to another tab according to handle |
| saveCurrentTabAsDefaultTab | Save current tab as default tab |
| getDefaultTabHandle | Get default tab handle |

- __Click alert__

| API | Description |
| --- | ----------- |
| clickAlertOK | Click alert OK |

- __Iframe-related operations__

| API | Description |
| --- | ----------- |
| switchToIframe | Switch to Iframe |
| switchFromIframeToMainHTML | Switch back from Iframe to main content |

- __Scroll-related operations__

| API | Description |
| --- | ----------- |
| scrollWindowTo | Scroll window to the specified offset |
| scrollToElement | Scroll to element (align start) |
| scrollToElementAlignCenter | Scroll to element (align center) |

- __Webdriver quit__

| API | Description |
| --- | ----------- |
| quitAndCloseBrowser | Call driver.quit() to close browser |

- __LoggingPrefs-related operations__

| API | Description |
| --- | ----------- |
| getRequestUrlFromLoggingPrefs | Search for request urls according to keywords. Only works for Chrome driver with loggingPrefs enabled |
| enableSaveCookieFromLoggingPrefs | Save cookie in Operation instance. Only works for Chrome driver with loggingPrefs enabled |
| saveSpecialHeader | Save specific header in Operation instance. Only works for Chrome driver with loggingPrefs enabled |

- __Thread.sleep__

| API | Description |
| --- | ----------- |
| sleep | Sleep for millis |

- __WebDriver screenshot__

| API | Description |
| --- | ----------- |
| screenshot | Screenshot |
| screenshotAndEmbedInCucumberReport | Screenshot and embed in Cucumber report |

#### BasePage

| API | Description |
| --- | ----------- |
| addElement | Store the element in the HashMap of BasePage |
| getElement | Get the element from the HashMap |
| setUrl | Set url for this page |
| getUrl | Get url for this page |
| navigate | Navigate to url |
| getOperation | Get Operation instance |

#### Utility Tools

| API | Description |
| --- | ----------- |
| NumberTool.findIntFromString | Find integer from string |
| NumberTool.parseIntFromString | Parse integer from string |
| StringTool.replaceTextInFile | Replace specific text the a file |
| XlsxTool.getWorkbookFromHttpURLConnection | Get Xlsx Workbook from HttpUrlConnection |
| XlsxTool.getDefaultSheetFromWorkbook | Get default sheet from workbook |
| XlsxTool.getRowCountOfSheet | Get row count of sheet |

## Demo Projects

__TODO__

## License
