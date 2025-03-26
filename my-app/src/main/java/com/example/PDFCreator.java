package com.example;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class PDFCreator {
    public static void main(String[] args) {
        try {
            createCanvasLikePDF("canvas_output.pdf");
            System.out.println("PDF created successfully!");
        } catch (IOException e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createCanvasLikePDF(String outputPath) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Set background color FIRST (OVERWRITE mode)
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
                PDPageContentStream.AppendMode.OVERWRITE, true)) {
            contentStream.setNonStrokingColor(220, 220, 220); // Light Gray
            contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
            contentStream.fill();
        }

        // Now create a NEW content stream in APPEND mode for drawings & text
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
                PDPageContentStream.AppendMode.APPEND, true)) {
            addImage(document, contentStream, "example.png", 50, 700, 100, 100);
            addText(contentStream, "Hello PDFBox!", 50, 650, 24);
            addOpaqueStroke(contentStream, 50, 600, 200, 600, 10, new Color(255, 0, 0));
            drawPolygonWithHole(contentStream, 300, 500);
            drawRoundedLine(contentStream, 50, 450, 250, 450, 15, new Color(0, 0, 255));
            drawFigureEight(contentStream, 500, 500);
            drawFigureEight(contentStream, 490, 500);
        }

        document.save(outputPath);
        document.close();
    }

    private static void addImage(PDDocument document, PDPageContentStream contentStream,
            String imagePath, float x, float y, float width, float height) throws IOException {
        File file = new File(imagePath);
        if (file.exists()) {
            PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);
            contentStream.drawImage(image, x, y, width, height);
        } else {
            System.out.println("Image not found: " + imagePath);
        }
    }

    private static void addText(PDPageContentStream contentStream, String text,
            float x, float y, float fontSize) throws IOException {
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();

    }

    private static void addOpaqueStroke(PDPageContentStream contentStream, float x1, float y1, float x2, float y2,
            float thickness, Color color) throws IOException {
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setStrokingAlphaConstant(1.0f);
        contentStream.setGraphicsStateParameters(gs);

        contentStream.setLineWidth(thickness);
        contentStream.setStrokingColor(color);
        contentStream.moveTo(x1, y1);
        contentStream.lineTo(x2, y2);
        contentStream.stroke();
    }

    private static void drawPolygonWithHole(PDPageContentStream contentStream, float x, float y) throws IOException {
        contentStream.setNonStrokingColor(0, 255, 0); // Green color for the polygon

        // Outer rectangle (big shape)
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + 100, y);
        contentStream.lineTo(x + 100, y - 100);
        contentStream.lineTo(x, y - 100);
        contentStream.closePath(); // Close outer rectangle

        // Inner rectangle (hole)
        contentStream.moveTo(x + 30, y - 30);
        contentStream.lineTo(x + 70, y - 30);
        contentStream.lineTo(x + 70, y - 70);
        contentStream.lineTo(x + 30, y - 70);
        contentStream.closePath(); // Close inner rectangle

        // **Apply even-odd rule to create the hole**
        contentStream.fillEvenOdd();
    }

    private static void drawRoundedLine(PDPageContentStream contentStream, float x1, float y1,
            float x2, float y2, float thickness, Color color) throws IOException {
        contentStream.setLineWidth(thickness);
        contentStream.setStrokingColor(color);
        contentStream.setLineCapStyle(1);
        contentStream.moveTo(x1, y1);
        contentStream.lineTo(x2, y2);
        contentStream.stroke();
    }

    private static void drawFigureEight(PDPageContentStream contentStream, float x, float y) throws IOException {
        // Set transparency for stroke
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setStrokingAlphaConstant(0.5f); // 50% transparency
        contentStream.setGraphicsStateParameters(gs);

        contentStream.setStrokingColor(Color.BLUE);
        contentStream.setLineWidth(5);

        // Start path
        contentStream.moveTo(x, y);

        // First loop (top, clockwise)
        contentStream.curveTo(x + 50, y + 100, x - 50, y + 200, x, y + 300);

        // Second loop (bottom, counterclockwise)
        contentStream.curveTo(x + 50, y + 200, x - 50, y + 100, x, y);

        // Close path & stroke it
        contentStream.stroke();
    }

}
