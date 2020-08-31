package deersheep.automation.utility;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class XlsxTool {

    public static XSSFWorkbook getWorkbookFromHttpURLConnection(HttpURLConnection connection) {
        InputStream is = null;
        XSSFWorkbook workbook = null;
        try {
            is = connection.getInputStream();
            workbook = new XSSFWorkbook(is);
            if (is != null) is.close();
            if (connection != null) connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public static XSSFSheet getDefaultSheetFromWorkbook(XSSFWorkbook workbook) {
        return workbook.getSheetAt(0);
    }

    public static int getRowCountOfSheet(XSSFSheet sheet) {
        return sheet.getLastRowNum();
    }

}
