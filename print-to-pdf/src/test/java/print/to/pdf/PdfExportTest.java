package print.to.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.ramussoft.gui.common.print.RamusPrintable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.Action;

/**
 * Headless regression test for the PDF export pipeline (OpenPDF).
 * <p>
 * Mirrors what {@link Plugin} does: AWT graphics are rendered through
 * {@code PdfGraphics2D} with a font mapper that embeds the bundled
 * AGHelvetica TTF via IDENTITY_H, so Cyrillic text must survive both the
 * rendering and the copy-paste (ToUnicode) path.
 */
public class PdfExportTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /**
     * End-to-end test of the real {@link Plugin} pipeline (its font mapper,
     * A3 page setup and PdfGraphics2D rendering) with a printable that draws
     * Cyrillic text — the same path the user's Print → PDF menu action takes.
     */
    @Test
    public void realPluginPrintsCyrillicPage() throws Exception {
        File out = tmp.newFile("plugin-output.pdf");

        Plugin plugin = new Plugin();
        Action action = plugin.getPrintAction(null, new CyrillicPrintable());
        Method printToPdf = action.getClass().getDeclaredMethod("printToPDF", File.class);
        printToPdf.setAccessible(true);
        printToPdf.invoke(action, out);

        byte[] bytes = Files.readAllBytes(out.toPath());
        Assert.assertEquals("%PDF-", new String(bytes, 0, 5, "US-ASCII"));
        Assert.assertTrue("the TTF font should be embedded (FontFile marker missing)",
                containsAsciiMarker(bytes, "FontFile"));

        PdfReader reader = new PdfReader(out.getAbsolutePath());
        try {
            String text = new PdfTextExtractor(reader).getTextFromPage(1);
            // Note: the bundled AGHelvetica.TTF has no glyph for 'ё', so it is
            // dropped from the output (the same was true with the original
            // iText pipeline — this is a font limitation, not a regression).
            Assert.assertTrue("extracted text should contain cyrillic, was: <" + text + ">",
                    text.contains("Диаграмма процесса") && text.contains("создание"));
        } finally {
            reader.close();
        }
    }

    /** Minimal printable that draws a line of Cyrillic text on one page. */
    private static class CyrillicPrintable implements RamusPrintable {
        @Override public int getPageCount() { return 1; }
        @Override public PageFormat getPageFormat(PageFormat pageFormat, int pageIndex) {
            PageFormat f = new PageFormat();
            f.setOrientation(PageFormat.LANDSCAPE);
            Paper paper = new Paper();
            paper.setSize(842, 595); // A3-ish, in points
            paper.setImageableArea(0, 0, 842, 595);
            f.setPaper(paper);
            return f;
        }
        @Override public void print(Graphics2D g, int pageIndex) {
            g.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
            g.drawString("Диаграмма процесса: создание отчёта", 50, 100);
        }
        @Override public String getJobName() { return "pdf-test"; }
        @Override public void pageSetup(com.ramussoft.gui.common.GUIFramework framework) { }
        @Override public void print(com.ramussoft.gui.common.GUIFramework framework) throws PrinterException { }
        @Override public PageFormat getPageFormat() { return new PageFormat(); }
        @Override public javax.print.PrintService getPrintService() { return null; }
        @Override public void setPageFormat(PageFormat pageFormat) { }
        @Override public void setPrintService(javax.print.PrintService printService) { }
        @Override public Action[] getActions(com.ramussoft.gui.common.GUIFramework framework) { return new Action[0]; }
    }

    private static boolean containsAsciiMarker(byte[] data, String marker) {
        byte[] m = marker.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        outer:
        for (int i = 0; i <= data.length - m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                if (data[i + j] != m[j]) continue outer;
            }
            return true;
        }
        return false;
    }

    private File extractFont() throws Exception {
        File out = tmp.newFile("AGHelvetica.TTF");
        try (InputStream in = PdfExportTest.class
                .getResourceAsStream("/print/to/pdf/AGHelvetica.TTF")) {
            Assert.assertNotNull("font resource /print/to/pdf/AGHelvetica.TTF is missing", in);
            Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return out;
    }

    @Test
    public void cyrillicTextIsEmbeddedAndExtractable() throws Exception {
        File font = extractFont();
        File pdf = tmp.newFile("cyrillic.pdf");

        Document document = new Document(PageSize.A4);
        FileOutputStream outputStream = new FileOutputStream(pdf);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        FontMapper mapper = new DefaultFontMapper() {
            @Override
            public BaseFont awtToPdf(Font f) {
                try {
                    return BaseFont.createFont(font.getAbsolutePath(),
                            BaseFont.IDENTITY_H, true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        PdfContentByte cb = writer.getDirectContent();
        // (cb, width, height, fontMapper, onlyShapes, convertImagesToJPEG, quality)
        Graphics2D g2d = new PdfGraphics2D(cb,
                (float) PageSize.A4.getWidth(),
                (float) PageSize.A4.getHeight(), mapper, false, false, 0f);
        g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        g2d.drawString("Пример кириллицы: "
                + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя 0123456789", 50, 100);
        g2d.dispose();
        document.close();
        outputStream.close();

        // The file must be a PDF with the TTF font embedded (OpenPDF writes
        // a subset of the used glyphs, so check for the FontFile marker
        // instead of a minimum size).
        byte[] bytes = Files.readAllBytes(pdf.toPath());
        Assert.assertEquals("%PDF-", new String(bytes, 0, 5, "US-ASCII"));
        Assert.assertTrue("the TTF font should be embedded (FontFile marker missing)",
                containsAsciiMarker(bytes, "FontFile"));

        // The page must exist and the text must be extractable (ToUnicode).
        PdfReader reader = new PdfReader(pdf.getAbsolutePath());
        try {
            Assert.assertEquals(1, reader.getNumberOfPages());
            String text = new PdfTextExtractor(reader).getTextFromPage(1);
            Assert.assertTrue("extracted text should contain cyrillic, was: <" + text + ">",
                    text.contains("кириллицы"));
        } finally {
            reader.close();
        }
    }
}
