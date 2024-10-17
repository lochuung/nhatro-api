package vn.huuloc.boardinghouse.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class FileUtils {
    public static HttpHeaders setResponseHeader(String fileName, String outputType) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, outputType + "; filename=" + fileName);
        return responseHeaders;
    }

    public static HttpHeaders setResponseHeader(String fileName, String outputType, String mediaType) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, mediaType);
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, outputType + "; filename=" + fileName);
        return responseHeaders;
    }

    public File convertInputStreamToFile(InputStream inputStream, String fileName) throws IOException {
        File file = new File(fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }
}
