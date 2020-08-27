package deersheep.automation.webdriver;

import deersheep.automation.webdriver.enums.Browser;
import deersheep.automation.webdriver.enums.Machine;
import org.junit.Test;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.junit.Assert.assertNotEquals;

public class WebDriverWrapperTest {

    protected WebDriverWrapper webDriverWrapper = new WebDriverWrapper();

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
        RemoteWebDriver driver = webDriverWrapper.getWebDriver(Browser.Chrome, Machine.Local);
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

    @Test
    public void testMobileChromeLocalDriver() {
        RemoteWebDriver driver = webDriverWrapper.getWebDriver(Browser.MobileChrome, Machine.Local);
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

    @Test
    public void testIELocalDriver() {
        RemoteWebDriver driver = webDriverWrapper.getWebDriver(Browser.IE, Machine.Local);
        assertNotEquals(driver, null);
        browse(driver);
        driver.quit();
    }

    @Test
    public void testChromeRemoteDriver() {
        RemoteWebDriver driver = webDriverWrapper.getWebDriver(Browser.Chrome, Machine.Remote);
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
