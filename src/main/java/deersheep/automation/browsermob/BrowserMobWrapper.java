package deersheep.automation.browsermob;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import deersheep.automation.utility.JsonTool;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarContent;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarPostData;
import net.lightbody.bmp.proxy.CaptureType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BrowserMobWrapper {

    protected static BrowserMobProxy browserMobProxy = null;

    protected static HashMap<String, HarEntryWrapper> entriesCollection = new HashMap<>();

    public void addToEntriesCollection(String key, HarEntryWrapper entry) {

        if (browserMobProxy == null) {
            return;
        }
        else if (entriesCollection.containsKey(key)) {
            System.out.println("duplicated key:");
            System.out.println(entriesCollection.get(key));
            //entriesCollection.put(key, entry);
        }
        else {
            entriesCollection.put(key, entry);
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

    public HashMap<String, HarEntryWrapper> readEntriesCollectionFromFile(File file) {
        HashMap<String, HarEntryWrapper> tempMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            tempMap = mapper.readValue(file, new TypeReference<HashMap<String, HarEntryWrapper>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return tempMap;
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

    public void startNewRecording() {

        if (browserMobProxy == null) {
            return;
        }
        else {
            browserMobProxy.newHar();
        }
    }

    public List<HarEntryWrapper> searchForKeywordsFromRecordedLogs(String... keywords) {
        if (browserMobProxy != null) {

            Har har = browserMobProxy.getHar();
            List<HarEntry> logs = har.getLog().getEntries();
            System.out.println("log size = " + logs.size());

            List<HarEntryWrapper> res = new ArrayList<>();

            for (HarEntry log : logs) {
                for (String str : keywords) {
                    if (log.getRequest().getUrl().contains(str)) {

                        HarContent _contentForMatching = log.getResponse().getContent();
                        String contentForMatching = (_contentForMatching == null) ? "" : _contentForMatching.getText();
                        contentForMatching = (contentForMatching.length() > 900) ? contentForMatching.substring(0, 900) : contentForMatching;

                        HarContent _content = log.getResponse().getContent();
                        String content = (_content == null) ? "" : _content.getText();

                        HarPostData _postData = log.getRequest().getPostData();
                        String postData = (_postData == null) ? "" : _postData.getText();

                        if (contentForMatching.contains("progress\":") && contentForMatching.contains("progress\":1")) {
                            res.add(new HarEntryWrapper(log.getRequest().getUrl(),
                                                        postData,
                                                        content));
                        }
                        else if (!contentForMatching.contains("progress\":")) {
                            res.add(new HarEntryWrapper(log.getRequest().getUrl(),
                                                        postData,
                                                        content));
                        }
                        break;
                    }
                }
            }

            return res;
        }

        return new ArrayList<HarEntryWrapper>();
    }

}
