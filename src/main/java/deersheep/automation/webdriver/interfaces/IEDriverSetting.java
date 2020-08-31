package deersheep.automation.webdriver.interfaces;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class IEDriverSetting implements WebDriverSettingAbility {

    @Override
    public Capabilities getCapabilities() {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
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
        return capabilities;
    }

    @Override
    public String getName() {
        return "IE";
    }
}
