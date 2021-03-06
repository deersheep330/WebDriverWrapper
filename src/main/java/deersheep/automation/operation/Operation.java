package deersheep.automation.operation;

import deersheep.automation.element.Element;
import deersheep.automation.loggingprefs.LoggingPrefs;
import deersheep.automation.operation.enums.ClickType;
import deersheep.automation.operation.enums.WaitType;
import io.cucumber.java.Scenario;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Operation {

    protected WebDriver driver;

    protected Actions actions;
    protected JavascriptExecutor js;
    protected Capabilities capabilities;

    protected String defaultTabHandle;

    protected long defaultTargetElementWaitTimeoutInSec = 8;
    protected long defaultWaitForElementWaitTimeoutInSec = 8;

    protected long pollingIntervalInMillis = 100;

    protected long clickMaxRetry = 3;

    protected LoggingPrefs loggingPrefs;

    public Operation(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(this.driver);
        this.js = (JavascriptExecutor) this.driver;
        this.capabilities = ((HasCapabilities) this.driver).getCapabilities();

        if (false && this.capabilities.getCapability("goog:loggingPrefs") == null) {
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

    public void setDefaultTargetElementWaitTimeoutInSec(long sec) {
        this.defaultTargetElementWaitTimeoutInSec = sec;
    }

    public void setDefaultWaitForElementWaitTimeoutInSec(long sec) {
        this.defaultWaitForElementWaitTimeoutInSec = sec;
    }

    public void setClickMaxRetry(long maxRetry) { this.clickMaxRetry = maxRetry; }

    public long getDefaultTargetElementWaitTimeoutInSec() { return this.defaultTargetElementWaitTimeoutInSec; }

    public long getDefaultWaitForElementWaitTimeoutInSec() { return this.defaultWaitForElementWaitTimeoutInSec; }

    public long getClickMaxRetry() { return this.clickMaxRetry; }

    protected void simpleClick(WebElement element) {
        actions.click(element).build().perform();
    }

    protected void simpleClickWithOffset(WebElement element, int xOffset, int yOffset) {
        actions.moveToElement(element, xOffset, yOffset).click().build().perform();
    }

    protected void contextClick(WebElement element) { actions.contextClick(element).build().perform(); }

    protected void contextClickWithOffset(WebElement element, int xOffset, int yOffset) {
        actions.moveToElement(element, xOffset, yOffset).contextClick().build().perform();
    }

    protected void clickAndHold(WebElement element) { actions.clickAndHold(element).build().perform(); }

    protected void clickAndHoldWithOffset(WebElement element, int xOffset, int yOffset) {
        actions.moveToElement(element, xOffset, yOffset).clickAndHold().build().perform();
    }

    protected void moveMouseToElement(WebElement element) {
        if (driver instanceof RemoteWebDriver) {
            RemoteWebElement _element = (RemoteWebElement) element;
            Coordinates c = _element.getCoordinates();
            ((RemoteWebDriver) driver).getMouse().mouseMove(c);
        }
        else {
            actions.moveToElement(element).build().perform();
        }
        sleep(300);
    }

    protected void moveMouseToElement(WebElement element, int xOffset, int yOffset) {
        if (driver instanceof RemoteWebDriver) {
            RemoteWebElement _element = (RemoteWebElement) element;
            Coordinates c = _element.getCoordinates();
            ((RemoteWebDriver) driver).getMouse().mouseMove(c, xOffset, yOffset);
        }
        else {
            actions.moveToElement(element, xOffset, yOffset).build().perform();
        }
        sleep(300);
    }

    /*
    click and hold methods group:
     */

    /*
    click and hold "target" element and no need to wait for anything
    */
    public void clickAndHold(Element target) {
        _clickWithOffsetAndWait(target, 0, 0,null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK_AND_HOLD, WaitType.PRESENT);
    }

    /*
    click and hold "target" element with offset and no need to wait for anything
    */
    public void clickAndHoldWithOffset(Element target, int xOffset, int yOffset) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK_AND_HOLD, WaitType.PRESENT);
    }

    /*
    click and hold "target" element and wait for "waitFor" element
    */
    public void clickAndHoldAndWait(Element target, Element waitFor) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK_AND_HOLD, WaitType.PRESENT);
    }

    /*
    click and hold "target" element with offset and wait for "waitFor" element
    */
    public void clickAndHoldWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK_AND_HOLD, WaitType.PRESENT);
    }

    /*
    click and hold "target" element and wait for "waitFor" element
    customize timeout
    */
    public void clickAndHoldAndWait(Element target, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CLICK_AND_HOLD, WaitType.PRESENT);
    }

    /*
    click and hold "target" element with offset and wait for "waitFor" element
    customize timeout
    */
    public void clickAndHoldWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CLICK_AND_HOLD, WaitType.PRESENT);
    }

    /*
    release "target" element after "click and hold"
     */
    public void release(Element target) {
        actions.release(findElement(target));
    }

    /*
    hover methods group:
     */

    /*
    hover "target" element and no need to wait for anything
    */
    public void hover(Element target) {
        _clickWithOffsetAndWait(target, 0, 0,null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.HOVER, WaitType.PRESENT);
    }

    /*
    hover "target" element with offset and no need to wait for anything
    */
    public void hoverWithOffset(Element target, int xOffset, int yOffset) {
        _clickWithOffsetAndWait(target, xOffset, yOffset,null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.HOVER, WaitType.PRESENT);
    }

    /*
    hover "target" element and wait for "waitFor" element
    */
    public void hoverAndWait(Element target, Element waitFor) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.HOVER, WaitType.PRESENT);
    }

    /*
    hover "target" element with offset and wait for "waitFor" element
    */
    public void hoverWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.HOVER, WaitType.PRESENT);
    }

    /*
    hover "target" element and wait for "waitFor" element
    customize timeout
    */
    public void hoverAndWait(Element target, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.HOVER, WaitType.PRESENT);
    }

    /*
    hover "target" element with offset and wait for "waitFor" element
    customize timeout
    */
    public void hoverWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.HOVER, WaitType.PRESENT);
    }

    /*
    context click methods group:
     */

    /*
    right click "target" element and no need to wait for anything
    */
    public void contextClick(Element target) {
        _clickWithOffsetAndWait(target, 0, 0,null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CONTEXT_CLICK, WaitType.PRESENT);
    }

    /*
    right click "target" element with offset and no need to wait for anything
    */
    public void contextClickWithOffset(Element target, int xOffset, int yOffset) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CONTEXT_CLICK, WaitType.PRESENT);
    }

    /*
    right click "target" element and wait for "waitFor" element
    */
    public void contextClickAndWait(Element target, Element waitFor) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CONTEXT_CLICK, WaitType.PRESENT);
    }

    /*
    right click "target" element with offset and wait for "waitFor" element
    */
    public void contextClickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CONTEXT_CLICK, WaitType.PRESENT);
    }

    /*
    right click "target" element and wait for "waitFor" element
    customize timeout
    */
    public void contextClickAndWait(Element target, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CONTEXT_CLICK, WaitType.PRESENT);
    }

    /*
    right click "target" element with offset and wait for "waitFor" element
    customize timeout
    */
    public void contextClickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CONTEXT_CLICK, WaitType.PRESENT);
    }

    /*
    simple click methods group:
     */

    /*
    keep clicking "target" element until the "target" element disappeared
    */
    public void keepClickingUntilDisappeared(Element target) {
        do {
            click(target);
            sleep(1500);
        }
        while (isExist(target));
    }

    /*
    click "target" element and no need to wait for anything
    */
    public void click(Element target) {
        _clickWithOffsetAndWait(target, 0, 0,null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.PRESENT);
    }

    /*
    click "target" element with offset and no need to wait for anything
    */
    public void clickWithOffset(Element target, int xOffset, int yOffset) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, null, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.PRESENT);
    }

    /*
    click "target" element and wait for "waitFor" element
    */
    public void clickAndWait(Element target, Element waitFor) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.PRESENT);
    }

    /*
    click "target" element and wait for "waitFor" element disappearing
    */
    public void clickAndWaitUntilDisappear(Element target, Element waitFor) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.DISAPPEAR);
    }

    /*
    click "target" element with offset and wait for "waitFor" element
    */
    public void clickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.PRESENT);
    }

    /*
    click "target" element with offset and wait for "waitFor" element disappearing
    */
    public void clickWithOffsetAndWaitUntilDisappear(Element target, int xOffset, int yOffset, Element waitFor) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, defaultWaitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.DISAPPEAR);
    }

    /*
    click "target" element and wait for "waitFor" element
    customize timeout
    */
    public void clickAndWait(Element target, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.PRESENT);
    }

    /*
    click "target" element and wait for "waitFor" element disappearing
    customize timeout
    */
    public void clickAndWaitUntilDisappear(Element target, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, 0, 0, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.DISAPPEAR);
    }

    /*
    click "target" element with offset and wait for "waitFor" element
    customize timeout
    */
    public void clickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.PRESENT);
    }

    /*
    click "target" element with offset and wait for "waitFor" element disappearing
    customize timeout
    */
    public void clickWithOffsetAndWaitUntilDisappear(Element target, int xOffset, int yOffset, Element waitFor, long waitForElementWaitTimeoutInSec) {
        _clickWithOffsetAndWait(target, xOffset, yOffset, waitFor, defaultTargetElementWaitTimeoutInSec, waitForElementWaitTimeoutInSec, ClickType.CLICK, WaitType.DISAPPEAR);
    }

    protected void _clickWithOffsetAndWait(Element target, int xOffset, int yOffset, Element waitFor, long targetElementWaitTimeoutInSec, long waitForElementWaitTimeoutInSec, ClickType clickType, WaitType waitType) throws RuntimeException {

        /*
        step 1:
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

        if (waitFor == null) {

            /*
            step 2-1:
            when we say we "find" the target element
            it means "target" element is present on the DOM of the page
            and the width and height are greater than 0
            */
            WebElement targetElement = targetElementWait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath(target.getXpath())));

            scrollToElementAlignCenter(target);

            /*
            step 2-2:
            if no need to wait for the other element
            just click target and return
            */
            switch (clickType) {
                case CLICK:
                    if (xOffset != 0 || yOffset != 0) simpleClickWithOffset(targetElement, xOffset, yOffset);
                    else simpleClick(targetElement);
                    break;
                case CONTEXT_CLICK:
                    if (xOffset != 0 || yOffset != 0) contextClickWithOffset(targetElement, xOffset, yOffset);
                    else contextClick(targetElement);
                    break;
                case CLICK_AND_HOLD:
                    if (xOffset != 0 || yOffset != 0) clickAndHoldWithOffset(targetElement, xOffset, yOffset);
                    else clickAndHold(targetElement);
                    break;
                case HOVER:
                    if (xOffset != 0 || yOffset != 0) moveMouseToElement(targetElement, xOffset, yOffset);
                    else moveMouseToElement(targetElement);
                    break;
                default:
                    throw new RuntimeException("Unsupported Click Type: " + clickType);
            }
        }
        else {

            boolean success = false;
            long retry = 0;
            do {
                try {

                    /*
                    step 3-1:
                    when we say we "find" the target element
                    it means "target" element is present on the DOM of the page
                    and the width and height are greater than 0
                    */
                    WebElement targetElement = targetElementWait.until(
                            ExpectedConditions.visibilityOfElementLocated(By.xpath(target.getXpath())));

                    scrollToElementAlignCenter(target);

                    sleep(250);

                    /*
                    step 3-2:
                    if need to wait for the other element
                    click target and wait for "waitFor" element
                    if "waitFor" element not found after timeout
                    click target again (retry)
                    */
                    switch (clickType) {
                        case CLICK:
                            if (xOffset != 0 || yOffset != 0) simpleClickWithOffset(targetElement, xOffset, yOffset);
                            else simpleClick(targetElement);
                            break;
                        case CONTEXT_CLICK:
                            if (xOffset != 0 || yOffset != 0) contextClickWithOffset(targetElement, xOffset, yOffset);
                            else contextClick(targetElement);
                            break;
                        case CLICK_AND_HOLD:
                            if (xOffset != 0 || yOffset != 0) clickAndHoldWithOffset(targetElement, xOffset, yOffset);
                            else clickAndHold(targetElement);
                            break;
                        case HOVER:
                            if (xOffset != 0 || yOffset != 0) moveMouseToElement(targetElement, xOffset, yOffset);
                            else moveMouseToElement(targetElement);
                            break;
                        default:
                            throw new RuntimeException("Unsupported Click Type: " + clickType);
                    }

                    Wait<WebDriver> waitForElementWait = new FluentWait<WebDriver>(driver) {
                        @Override
                        protected RuntimeException timeoutException(String Message, Throwable lastException) {
                            throw new RuntimeException("Click " + target.getName() + " and Wait for " + waitFor.getName() + " Fail! " + Message);
                        }
                    }.withTimeout(Duration.ofSeconds(waitForElementWaitTimeoutInSec))
                            .pollingEvery(Duration.ofMillis(pollingIntervalInMillis))
                            .ignoring(Exception.class);

                    if (waitType == WaitType.PRESENT) {
                        waitForElementWait.until(
                                ExpectedConditions.visibilityOfElementLocated(By.xpath(waitFor.getXpath())));
                    }
                    else {
                        waitForElementWait.until(
                                ExpectedConditions.invisibilityOfElementLocated(By.xpath(waitFor.getXpath())));
                    }
                    success = true;
                }
                /*
                catch the runtime exception we defined above
                so we can retry (click again) instead of throwing an exception
                 */
                catch (Exception e) {
                    System.out.println(e + " retry: " + retry);
                }
            } while (!success && ++retry < clickMaxRetry);

            if (!success) {
                if (waitType == WaitType.PRESENT) {
                    throw new RuntimeException("Click " + target.getName() + " and wait for " + waitFor.getName() + " fail after timeout " + clickMaxRetry * waitForElementWaitTimeoutInSec + " s");
                }
                else {
                    throw new RuntimeException("Click " + target.getName() + " and wait for " + waitFor.getName() + " disappearing fail after timeout " + clickMaxRetry * waitForElementWaitTimeoutInSec + " s");
                }
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

    public WebElement findElement(Element target) {
        boolean found = tryToFind(target);
        if (!found) throw new RuntimeException("Element " + target.getName() + " not Found!");

        List<WebElement> list = driver.findElements(By.xpath(target.getXpath()));
        if (list.size() == 1) return list.get(0);
        else throw new RuntimeException("More than one element found! Use Operation.getElements() instead.");
    }

    public List<WebElement> findElements(Element target) {
        boolean found = tryToFind(target);
        if (!found) throw new RuntimeException("Element " + target.getName() + " not Found!");

        List<WebElement> list = driver.findElements(By.xpath(target.getXpath()));
        return list;
    }

    public void sendText(Element target, String text) {

        if (!isIE()) {
            click(target);
            WebElement element = findElement(target);
            element.clear();
            element.sendKeys(text);
        }
        else {
            // IE send keys too slow, so it's possible parts of text are missing
            // keep retry...
            long count = 0;
            do {
                click(target);
                WebElement element = findElement(target);
                element.clear();
                element.sendKeys(text);
                count++;
            } while(!findElement(target).getAttribute("value").equals(text) && (count < 65535));
            /*
            click(target);
            WebElement element = findElement(target);
            element.sendKeys("dummy");
            sleep(1000);
            js.executeScript("arguments[0].innerHTML=arguments[1]", element, text);
            sleep(1000);
             */
        }
    }

    public void dragAndDropFile(Element fileArea, String filePath) {
        driver.findElement(By.xpath(fileArea.getXpath())).sendKeys(filePath);
    }

    public void selectDropdownMenuOptionByValue(Element dropdown, String valueToBeSelected) {
        Select select = new Select(findElement(dropdown));
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

    public String getCurrentUrl() { return driver.getCurrentUrl(); }

    public void saveCurrentTabAsDefaultTab() {
        defaultTabHandle = driver.getWindowHandle();
    }

    public String getCurrentTabHandle() {
        return driver.getWindowHandle();
    }

    public String getDefaultTabHandle() {
        return defaultTabHandle;
    }

    public void clickAlertOK() {
        driver.switchTo().alert().accept();
    }

    public void switchToIframe(Element target) {
        if (!target.getXpath().contains("iframe")) throw new RuntimeException("target of switchToIframe should be an iframe");
        driver.switchTo().frame(findElement(target));
    }

    public void switchFromIframeToMainHTML() {
        driver.switchTo().defaultContent();
    }

    public void scrollWindowTo(String xOffset, String yOffset) {
        js.executeScript("window.scrollTo(arguments[0],arguments[1]);", xOffset, yOffset);
    }

    public void scrollToElement(Element target) {
        if (isIE()) System.out.println("[Warning] scrollIntoView not support in IE");
        else js.executeScript("arguments[0].scrollIntoView();", findElement(target));
    }

    public void scrollToElementAlignCenter(Element target) {
        if (isIE()) System.out.println("[Warning] scrollIntoView not support in IE");
        else js.executeScript("arguments[0].scrollIntoView({block: 'center'});", findElement(target));
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
    public String searchForRequestUrlFromLoggingPrefs(int timeoutInSec, String... keywords) {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        return loggingPrefs.searchForRequestUrlFromKeywords(timeoutInSec, keywords);
    }

    public Map<String, Object> getResponseFromLoggingPrefs(int timeoutInSec, String... keywords) {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        return loggingPrefs.getResponseFromRequestUrlKeywords(timeoutInSec, keywords);
    }

    public List<String> getAllRequestUrlsFromLoggingPrefs() {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        return loggingPrefs.getAllRequestUrls();
    }

    public List<Map<String, String>> getAllResponseUrlsFromLoggingPrefs() {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        return loggingPrefs.getAllResponseUrls();
    }

    public void resetLoggingPrefs() {
        if (loggingPrefs == null) throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        loggingPrefs.reset();
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

    public byte[] screenshot() {

        byte[] screenshotData = null;

        try {
            screenshotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (WebDriverException | ClassCastException e) {
            System.err.println(e.getMessage());
        }

        return screenshotData;
    }

    public void screenshotAndEmbedInCucumberReport(Scenario scenario) {

        try {
            byte[] screenshotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshotData, "image/png", scenario.getName());
        } catch (WebDriverException | ClassCastException e) {
            System.err.println(e.getMessage());
        }

    }

}
