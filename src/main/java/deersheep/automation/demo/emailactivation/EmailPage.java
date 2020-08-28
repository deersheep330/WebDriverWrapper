package deersheep.automation.demo.emailactivation;

import deersheep.automation.operation.Operation;
import deersheep.automation.pageobject.BasePage;
import org.openqa.selenium.WebDriver;

public class EmailPage extends BasePage {

    protected String emailUrl = "https://mytemp.email/2/#!/";
    protected String emailAddress;
    protected String hash;

    protected Operation op;

    public EmailPage(WebDriver driver, String activationLetterXpath, String activationLinkXpath) {
        super(driver);
        this.op = new Operation(this.driver);

        addElement("inbox", "/html/body/md-sidenav/div/div[2]/md-list[1]/md-list-item/button/div[1]/span[1]");
        addElement("activationLetter", activationLetterXpath);
        addElement("activationLetterIframe", "//iframe[@id='body-html-iframe-content']");
        addElement("activationLink", activationLinkXpath);
    }

    /*
    the first time you navigate to the email page,
    a new email address would be created and recorded,
    and if you navigate to the email page again,
    the recorded email address would be used
     */
    public void navigate() {

        // create a new email or use exist one
        String url = (emailAddress != null && hash != null) ? emailUrl + "inbox/" + emailAddress + "/" + hash : emailUrl;

        // navigate to email page and wait it loading
        op.navigateTo(url);
        op.waitFor(elements.get("inbox"));

        // wait for url rewriting
        int retry = 0, maxRetry = 5;
        while (retry++ < maxRetry && driver.getCurrentUrl().split("/").length < 6) {
            op.sleep(3000);
        }
        if (driver.getCurrentUrl().split("/").length < 6) throw new RuntimeException("Cannot open email inbox: " + driver.getCurrentUrl());

        // if a new email created
        // record the email address and hash
        if (emailAddress == null || hash == null) {
            String currentUrl = driver.getCurrentUrl();
            System.out.println(currentUrl);
            String[] tokens = currentUrl.split("/");
            hash = tokens[tokens.length - 1];
            emailAddress = tokens[tokens.length - 2];
        }
    }

    public void checkInboxForActivationLetter() {
        boolean getActivationLetter = false;
        int retry = 0, maxRetry = 30;
        do {
            op.click(elements.get("inbox"));
            getActivationLetter = op.tryToFind(elements.get("activationLetter"));
            if (getActivationLetter) {
                break;
            }
            else {
                op.sleep(3000);
            }
        } while(retry++ < maxRetry);

        if (!getActivationLetter) throw new RuntimeException("Cannot get activation letter");
    }

    public void activateAccountByClickingActivationLink() {

        op.saveCurrentTabAsDefaultTab();

        op.clickAndWait(elements.get("activationLetter"), elements.get("activationLetterIframe"));

        op.switchToIframe(elements.get("activationLetterIframe"));

        int retry = 0, maxRetry = 3;
        while (retry++ < maxRetry) {
            op.click(elements.get("activationLink"));
            op.sleep(1500);
            if (op.IsNewTabBeingOpened()) {
                break;
            }
        }

        if (op.getCurrentOpenedTabsCount() == 1) {
            throw new RuntimeException("Cannot Open Activation Link");
        }

        op.switchFromIframeToMainHTML();
    }

    /*
     share the email address with other pages
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    public void quitAndCloseBrowser() {
        op.quitAndCloseBrowser();
    }

}
