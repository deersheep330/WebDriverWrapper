package deersheep.automation.browsermob;

public class HarEntryWrapper {

    public String url;
    public String reqPostData;
    public String respContent;

    public HarEntryWrapper(String url, String postData, String content) {
        this.url = url;
        this.reqPostData = postData;
        this.respContent = content;
    }

}
