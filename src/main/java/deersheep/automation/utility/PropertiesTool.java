package deersheep.automation.utility;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class PropertiesTool {

    protected static String suffix = ".properties";
    protected static Path propertiesFolderPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources");

    public String getSuffix() {
        return suffix;
    }

    /*
    default suffix is ".properties"
    e.g. default.properties
     */
    public static void setSuffix(String _suffix) {
        if (_suffix.charAt(0) == '.') {
            suffix = _suffix;
        }
        else {
            suffix = "." + _suffix;
        }
        System.out.println("==> set properties file suffix to " + suffix);
    }

    public String getPropertiesFolderPath() {
        return propertiesFolderPath.toString();
    }

    public void setPropertiesFolderPath(Path path) {
        propertiesFolderPath = path;
    }

    /*
    filename is relative to src/resources
    e.g. provide filename = default.properties
         the file path would be src/resources/default.properties
     */
    public static boolean storeProperty(String filename, String key, String value) {

        Properties props = new Properties();

        filename = filename + suffix;
        Path filePath = Paths.get(propertiesFolderPath.toString(), filename);

        FileInputStream in = null;
        FileOutputStream out = null;
        File f = null;
        try {
            f = new File(filePath.toString());
            if (!f.exists()) f.createNewFile();
            in = new FileInputStream(filePath.toString());
            props.load(in);
            in.close();

            out = new FileOutputStream(filePath.toString());
            props.setProperty(key, value);
            props.store(out, null);
            out.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /*
    filename is relative to src/resources
    e.g. provide filename = default.properties
         the file path would be src/resources/default.properties
     */
    public static String getProperty(String filename, String key) {

        Properties prop = new Properties();

        filename = filename + suffix;
        Path filePath = Paths.get(propertiesFolderPath.toString(), filename);

        InputStream in = null;
        try {
            in = new FileInputStream(filePath.toString());
            prop.load(in);
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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

        filename = filename + suffix;
        Path filePath = Paths.get(propertiesFolderPath.toString(), filename);

        InputStream in = null;
        try {
            in = new FileInputStream(filePath.toString());
            prop.load(in);
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return prop;
    }

    public static void generateBuildNumber() {

        StringBuilder buildNumber = new StringBuilder("");

        String env = getProperty("environment","env");
        if (env == null) {
            System.out.println("environment" + suffix + " file not found! build number would not include env variable.");
        }
        else if (env.equals("")) {
            System.out.println("there is no env variable in environment" + suffix + " file! build number would not include env variable.");
        }
        else {
            buildNumber.append(env).append("-");
        }


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date date = new Date();
        buildNumber.append(dateFormat.format(date));

        boolean res = storeProperty("buildNum", "build", buildNumber.toString());
        if (!res) {
            System.out.println("Cannot find buildNum" + suffix + " file to store build number!");
        }

        System.out.println("==> Generate Build Number: " + buildNumber);
    }

}
