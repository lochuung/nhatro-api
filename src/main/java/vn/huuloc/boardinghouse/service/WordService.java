package vn.huuloc.boardinghouse.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class WordService {
    public byte[] print(String templateName, Map<String, String> data) throws IOException {
        // Load the Word template from the resources folder
        try (InputStream templateStream = getClass().getClassLoader().getResourceAsStream(templateName)) {
            assert templateStream != null;
            try (XWPFDocument document = new XWPFDocument(templateStream);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                // Replace placeholders in the document
                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        String text = run.getText(0);
                        if (text != null) {
                            for (Map.Entry<String, String> entry : data.entrySet()) {
                                text = text.replace("{{" + entry.getKey() + "}}", entry.getValue());
                            }
                            run.setText(text, 0);
                        }
                    }
                }

                // Write the document content to the byte array output stream
                document.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }
}
