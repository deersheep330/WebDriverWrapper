package deersheep.automation.loggingprefs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class LoggingPrefs {

    protected WebDriver driver;

    protected boolean saveCookieEnabled = true;
    protected String storedCookie;
    protected Set<String> storedHeaderNames;
    protected Map<String, String> storedHeadersPairs;

    protected int readPtr = 0;

    public LoggingPrefs(WebDriver driver) {
        Capabilities capabilities = ((HasCapabilities) driver).getCapabilities();
        if (false && capabilities.getCapability("goog:loggingPrefs") == null) {
            throw new RuntimeException("capability goog:loggingPrefs should be enabled");
        }

        this.driver = driver;
        this.storedHeaderNames = new HashSet<>();
        this.storedHeadersPairs = new HashMap<>();
    }

    public void reset() {
        List<LogEntry> list = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
        readPtr = (list.size() > 0) ? list.size() - 1 : 0;
        System.out.println("update readPtr to " + readPtr);
    }

    public Map<String, Object> getResponseFromRequestUrlKeywords(int timeoutInSec, String... keywords) {

        ObjectMapper mapper = new ObjectMapper();

        /*
        sometimes the request has been sent but it's not been logged yet
        so try again
         */
        int retry = 0, sleepTime = 3000, maxRetry = timeoutInSec * 1000 / sleepTime;
        while (retry++ < maxRetry) {

            List<LogEntry> list = driver.manage().logs().get(LogType.PERFORMANCE).getAll();

            System.out.println("total logs count = " + list.size() + ", readPtr = " + readPtr);

            for (int i = list.size() - 1; i >= readPtr; i--) {

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
                            return response;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException("Unable to find request url with keywords: " + Arrays.toString(keywords));

    }

    public String searchForRequestUrlFromKeywords(String... keywords) {

        ObjectMapper mapper = new ObjectMapper();

        /*
        sometimes the request has been sent but it's not been logged yet
        so try again
         */
        int retry = 0, maxRetry = 3;
        while (retry++ < maxRetry) {

            List<LogEntry> list = driver.manage().logs().get(LogType.PERFORMANCE).getAll();

            for (int i = list.size() - 1; i >= readPtr; i--) {

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

        throw new RuntimeException("Unable to find request url with keywords: " + Arrays.toString(keywords));

    }

    public void enableSaveCookie(boolean enabled) {
        saveCookieEnabled = enabled;
    }

    public void saveSpecialHeader(String headerName) {
        storedHeaderNames.add(headerName);
    }

    public HttpURLConnection resendRequestWithStoredCookieAndHeaders(String url) {

        HttpURLConnection connection = null;
        Exception ex = null;
        int retry = 0, maxRetry = 3;

        while (retry++ < maxRetry) {
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
                if (storedCookie != null) {
                    connection.setRequestProperty("Cookie", storedCookie);
                }
                if (storedHeadersPairs != null && storedHeadersPairs.size() != 0) {
                    for (Map.Entry<String, String> pair : storedHeadersPairs.entrySet()) {
                        connection.setRequestProperty(pair.getKey(), pair.getValue());
                    }
                }
            } catch (IOException e) {
                ex = e;
                connection = null;
            }
        }

        if (connection == null) throw new RuntimeException(ex.getMessage());
        else return connection;
    }

}
