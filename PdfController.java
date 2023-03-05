import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class PdfController {

    @PostMapping(value = "/generate-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfData pdfData) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Create a map of data to be used by Thymeleaf template
        Map<String, Object> thymeleafData = new HashMap<>();
        thymeleafData.put("pdfData", pdfData);

        // Render HTML using Thymeleaf template engine
        String html = ThymeleafUtil.renderThymeleafTemplate("pdf-template.html", thymeleafData);

        // Convert HTML to PDF using iText library
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes());
        ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, pdfOutput);
        XMLWorkerHelper.getInstance().parseXHtml(stamper, reader, inputStream);
        stamper.close();
        document.close();

        // Set the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "generated.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfOutput.toByteArray(), headers, HttpStatus.OK);
    }
}