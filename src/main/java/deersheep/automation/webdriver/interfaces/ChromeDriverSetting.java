package deersheep.automation.webdriver.interfaces;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Level;

public class ChromeDriverSetting implements WebDriverSettingAbility {

    @Override
    public Capabilities getCapabilities() {

        ChromeOptions options = new ChromeOptions();

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        return options;
    }

    @Override
    public String getName() {
        return "Chrome";
    }
}
