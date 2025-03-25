package com.example;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class Figure8PDFCreator {

    public static void main(String[] args) {
        try {
            createFigure8PDF("figure8.pdf");
            System.out.println("PDF created successfully!");
        } catch (IOException e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createFigure8PDF(String outputPath) throws IOException {
        // Create a new document
        PDDocument document = new PDDocument();

        // Add a blank page (A4 size)
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Constants for figure 8
        float centerX = page.getMediaBox().getWidth() / 2;
        float centerY = page.getMediaBox().getHeight() / 2;
        float width = 200; // Width of the figure 8
        float height = 400; // Height of the figure 8
        float lineThickness = 4;

        // Create a content stream for drawing
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set line width (thickness)
        contentStream.setLineWidth(lineThickness);

        // Set transparency (0.7 opacity)
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setNonStrokingAlphaConstant(0.7f);
        graphicsState.setStrokingAlphaConstant(0.7f);
        graphicsState.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(graphicsState);

        // Set color - we'll use blue
        contentStream.setStrokingColor(0, 0, 255); // RGB Blue

        // Draw the figure 8 using parametric equations
        drawSmoothFigure8(contentStream, centerX, centerY, width, height);

        // Close the content stream
        contentStream.close();

        // Save the document
        document.save(outputPath);
        document.close();
    }

    /**
     * Draw a smooth figure 8 using Bezier curves
     * Uses the mathematical lemniscate of Bernoulli equation
     */
    private static void drawSmoothFigure8(PDPageContentStream contentStream,
            float centerX, float centerY,
            float width, float height) throws IOException {
        // Number of segments to approximate the curve
        int segments = 100;

        // Calculate the first point
        float t = 0;
        float x = calculateX(t, width, centerX);
        float y = calculateY(t, height, centerY);

        // Move to the starting point
        contentStream.moveTo(x, y);

        // Draw the curve using multiple segments
        for (int i = 1; i <= segments; i++) {
            t = (float) i / segments;
            x = calculateX(t, width, centerX);
            y = calculateY(t, height, centerY);

            // For the first half of the curve
            if (i <= segments / 2) {
                contentStream.lineTo(x, y);
            }
            // For the second half of the curve
            else {
                contentStream.lineTo(x, y);
            }
        }

        // Stroke the path
        contentStream.stroke();
    }

    /**
     * Calculate x coordinate for figure 8 (lemniscate of Bernoulli)
     * 
     * @param t       Parameter from 0 to 1
     * @param width   Width of the figure 8
     * @param centerX Horizontal center of the figure 8
     * @return x coordinate
     */
    private static float calculateX(float t, float width, float centerX) {
        // Lemniscate equation for x
        float a = width / 2;
        return centerX + (float) (a * Math.sqrt(2) * Math.cos(t * 2 * Math.PI) /
                (Math.sin(t * 2 * Math.PI) * Math.sin(t * 2 * Math.PI) + 1));
    }

    /**
     * Calculate y coordinate for figure 8 (lemniscate of Bernoulli)
     * 
     * @param t       Parameter from 0 to 1
     * @param height  Height of the figure 8
     * @param centerY Vertical center of the figure 8
     * @return y coordinate
     */
    private static float calculateY(float t, float height, float centerY) {
        // Lemniscate equation for y
        float a = height / 2;
        return centerY + (float) (a * Math.sqrt(2) * Math.cos(t * 2 * Math.PI) *
                Math.sin(t * 2 * Math.PI) /
                (Math.sin(t * 2 * Math.PI) * Math.sin(t * 2 * Math.PI) + 1));
    }
}