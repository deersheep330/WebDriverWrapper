package deersheep.automation.webdriver.enums;

public enum Machine {

    Local("http://localhost:4040/wd/hub"),
    Remote("http://10.60.91.40:4040/wd/hub");

    private String url;

    Machine(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

}
