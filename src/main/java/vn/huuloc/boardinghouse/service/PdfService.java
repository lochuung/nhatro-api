package vn.huuloc.boardinghouse.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class PdfService {
    public static final String ARIAL = "Arial";
    @Value("${app.templates.pdf.url:/templates/}")
    private String templateUrl;

    public byte[] generatePdfFromSource(String type, Context context) throws IOException {
        String content = parseTemplateToXhtml(context, type);
        return generatePdf(content);
    }


    private String parseTemplateToXhtml(Context context, String templateName) throws IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(templateUrl);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(UTF_8.name());

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine.process(templateName, context);
    }

    private static byte[] generatePdf(String content) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
//        builder.useFont(new File(Objects.requireNonNull(PdfService.class.getClassLoader()
//                .getResource("templates/fonts/Petrona-Regular.ttf")).getFile()
//        ), "Petrona-Regular");
//        builder.useFont(new File(Objects.requireNonNull(PdfService.class.getClassLoader()
//                .getResource("templates/fonts/SVN-Arial-Regular.ttf")).getFile()
//        ), "SVN-Arial");
        InputStream is = PdfService.class.getResourceAsStream("/templates/fonts/Arial.ttf");
        if (is == null) {
            throw BadRequestException.message("Font path is invalid");
        }
        File fontFile = FileUtils.convertInputStreamToFile(is, ARIAL);
        builder.useFont(fontFile, ARIAL);
        builder.withHtmlContent(content, null);
        //A5
//        builder.useDefaultPageSize(5.8f, 8.3f, BaseRendererBuilder.PageSizeUnits.INCHES);
        builder.toStream(outputStream);
        builder.useFastMode();
        builder.testMode(true);
        builder.run();

//        if (password != null) {
//            PdfBoxRenderer renderer = builder.buildPdfRenderer();
//
//            AccessPermission p = new AccessPermission();
//            p.setCanAssembleDocument(false);
//            p.setCanExtractContent(true);
//            p.setCanExtractForAccessibility(true);
//            p.setCanFillInForm(true);
//            p.setCanModify(false);
//            p.setCanModifyAnnotations(true);
//            p.setCanPrint(false);
//            p.setCanPrintDegraded(false);
//            p.setReadOnly();
//
//            PDDocument doc = renderer.getPdfDocument();
//            doc.protect(new StandardProtectionPolicy(password, password, p));
//
//            renderer.layout();
//            renderer.createPDF();
//            renderer.cleanup();
//        } else {
//            builder.run();
//        }

        return outputStream.toByteArray();
    }

    private String readContent(String path) {
        try (InputStream htmlIs = PdfService.class.getResourceAsStream(path)) {
            if (htmlIs == null) {
                throw BadRequestException.message(path + " không hợp lệ");
            }
            byte[] htmlBytes = IOUtils.toByteArray(htmlIs);
            return new String(htmlBytes, UTF_8);
        } catch (IOException e) {
            throw BadRequestException.message(e.getMessage());
        }
    }
}
