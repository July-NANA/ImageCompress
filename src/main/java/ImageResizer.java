import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class ImageResizer {

    public static void main(String[] args) throws IOException {

        // 读入图像文件
        BufferedImage inputImage = ImageIO.read(new File("origin.png"));

        // 缩小图像尺寸
        int newWidth = inputImage.getWidth() / 8;
        int newHeight = inputImage.getHeight() / 8;

        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(inputImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // 输出缩小后的图像
        ImageIO.write(outputImage, "png", new File("output.png"));

    }
}
