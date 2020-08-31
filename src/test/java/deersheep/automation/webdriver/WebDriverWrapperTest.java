package deersheep.automation.webdriver;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Arrays;

import static org.junit.Assert.assertNotEquals;

public class WebDriverWrapperTest {

    protected static WebDriverWrapper webDriverWrapper = new WebDriverWrapper();

    @BeforeClass
    public static void setUp() {
        webDriverWrapper.addRemoteNode("localhost", "http://localhost:4040/wd/hub");
        webDriverWrapper.addRemoteNode("remoteServer", "http://10.60.91.40:4040/wd/hub");

        System.out.println("==> current active webdriver settings: " + Arrays.toString(webDriverWrapper.getCurrentActiveWebDriverSettingList().toArray()));
        System.out.println("==> current active remote nodes: " + Arrays.toString(webDriverWrapper.getCurrentActiveRemoteNodeList().toArray()));
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void browse(WebDriver driver) {
        System.out.println("==> Current Browser: " + ((HasCapabilities) driver).getCapabilities().getBrowserName());
        driver.get("https://www.facebook.com");
        sleep(3000);
    }

    @Test
    public void testChromeLocalDriver() {
        WebDriver driver = webDriverWrapper.getWebDriver("Chrome", "localhost");
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

    @Test
    public void testMobileChromeLocalDriver() {
        WebDriver driver = webDriverWrapper.getWebDriver("ChromeMobileEmulation");
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

    @Test
    public void testIELocalDriver() {

        // currently IE is not working, maybe there are some issues in the latest IEDriverServer.exe
        // the browser will open and navigate to the webpage, but it always get a Wait Page Load timeout error...
        // WebDriver driver = webDriverWrapper.getWebDriver("IE");
        WebDriver driver = webDriverWrapper.getWebDriver("IE", "localhost");
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

    @Test
    public void testChromeRemoteDriver() {
        WebDriver driver = webDriverWrapper.getWebDriver("Chrome", "remoteServer");
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

    @Test
    public void testHttpRequestInterceptionChromeDriver() {
        ChromeDriver driver = webDriverWrapper.getHttpRequestsInterceptionChromeDriver();
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

}
