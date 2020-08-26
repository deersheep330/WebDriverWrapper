package deersheep.automation.webdriver.enums;

public enum Browser {

    Chrome("Chrome"),
    IE("IE"),
    MobileChrome("MobileChrome");

    private String strValue;

    Browser(String value) {
        this.strValue = value;
    }

    public String getStrValue() {
        return strValue;
    }

}