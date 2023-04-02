import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageCompression {

    public static BufferedImage bitCompress(BufferedImage originalImage, double k) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[] pixels = originalImage.getRGB(0, 0, width, height, null, 0, width);
        double threshold = 1.0 - k;

        int[] compressed = new int[((int) (width * k) + 1) * ((int) (height * k) + 1)];
        int compressedWidth = (int) (width * k) + 1;
        int compressedHeight = (int) (height * k) + 1;

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int red = (pixel >> 16) & 0xFF;
            int green = (pixel >> 8) & 0xFF;
            int blue = pixel & 0xFF;

            if (red + green + blue > 255 * 3 * threshold) {
                int row = i / width;
                int col = i % width;
                int compressedRow = (int) Math.ceil(row * k);
                int compressedCol = (int) Math.ceil(col * k);
                int compressedIndex = compressedRow * compressedWidth + compressedCol;
                compressed[compressedIndex] = (255 << 24) | (red << 16) | (green << 8) | blue;
            }
        }

        BufferedImage outputImage = new BufferedImage(compressedWidth, compressedHeight, BufferedImage.TYPE_INT_ARGB);
        outputImage.setRGB(0, 0, compressedWidth, compressedHeight, compressed, 0, compressedWidth);
        return outputImage;
    }

    public static void main(String[] args) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("origin.png"));
            int targetWidth = originalImage.getWidth()/4+1;
            int targetHeight = originalImage.getHeight()/4+1;
            double k = (double) targetWidth / originalImage.getWidth();
            BufferedImage compressedImage = bitCompress(originalImage, k);
            BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, compressedImage.getType());
            outputImage.getGraphics().drawImage(compressedImage, 0, 0, targetWidth, targetHeight, null);
            ImageIO.write(outputImage, "png", new File("output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
