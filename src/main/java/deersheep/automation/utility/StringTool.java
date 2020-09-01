package deersheep.automation.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class StringTool {

    public static void replaceTextInFile(Path filePath, String toBeReplaced, String replaceWith) {

        Charset charset = StandardCharsets.UTF_8;

        String content;
        try {
            content = new String(Files.readAllBytes(filePath), charset);
            content = content.replaceAll(toBeReplaced, replaceWith);
            Files.write(filePath, content.getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
