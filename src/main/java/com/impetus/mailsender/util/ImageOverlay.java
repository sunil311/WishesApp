package com.impetus.mailsender.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.impetus.mailsender.beans.Employee;

public class ImageOverlay {

    public static int createOverlay(String BG, String FG, String ovImg, Employee employee) {
        BufferedImage bgImage = readImage(BG);
        BufferedImage fgImage = readImage(FG);
        BufferedImage overlayedImage = overlayImages(bgImage, fgImage, employee);
        if (overlayedImage != null) {
            // writeImage(overlayedImage, BASE_PATH + "resources/" + ovImg, "JPG");
            writeImage(overlayedImage, FG.substring(0, FG.lastIndexOf("/")) + ovImg, "JPG");
            System.out.println("Overlay Completed...");
            return 0;
        } else {
            System.out.println("Problem With Overlay...");
            return 1;
        }
    }

    public static BufferedImage overlayImages(BufferedImage bgImage, BufferedImage fgImage, Employee employee) {
        if (fgImage.getHeight() > bgImage.getHeight() || fgImage.getWidth() > fgImage.getWidth()) {
            JOptionPane.showMessageDialog(null, "Foreground Image Is Bigger In One or Both Dimensions" + "nCannot proceed with overlay."
                    + "nn Please use smaller Image for foreground");
            return null;
        }

        Graphics2D g = bgImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(bgImage, 0, 0, null);
        Font font = new Font("Serif", Font.ITALIC, 25);
        g.setFont(font);
        g.setColor(Color.BLACK);
        // g.drawImage(fgImage, 50, 140, 230, 230, null);
        g.drawString("Dear " + employee.getNAME().substring(0, employee.getNAME().lastIndexOf(" ")), 75, 45);
        g.drawImage(fgImage, 70, 70, 230, 240, null);
        g.dispose();
        return bgImage;
    }

    public static BufferedImage readImage(String fileLocation) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    public static void writeImage(BufferedImage img, String fileLocation, String extension) {
        try {
            BufferedImage bi = img;
            File outputfile = new File(fileLocation);
            ImageIO.write(bi, extension, outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}