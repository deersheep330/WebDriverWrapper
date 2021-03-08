package deersheep.automation.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class JsonTool {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String pretty(String str) {
        return gson.toJson(JsonParser.parseString(str));
    }
}
