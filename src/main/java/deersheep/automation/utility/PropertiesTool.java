package deersheep.automation.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesTool {

    protected static String postfix = ".properties";

    /*
    default postfix is ".properties"
    e.g. default.properties
     */
    public static void setPostfix(String _postfix) {
        if (_postfix.charAt(0) == '.') {
            postfix = _postfix;
        }
        else {
            postfix = "." + _postfix;
        }
        System.out.println("==> set properties file postfix to " + postfix);
    }

    /*
    filename is relative to src/resources
    e.g. provide filename = default.properties
         the file path would be src/resources/default.properties
     */
    public static void storeProperty(String filename, String key, String value) {

        Properties props = new Properties();

        filename = filename + postfix;
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "src", "resources", filename);

        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(filePath.toString());
            props.load(in);
            in.close();

            out = new FileOutputStream(filePath.toString());
            props.setProperty(key, value);
            props.store(out, null);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    filename is relative to src/resources
    e.g. provide filename = default.properties
         the file path would be src/resources/default.properties
     */
    public static String getProperty(String filename, String key) {

        Properties prop = new Properties();

        filename = filename + postfix;
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "src", "resources", filename);

        InputStream in = null;
        try {
            in = new FileInputStream(filePath.toString());
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop.getProperty(key);
    }

    /*
    filename is relative to src/resources
    e.g. provide filename = default.properties
         the file path would be src/resources/default.properties
     */
    public static Properties getAllProperties(String filename) {

        Properties prop = new Properties();

        filename = filename + postfix;
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "src", "resources", "config", filename);

        InputStream in = null;
        try {
            in = new FileInputStream(filePath.toString());
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop;
    }

}
