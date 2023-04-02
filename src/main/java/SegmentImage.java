import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SegmentImage {
    public static void main(String[] args) throws IOException {
        // 读入图像文件
        BufferedImage inputImage = ImageIO.read(new File("origin.png"));
    }
    // 图像分割（阈值法）
    public static BufferedImage segmentImage(BufferedImage originalImage, int threshold) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = originalImage.getRGB(j, i);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                if (gray > threshold) {
                    outputImage.setRGB(j, i, Color.WHITE.getRGB());
                } else {
                    outputImage.setRGB(j, i, Color.BLACK.getRGB());
                }
            }
        }
        return outputImage;
    }

}
