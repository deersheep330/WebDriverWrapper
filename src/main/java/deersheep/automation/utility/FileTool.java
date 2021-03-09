package deersheep.automation.utility;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FileTool {

    public static File getLastModifiedFile(File directory) {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) return null;
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }});
        return files[0];
    }

    public static void writeMapToFile(String filePath, Map<String, String> map, String delimiter) {

        File file = new File(filePath);

        BufferedWriter bf;
        try {
            bf = new BufferedWriter(new FileWriter(file));

            for (Map.Entry<String, String> entry : map.entrySet()) {
                // put key and value separated by a colon
                bf.write(entry.getKey() + delimiter + entry.getValue());
                // new line
                bf.newLine();
            }

            bf.flush();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> readMapFromFile(String filePath, String delimiter) {

        Map<String, String> map = new HashMap<>();
        BufferedReader br;

        try {
            File file = new File(filePath);
            br = new BufferedReader(new FileReader(file));

            String line;
            // read file line by line
            while ((line = br.readLine()) != null) {
                // split the line by :
                String[] parts = line.split(delimiter);
                // first part is key, second is value
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (!key.equals("") && !value.equals("")) map.put(key, value);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
