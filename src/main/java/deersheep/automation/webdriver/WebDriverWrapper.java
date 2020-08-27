package deersheep.automation.webdriver;

import deersheep.automation.browsermob.BrowserMobWrapper;
import deersheep.automation.webdriver.enums.Browser;
import deersheep.automation.webdriver.enums.Machine;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WebDriverWrapper {

    public WebDriverWrapper() {
    }

    public RemoteWebDriver getWebDriver(Browser browser, Machine machine) {

        RemoteWebDriver driver = null;

        // init capabilities as Chrome
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        // modify capabilities according to "browser"
        switch(browser) {
            case MobileChrome:
                HashMap<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "Nexus 5");
                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("mobileEmulation", mobileEmulation);
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            case Chrome:
                LoggingPreferences logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                capabilities.setCapability("goog:loggingPrefs", logPrefs);
                break;
            case IE:
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                capabilities.setCapability("requireWindowFocus", false);
                capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
                capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
                capabilities.setCapability("ie.ensureCleanSession", true);
				/*capabilities.setJavascriptEnabled(true);
				capabilities.setCapability("nativeEvents", false);
				capabilities.setCapability("unexpectedAlertBehaviour", "accept");
				capabilities.setCapability("ignoreProtectedModeSettings", true);
				capabilities.setCapability("disable-popup-blocking", true);
				capabilities.setCapability("enablePersistentHover", true);
				capabilities.setCapability("ignoreZoomSetting", true);*/
                break;
            default:
                throw new RuntimeException("Unsupported Browser Type: " + browser);
        }

        String url = Machine.Local.getUrl();
        switch (machine) {
            case Remote:
                url = Machine.Remote.getUrl();
                break;
            case Local:
                url = Machine.Local.getUrl();
                break;
            default:
                throw new RuntimeException("Unsupported Machine Type: " + machine);
        }

        try {
            driver = new RemoteWebDriver(new URL(url), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        driver.setFileDetector(new LocalFileDetector());
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        System.out.println("==> Get " + browser + " " + machine + " WebDriver Success!");

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

        //driver.setFileDetector(new LocalFileDetector());
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        System.out.println("==> Get BrowserMob Chrome WebDriver Success!");

        return driver;
    }

}
