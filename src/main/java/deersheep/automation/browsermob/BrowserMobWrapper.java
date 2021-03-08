package deersheep.automation.browsermob;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BrowserMobWrapper {

    protected static BrowserMobProxy browserMobProxy = null;

    protected HashMap<String, List<HarEntryWrapper>> entriesCollection = new HashMap<>();

    protected String currentKey;

    public void addToEntriesCollection(HarEntryWrapper entry) {

        if (browserMobProxy == null) {
            return;
        }
        else if (entriesCollection.containsKey(currentKey)) {
            entriesCollection.get(currentKey).add(entry);
        }
        else {
            ArrayList<HarEntryWrapper> list = new ArrayList<>();
            list.add(entry);
            entriesCollection.put(currentKey, list);
        }
    }

    public void clearEntriesCollection() {
        if (browserMobProxy == null) {
            return;
        }
        else {
            entriesCollection.clear();
        }
    }

    public void writeEntriesCollectionToFile(String filename) {

        if (browserMobProxy == null) return;

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(Paths.get(filename).toFile(), entriesCollection);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BrowserMobProxy initBrowserMobProxy() {

        if (browserMobProxy == null) {
            browserMobProxy = new BrowserMobProxyServer();
            browserMobProxy.start(0);
            browserMobProxy.enableHarCaptureTypes(
                    CaptureType.REQUEST_CONTENT,
                    CaptureType.RESPONSE_CONTENT,
                    CaptureType.RESPONSE_BINARY_CONTENT);
        }
        return browserMobProxy;
    }

    public void startRecordingWithKey(String key) {

        if (browserMobProxy == null) {
            return;
        }
        else {
            currentKey = key;
            browserMobProxy.newHar();
        }
    }

    public List<HarEntry> searchForKeywordsFromRecordedLogs(String... keywords) {
        if (browserMobProxy != null) {

            Har har = browserMobProxy.getHar();
            List<HarEntry> logs = har.getLog().getEntries();
            System.out.println("log size = " + logs.size());

            List<HarEntry> res = new ArrayList<>();

            for (HarEntry log : logs) {
                for (String str : keywords) {
                    if (log.getRequest().getUrl().contains(str)) {
                        String content = log.getResponse().getContent().getText();
                        content = (content == null) ? "" : content;
                        content = (content.length() > 900) ? content.substring(0, 900) : content;
                        if (content.contains("progress\":") && content.contains("progress\":1")) res.add(log);
                        else if (!content.contains("progress\":")) res.add(log);
                        break;
                    }
                }
            }

            return res;
        }

        return new ArrayList<HarEntry>();
    }
}
