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

```
WebDriverWrapper wrapper = WebDriverWrapper.getInstance();
```

- __Get Chrome driver runs on your local machine__

WebDriverWrapper has built-in Chrome driver.

```
WebDriver driver = wrapper.getWebDriver("Chrome");
```

- __Get Chrome Mobile Emulation driver runs on your local machine__

WebDriverWrapper has built-in ChromeMobileEmulation driver.

```
WebDriver driver = wrapper.getWebDriver("ChromeMobileEmulation");
```

- __Get IE driver runs on your local machine__

WebDriverWrapper has built-in IE driver.

```
WebDriver driver = wrapper.getWebDriver("IE");
```

- __Get a customized driver runs on your local machine__

If you'd like to create your own customized driver, you can implement the WebDriverSettingAbility interface, and override the getCapabilities() method to setup the capabilities (options) you want, and override the getName() method to specify a name to this customized driver so you can create this driver by this name later.

```
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

The node name - node address mappings should be added into WebDriverWrapper so it can create the remote driver for you.

```
wrapper.addRemoteNode("remote-safari-on-mac", "http://192.168.30.40:4040/wd/hub");
wrapper.addRemoteNode("remote-chrome-on-win", "http://50.60.70.80:4040/wd/hub");

WebDriver remoteSafariDriver = wrapper.getWebDriver("CustomSafari", "remote-safari-on-mac");
WebDriver remoteChromeDriver = wrapper.getWebDriver("Chrome", "remote-chrome-on-win");
```

- __Get a driver using BrowserMob to intercept http requests and running on your local machine__

For more details about BrowserMob, please visit [its github](https://github.com/lightbody/browsermob-proxy).
Currently, WebDriverWrapper only create BrowserMob driver running on a local machine, if you know how to setup BrowserMob so it can run on a remote node, please kindly let me know :)

```
WebDriver driver = wrapper.getHttpRequestsInterceptionChromeDriver();
```

### Operation



### BasePage

## Detailed API Docs

## Demo Projects

__TODO__

## License
