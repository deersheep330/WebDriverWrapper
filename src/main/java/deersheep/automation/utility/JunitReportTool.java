package deersheep.automation.utility;

import java.nio.file.Path;

import static deersheep.automation.utility.StringTool.replaceTextInFile;

public class JunitReportTool {

    /*
    (usage 1)
    the default junit report class (display) name would be the full package name of the test case
    e.g. com.yourcompany.automation.projectname.testclassname
    the name maybe too long or maybe you want a more simple and expressive name

    (usage 2)
    the default junit report class (display) name of a cucumber test would be the feature name
    but if you have 2 cucumber runners share the same feature file, the report class name would be the same

    use this method to modify junit report class name
     */
    public static void modifyJunitReportClassName(Path junitReportFilePath, String origClassName, String newClassName) {

        try {
            replaceTextInFile(junitReportFilePath, origClassName, newClassName);
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }

}
