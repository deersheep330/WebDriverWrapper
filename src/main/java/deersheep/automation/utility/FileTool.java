package deersheep.automation.utility;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileTool {

    public static File getLastModifiedFile(File directory) {
        File[] files = directory.listFiles();
        if (files.length == 0) return null;
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
            }});
        return files[0];
    }

}
