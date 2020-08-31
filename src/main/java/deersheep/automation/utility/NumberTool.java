package deersheep.automation.utility;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberTool {

    protected static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    protected static Pattern numberPattern = Pattern.compile("[-,0-9]+");


    public static String findIntFromString(String str) {
        Matcher matcher = numberPattern.matcher(str);
        while(matcher.find()) {
            str = matcher.group();
        }
        return str;
    }

    public static int parseIntFromString(String str) throws ParseException {
        return numberFormat.parse(findIntFromString(str)).intValue();
    }
}
