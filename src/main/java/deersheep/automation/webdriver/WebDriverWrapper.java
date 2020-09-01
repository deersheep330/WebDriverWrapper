package deersheep.automation.webdriver;

import deersheep.automation.browsermob.BrowserMobWrapper;
import deersheep.automation.webdriver.interfaces.ChromeDriverSetting;
import deersheep.automation.webdriver.interfaces.ChromeMobileEmulationSetting;
import deersheep.automation.webdriver.interfaces.IEDriverSetting;
import deersheep.automation.webdriver.interfaces.WebDriverSettingAbility;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.client.ClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WebDriverWrapper {

    private static WebDriverWrapper instance;

    public WebDriverWrapper getInstance() {
        if (instance == null) {
            instance = new WebDriverWrapper();
        }
        return instance;
    }

    protected Map<String, WebDriverSettingAbility> webDriverSettingsMap = new HashMap<>();
    protected Map<String, String> remoteNodesMap = new HashMap<>();

    protected int pageLoadTimeoutInSec = 60;

    public void setPageLoadTimeoutInSec(int sec) {
        pageLoadTimeoutInSec = sec;
    }

    protected WebDriverWrapper() {

        ChromeDriverSetting chromeDriverSetting = new ChromeDriverSetting();
        webDriverSettingsMap.put(chromeDriverSetting.getName(), chromeDriverSetting);
        System.out.println("==> add " + chromeDriverSetting.getName() + " to default webdriver setting");

        ChromeMobileEmulationSetting chromeMobileEmulationSetting = new ChromeMobileEmulationSetting();
        webDriverSettingsMap.put(chromeMobileEmulationSetting.getName(), chromeMobileEmulationSetting);
        System.out.println("==> add " + chromeMobileEmulationSetting.getName() + " to default webdriver setting");

        IEDriverSetting ieDriverSetting = new IEDriverSetting();
        webDriverSettingsMap.put(ieDriverSetting.getName(), ieDriverSetting);
        System.out.println("==> add " + ieDriverSetting.getName() + " to default webdriver setting");

        System.out.println("==> please use addWebDriverSetting() to add other settings if you have your customized webdriver settings");
        System.out.println("==> please use addRemoteNode() to add remote node if you'd like to run these webdrivers on remote node");
    }

    public void addWebDriverSetting(WebDriverSettingAbility webDriverSettingAbility) {
        webDriverSettingsMap.put(webDriverSettingAbility.getName(), webDriverSettingAbility);
        System.out.println("==> add " + webDriverSettingAbility.getName() + " to default webdriver setting");
    }

    public void addRemoteNode(String name, String remoteAddress) {
        remoteNodesMap.put(name, remoteAddress);
        System.out.println("==> add " + name + ":" + remoteAddress + " to default remote nodes map");
    }

    public List<String> getCurrentActiveWebDriverSettingList() {
        return new ArrayList<>(webDriverSettingsMap.keySet());
    }

    public List<String> getCurrentActiveRemoteNodeList() {
        return new ArrayList<>(remoteNodesMap.keySet());
    }

    /*
    get RemoteWebDriver for the specific setting and connect to the remote node
     */
    public RemoteWebDriver getWebDriver(String webDriverSettingName, String remoteNodeName) {

        if (!webDriverSettingsMap.containsKey(webDriverSettingName)) {
            throw new RuntimeException("Cannot find setting: " + webDriverSettingName + ", please add it using addWebDriverSetting()");
        }
        else if (!remoteNodesMap.containsKey(remoteNodeName)) {
            throw new RuntimeException("Cannot find remote node: " + remoteNodeName + ", plase add it using addRemoteNode()");
        }

        Capabilities capabilities = webDriverSettingsMap.get(webDriverSettingName).getCapabilities();

        RemoteWebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL(remoteNodesMap.get(remoteNodeName)), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        driver.setFileDetector(new LocalFileDetector());
        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeoutInSec, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        System.out.println("==> Get " + webDriverSettingName + " " + remoteNodeName + " WebDriver Success!");

        return driver;
    }

    /*
    get ChromeDriver / FirefoxDriver / InternetExplorerDriver / EdgeDriver / OperaDriver
    and run the automation test on local machine without standalone selenium server

    currently IE is not working, maybe there are some issues in the latest IEDriverServer.exe
     */
    public WebDriver getWebDriver(String webDriverSettingName) {

        if (!webDriverSettingsMap.containsKey(webDriverSettingName)) {
            throw new RuntimeException("Cannot find setting: " + webDriverSettingName + ", please add it using addWebDriverSetting()");
        }

        Capabilities capabilities = webDriverSettingsMap.get(webDriverSettingName).getCapabilities();

        WebDriver driver = null;

        if (StringUtils.containsIgnoreCase(webDriverSettingName, "chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver((ChromeOptions) capabilities);
        }
        else if (StringUtils.containsIgnoreCase(webDriverSettingName, "firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver((FirefoxOptions) capabilities);
        }
        else if (StringUtils.containsIgnoreCase(webDriverSettingName, "ie")) {
            WebDriverManager.iedriver().setup();
            InternetExplorerOptions options = new InternetExplorerOptions();
            options.merge(capabilities);
            driver = new InternetExplorerDriver(options);
        }
        else if (StringUtils.containsIgnoreCase(webDriverSettingName, "edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver((EdgeOptions) capabilities);
        }
        else if (StringUtils.containsIgnoreCase(webDriverSettingName, "opera")) {
            WebDriverManager.operadriver().setup();
            driver = new OperaDriver((OperaOptions) capabilities);
        }
        else {
            throw new RuntimeException("Unsupported WebDriver: " + webDriverSettingName);
        }

        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeoutInSec, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        System.out.println("==> Get " + webDriverSettingName + " WebDriver Success!");

        return driver;
    }

    /*
    use browser mob to intercept http requests
    so we can get details of the headers/payloads of every requests
    only work when browser = Chrome and Machine = Local
     */
    public ChromeDriver getHttpRequestsInterceptionChromeDriver() {

        ChromeDriver driver = null;

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(new BrowserMobWrapper().initBrowserMobProxy());

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

        /*ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(".//driver//chromedriver.exe"))
                .build();*/
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors");
        options.merge(capabilities);
        driver = new ChromeDriver(options);

        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeoutInSec, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        System.out.println("==> Get BrowserMob Chrome WebDriver Success!");

        return driver;
    }

}
