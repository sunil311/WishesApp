package com.impetus.mailsender.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ImageOverlay {

    public static int createOverlay(String BG, String FG, String ovImg) {
        BufferedImage bgImage = readImage(BG);
        BufferedImage fgImage = readImage(FG);
        BufferedImage overlayedImage = overlayImages(bgImage, fgImage);
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

    public static BufferedImage overlayImages(BufferedImage bgImage, BufferedImage fgImage) {
        if (fgImage.getHeight() > bgImage.getHeight() || fgImage.getWidth() > fgImage.getWidth()) {
            JOptionPane.showMessageDialog(null, "Foreground Image Is Bigger In One or Both Dimensions" + "nCannot proceed with overlay."
                    + "nn Please use smaller Image for foreground");
            return null;
        }

        Graphics2D g = bgImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(bgImage, 0, 0, null);
        g.drawImage(fgImage, 50, 140, 230, 230, null);
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