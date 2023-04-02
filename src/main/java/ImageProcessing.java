import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageProcessing {
    public static void main(String[] args) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("origin.png"));
            int targetWidth = originalImage.getWidth()/2;
            int targetHeight = originalImage.getHeight()/2;
            BufferedImage outputImage = bitCompress(originalImage, targetWidth, targetHeight);
            ImageIO.write(outputImage, "png", new File("output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage bitCompress(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double k = Math.min((double)originalWidth / targetWidth, (double)originalHeight / targetHeight);
        int newWidth = (int)Math.round(originalWidth / k);
        int newHeight = (int)Math.round(originalHeight / k);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        resizedImage.getGraphics().drawImage(originalImage, 0, 0, newWidth, newHeight, null);

        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());

        int[] r = new int[targetWidth * targetHeight];
        int[] g = new int[targetWidth * targetHeight];
        int[] b = new int[targetWidth * targetHeight];

        int[] pixels = new int[1];
        int pixelCount = 0;

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int x = (int)Math.floor(j * k);
                int y = (int)Math.floor(i * k);

                if (x < 0 || x >= originalWidth || y < 0 || y >= originalHeight) {
                    continue;
                }

                // 修改后
                if (x < 0 || x >= originalWidth || y < 0 || y >= originalHeight) {
                    continue;
                }
                resizedImage.getRaster().getPixel(x, y, pixels);

                r[pixelCount] = pixels[0];
                g[pixelCount] = pixels[1];
                b[pixelCount] = pixels[2];

                pixelCount++;
            }
        }

        double s = Math.min(1, Math.sqrt((double)pixelCount / (targetWidth * targetHeight)));

        int[][] row = enlarge(getCol(pixelCount, s), targetWidth - 1, s);
        int[][] col = enlarge(getRow(pixelCount, s), targetHeight - 1, s);

        for (int i = 0; i < col.length; i++) {
            for (int j = 0; j < row.length; j++) {
                int index = col[i][j] * targetWidth + row[i][j];
                if (index < r.length) {
                    outputImage.setRGB(row[i][j], i, new Color(r[index], g[index], b[index]).getRGB());
                }
            }
        }

        return outputImage;
    }

    private static int[] getRow(int pixelCount, double s) {
        int[] row = new int[pixelCount];
        for (int i = 0; i < pixelCount; i++) {
            row[i] = i % (int)(Math.ceil(1.0 / s));
        }
        return row;
    }

    private static int[] getCol(int pixelCount, double s) {
        int[] col = new int[pixelCount];
        for (int i = 0; i < pixelCount; i++) {
            col[i] = i / (int)(Math.ceil(1.0 / s));
        }
        return col;
    }

    private static int[][] enlarge(int[] x, int max, double s) {
        s = Math.round(s);

        if (s < 2) {
            return new int[][] {x};
        }

        int[][] v = new int[(int)s][];
        double[] f = new double[(int)s];
        for (int i = 0; i < f.length; i++) {
            f[i] = ((double)i - (s - 1) / 2) / (s - 1);
        }

        for (int i = 0; i < f.length; i++) {
            v[i] = new int[x.length];
            for (int j = 0; j < x.length; j++) {
                v[i][j] = (int)Math.max(0, Math.min(Math.round(x[j] + f[i]), max));
            }
        }

        return v;
    }
}
