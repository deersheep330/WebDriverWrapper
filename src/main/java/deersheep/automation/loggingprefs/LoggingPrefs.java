package deersheep.automation.loggingprefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.*;

public class LoggingPrefs {

    protected WebDriver driver;

    protected boolean saveCookieEnabled = true;
    protected String storedCookie;
    protected Set<String> storedHeaderNames;
    protected Map<String, String> storedHeadersPairs;

    public LoggingPrefs(WebDriver driver) {
        Capabilities capabilities = ((HasCapabilities) driver).getCapabilities();
        if (capabilities.getCapability("goog:loggingPrefs") == null) {
            throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        }

        this.driver = driver;
        this.storedHeaderNames = new HashSet<>();
        this.storedHeadersPairs = new HashMap<>();
    }

    public String getRequestUrl(String... keywords) {

        ObjectMapper mapper = new ObjectMapper();

        /*
        sometimes the request has been sent but it's not been logged yet
        so try again
         */
        int retry = 0, maxRetry = 3;
        while (retry++ < maxRetry) {

            List<LogEntry> list = driver.manage().logs().get(LogType.PERFORMANCE).getAll();

            for (int i = list.size() - 1; i >= 0; i--) {

                if (list.get(i).getMessage().contains("Network.requestWillBeSentExtraInfo")) {
                    try {

                        Map<String, Object> root = mapper.readValue(list.get(i).getMessage(), Map.class);
                        Map<String, Object> message = (Map<String, Object>) root.get("message");
                        Map<String, Object> params = (Map<String, Object>) message.get("params");
                        Map<String, Object> headers = (Map<String, Object>) params.get("headers");

                        String cookie = (String) headers.get("cookie");
                        if (cookie.contains("SESSION")) {
                            storedCookie = cookie;
                            System.out.println("store cookie: " + storedCookie);
                        }

                        for (String name : storedHeaderNames) {
                            String value = (String) headers.get(name);
                            storedHeadersPairs.put(name, value);
                            System.out.println("store " + name + " : " + value);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (list.get(i).getMessage().contains("Network.responseReceived")) {
                    try {

                        Map<String, Object> root = mapper.readValue(list.get(i).getMessage(), Map.class);
                        Map<String, Object> message = (Map<String, Object>) root.get("message");
                        Map<String, Object> params = (Map<String, Object>) message.get("params");
                        Map<String, Object> response = (Map<String, Object>) params.get("response");

                        String url = (String) response.get("url");
                        boolean matched = true;
                        System.out.println(url);
                        for (String str : keywords) {
                            if (!url.contains(str)) {
                                matched = false;
                                break;
                            }
                        }

                        if (matched) {
                            return url;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(1500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException("Unable to find Url with keywords: " + keywords);

    }

    public void enableSaveCookie(boolean enabled) {
        saveCookieEnabled = enabled;
    }

    public void saveSpecialHeader(String headerName) {
        storedHeaderNames.add(headerName);
    }

}
