package deersheep.automation.operation;

import deersheep.automation.element.Element;
import deersheep.automation.loggingprefs.LoggingPrefs;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class Operation {

    protected WebDriver driver;

    protected Actions actions;
    protected JavascriptExecutor js;
    protected Capabilities capabilities;

    protected String defaultTabHandle;

    protected long defaultTargetElementWaitTimeoutInSec = 8;
    protected long defaultWaitForElementWaitTimeoutInSec = 8;

    protected long pollingIntervalInMillis = 500;

    protected long clickMaxRetry = 3;

    protected LoggingPrefs loggingPrefs;

    public Operation(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(this.driver);
        this.js = (JavascriptExecutor) this.driver;
        this.capabilities = ((HasCapabilities) this.driver).getCapabilities();

        if (this.capabilities.getCapability("goog:loggingPrefs") == null) {
            this.loggingPrefs = null;
        }
        else {
            this.loggingPrefs = new LoggingPrefs(this.driver);
        }
    }

    public boolean isIE() {
        if (this.capabilities.getBrowserName().equals("internet explorer")) return true;
        else return false;
    }

    protected void setTargetElementWaitTimeoutInSec(long sec) {
        this.defaultTargetElementWaitTimeoutInSec = sec;
    }

    protected void setWaitForElementWaitTimeoutInSec(long sec) {
        this.defaultWaitForElementWaitTimeoutInSec = sec;
    }

    protected void simpleClick(WebElement element) {
        actions.click(element).build().perform();
    }

    protected void simpleClickWithOffset(WebElement element, int xOffset, int yOffset) {
        actions.moveToElement(element, xOffset, yOffset).click().build().perform();
    }

    /*
    click "target" element and no need to wait for anything
    */
    public void click(Element target) {
        _clickWithOffsetAndWait(target, 0, 0,null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec);
    }

    /*
    click "target" element with offset and no need to wait for anything
    */
    public void clickWithOffset(Element target, int xOffset, int yOffset) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec);
    }

    /*
    click "target" element and wait for "waitFor" element
    */
    public void clickAndWait(Element target, Element waitFor) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec);
    }

    /*
    click "target" element with offset and wait for "waitFor" element
    */
    public void clickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec);
    }

    /*
    click "target" element and wait for "waitFor" element
    customize timeout
    */
    public void clickAndWait(Element target, Element waitFor, long targetElementWaitTimeoutInSec, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, targetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec);
    }

    /*
    click "target" element with offset and wait for "waitFor" element
    customize timeout
    */
    public void clickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor, long targetElementWaitTimeoutInSec, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, targetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec);
    }

    protected void _clickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor, long targetElementWaitTimeoutInSec, long waitForElementWaitTimeoutInSec) throws RuntimeException {

        /*
        step 1-1:
        try to find "target" element
        polling in 1 second interval
        if "target" element not found after 8 seconds
        throw an exception and return
         */
        Wait<WebDriver> targetElementWait = new FluentWait<WebDriver>(driver) {
            @Override
            protected RuntimeException timeoutException(String Message, Throwable lastException) {
                throw new RuntimeException("Target Element " + target.getName() + " not Found! " + Message);
            }
        }.withTimeout(Duration.ofSeconds(targetElementWaitTimeoutInSec))
                .pollingEvery(Duration.ofMillis(pollingIntervalInMillis))
                .ignoring(Exception.class);

        /*
        step 1-2:
        when we say we "find" the target element
        it means "target" element is present on the DOM of the page
        and the width and height are greater than 0
         */
        WebElement targetElement = targetElementWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(target.getXpath())));

        /*
        step 2-1:
        if no need to wait for the other element
        just click target and return
         */
        if (waitFor == null) {
            if (xOffset != 0 || yOffset != 0) simpleClickWithOffset(targetElement, xOffset, yOffset);
            else simpleClick(targetElement);
        }
        /*
        step 2-2:
        if need to wait for the other element
        click target and wait for "waitFor" element
        if "waitFor" element not found after timeout
        click target again (retry)
         */
        else {

            Wait<WebDriver> waitForElementWait = new FluentWait<WebDriver>(driver) {
                @Override
                protected RuntimeException timeoutException(String Message, Throwable lastException) {
                    throw new RuntimeException("Click " + target.getName() + " and Wait for " + waitFor.getName() + " Fail! " + Message);
                }
            }.withTimeout(Duration.ofSeconds(waitForElementWaitTimeoutInSec))
                    .pollingEvery(Duration.ofMillis(pollingIntervalInMillis))
                    .ignoring(Exception.class);

            boolean success = false;
            long retry = 0;
            do {
                try {
                    sleep(1000);
                    if (xOffset != 0 || yOffset != 0) simpleClickWithOffset(targetElement, xOffset, yOffset);
                    else simpleClick(targetElement);

                    waitForElementWait.until(
                            ExpectedConditions.visibilityOfElementLocated(By.xpath(waitFor.getXpath())));
                    success = true;
                }
                /*
                catch the runtime exception we defined above
                so we can retry (click again) instead of throwing an exception
                 */ catch (Exception e) {
                    System.out.println(e + " retry: " + retry);
                }
            } while (!success && ++retry < clickMaxRetry);

            if (!success) {
                throw new RuntimeException("Click " + target.getName() + " and wait for " + waitFor.getName() + " fail after timeout " + clickMaxRetry * waitForElementWaitTimeoutInSec + " s");
            }
        }
    }

    /*
    test "target" element is exist or not
    if "target" is exist, return true
    if "target" isn't exist, return false, no exception would be thrown
     */
    public boolean tryToFind(Element target) {
        return tryToFind(target, defaultTargetElementWaitTimeoutInSec);
    }

    /*
    test "target" element is exist or not
    if "target" is exist, return true
    if "target" isn't exist, return false, no exception would be thrown
     */
    public boolean tryToFind(Element target, long targetElementWaitTimeoutInSec) {
        /*
        step 1: wait target
         */
        Wait<WebDriver> targetElementWait = new FluentWait<WebDriver>(driver) {
            @Override
            protected RuntimeException timeoutException(String Message,Throwable lastException) {
                throw new RuntimeException("Try to find " + target.getName() + " but not found ! " + Message);
            }
        }.withTimeout(Duration.ofSeconds(targetElementWaitTimeoutInSec))
         .pollingEvery(Duration.ofMillis(pollingIntervalInMillis))
         .ignoring(Exception.class);

        try {
            targetElementWait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath(target.getXpath())));
        }
        catch(Exception e) {
            // do nothing
        }

        /*
        step 2: test target
         */
        return isExist(target);
    }

    /*
    wait for "waitFor" element
    if not found after timeout, exception would be thrown
     */
    public void waitFor(Element waitFor) throws RuntimeException {
        waitFor(waitFor, defaultWaitForElementWaitTimeoutInSec);
    }

    /*
    wait for "waitFor" element
    if not found after timeout, exception would be thrown
     */
    public void waitFor(Element waitFor, long waitForElementWaitTimeoutInSec) throws RuntimeException {
        Wait<WebDriver> waitForElementWait = new FluentWait<WebDriver>(driver) {
            @Override
            protected RuntimeException timeoutException(String Message,Throwable lastException) {
                throw new RuntimeException("Element " + waitFor.getName() + " not found after timeout " + waitForElementWaitTimeoutInSec + " s. " + Message);
            }
        }.withTimeout(Duration.ofSeconds(waitForElementWaitTimeoutInSec))
         .pollingEvery(Duration.ofMillis(pollingIntervalInMillis))
         .ignoring(Exception.class);

        waitForElementWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(waitFor.getXpath())));
    }

    /*
    check "target" element is displayed or not
     */
    public boolean isExist(Element target) {
        if (driver.findElements(By.xpath(target.getXpath())).size() != 0 &&
                driver.findElement(By.xpath(target.getXpath())).isDisplayed()) {
            return true;
        }
        else {
            return false;
        }
    }

    public WebElement getElement(Element target) {
        boolean found = tryToFind(target);
        if (!found) throw new RuntimeException("Element " + target.getName() + " not Found!");

        List<WebElement> list = driver.findElements(By.xpath(target.getXpath()));
        if (list.size() == 1) return list.get(0);
        else throw new RuntimeException("More than one element found! Use Operation.getElements() instead.");
    }

    public List<WebElement> getElements(Element target) {
        boolean found = tryToFind(target);
        if (!found) throw new RuntimeException("Element " + target.getName() + " not Found!");

        List<WebElement> list = driver.findElements(By.xpath(target.getXpath()));
        return list;
    }

    public void sendText(Element target, String text) {

        if (!isIE()) {
            click(target);
            WebElement element = getElement(target);
            element.clear();
            element.sendKeys(text);
        }
        else {
            click(target);
            WebElement element = getElement(target);
            element.sendKeys("dummy");
            sleep(1000);
            js.executeScript("arguments[0].innerHTML=arguments[1]", element, text);
            sleep(1000);
        }
    }

    public void selectDropdownMenuOptionByValue(Element dropdown, String valueToBeSelected) {
        Select select = new Select(getElement(dropdown));
        select.selectByValue(valueToBeSelected);
    }

    public void navigateTo(String url) {
        driver.get(url);
        defaultTabHandle = driver.getWindowHandle();
    }

    public void reloadPage() {
        driver.navigate().refresh();
        defaultTabHandle = driver.getWindowHandle();
    }

    public boolean IsNewTabBeingOpened() {
        if (driver.getWindowHandles().size() > 1) return true;
        else return false;
    }

    public int getCurrentOpenedTabsCount() {
        return driver.getWindowHandles().size();
    }

    public Set<String> getCurrentOpenedTabsSet() {
        return driver.getWindowHandles();
    }

    public void switchToFirstNewlyOpenedTab() {
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(defaultTabHandle)) {
                System.out.println("Switch from " + defaultTabHandle + " to " + handle);
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    public void switchToTab(String handle) {
        for (String _handle : driver.getWindowHandles()) {
            if (_handle.equals(handle)) {
                System.out.println("Switch to " + handle);
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    public void closeNewlyOpenedTabs() {

        if (defaultTabHandle == null) return;

        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(defaultTabHandle)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(defaultTabHandle);
    }

    public void saveCurrentTabAsDefaultTab() {
        defaultTabHandle = driver.getWindowHandle();
    }

    public String getDefaultTabHandle() {
        return defaultTabHandle;
    }

    public void switchToIframe(Element target) {
        if (!target.getXpath().contains("iframe")) throw new RuntimeException("target of switchToIframe should be an iframe");
        driver.switchTo().frame(getElement(target));
    }

    public void switchFromIframeToMainHTML() {
        driver.switchTo().defaultContent();
    }

    public void scrollWindowTo(String xOffset, String yOffset) {
        js.executeScript("window.scrollTo(arguments[0],arguments[1]);", xOffset, yOffset);
    }

    public void scrollToElement(Element target) {
        js.executeScript("arguments[0].scrollIntoView();", getElement(target));
    }

    public void quitAndCloseBrowser() {
        driver.quit();
    }

    /*
    only works in Chrome driver with capabilities set below:
    LoggingPreferences logPrefs = new LoggingPreferences();
    logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
    capabilities.setCapability("goog:loggingPrefs", logPrefs);
     */
    public String getRequestUrlFromLoggingPrefs(String... keywords) {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        return loggingPrefs.getRequestUrl(keywords);
    }

    public void enableSaveCookieFromLoggingPrefs(boolean enabled) {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        loggingPrefs.enableSaveCookie(enabled);
    }

    public void saveSpecialHeader(String headerName) {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        loggingPrefs.saveSpecialHeader(headerName);
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
