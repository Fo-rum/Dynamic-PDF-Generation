import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class PdfGeneratorService {

    private final TemplateEngine templateEngine;

    public PdfGeneratorService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public InputStream generatePdfFromHtml(Map<String, Object> data, String template) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        document.open();

        // Convert HTML to PDF using Thymeleaf template engine
        String html = templateEngine.process(template, new Context(null, data));
        Font font = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph paragraph = new Paragraph("PDF generated using iText and Thymeleaf", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph(html));

        document.close();

        // Return the PDF as an InputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return inputStream;
    }
}

