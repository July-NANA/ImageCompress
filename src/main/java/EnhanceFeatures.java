import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class EnhanceFeatures {
    public static void main(String[] args) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("origin.png"));
            // 缩小图像尺寸
            int targetWidth = originalImage.getWidth() / 8;
            int targetHeight = originalImage.getHeight() / 8;
            BufferedImage outputImage = enhanceFeatures(originalImage, targetWidth, targetHeight);
            ImageIO.write(outputImage, "png", new File("output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, type);
        Graphics g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resizedImage;
    }

    // 将图像转换为灰度图
    public static BufferedImage toGrayImage(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = outputImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        return outputImage;
    }

    // 将灰度图像进行二值化，得到黑白图像
    public static BufferedImage toBlackWhiteImage(BufferedImage originalImage, int threshold) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = outputImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = outputImage.getRGB(j, i);
                int gray = (pixel >> 16) & 0xFF;
                if (gray > threshold) {
                    outputImage.setRGB(j, i, Color.WHITE.getRGB());
                } else {
                    outputImage.setRGB(j, i, Color.BLACK.getRGB());
                }
            }
        }

        return outputImage;
    }

    // 将图像进行腐蚀式的图像处理，突出处于同一条直线上的一系列点
    public static BufferedImage erodeImage(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int[] masks = new int[] {0xffff0000, 0xff00ff00, 0xff0000ff};
        Random random = new Random();
        int mask = masks[random.nextInt(3)];

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = outputImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int pixel1 = outputImage.getRGB(j - 1, i);
                int pixel2 = outputImage.getRGB(j, i);
                int pixel3 = outputImage.getRGB(j + 1, i);
                if (pixel1 != pixel2 || pixel1 != pixel3) {
                    outputImage.setRGB(j, i, mask);
                }
            }
        }

        return outputImage;
    }

    public static BufferedImage enhanceFeatures(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = resizeImage(originalImage, targetWidth, targetHeight);
        BufferedImage grayImage = toGrayImage(resizedImage);
        BufferedImage blackWhiteImage = toBlackWhiteImage(grayImage, 128);
        BufferedImage erodedImage = erodeImage(blackWhiteImage);

        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D graphics = outputImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, null);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        graphics.drawImage(erodedImage, 0, 0, null);
        graphics.dispose();

        return outputImage;
    }

}
