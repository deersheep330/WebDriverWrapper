package deersheep.automation.browsermob;

import deersheep.automation.utility.JsonTool;

public class HarEntryWrapper {

    public String url;
    public String reqPostData;
    public String respContent;

    public HarEntryWrapper(String url, String postData, String content) {
        this.url = url;
        this.reqPostData = postData;
        this.respContent = content;
    }

    @Override
    public String toString() {
        return JsonTool.pretty(String.format("{ \"url\": %s, \"postData\": %s, \"content\": %s }", url, reqPostData, respContent));
    }
}
