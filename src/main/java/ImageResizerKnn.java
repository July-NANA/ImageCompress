import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageResizerKnn {
    public static void main(String[] args) {
        String inputImagePath = "origin.png";
        String outputImagePath = "output_image.png";
        int scale = 8;

        String formatName = "png"; // 输出图像格式

        try {
            // 读取原图像
            File inputFile = new File(inputImagePath);
            BufferedImage inputImage = ImageIO.read(inputFile);
            int newWidth = inputImage.getWidth() / scale;// 新图像宽度
            int newHeight = inputImage.getHeight() / scale; // 新图像高度
            // 缩小图像尺寸
            BufferedImage outputImage = new BufferedImage(newWidth, newHeight, inputImage.getType());
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    // 最近邻算法：寻找最近的像素点
                    int sourceX = Math.round((float) x * inputImage.getWidth() / newWidth);
                    int sourceY = Math.round((float) y * inputImage.getHeight() / newHeight);
                    Color color = new Color(inputImage.getRGB(sourceX, sourceY));
                    outputImage.setRGB(x, y, color.getRGB());
                }
            }

            // 写入缩小后的图像
            File outputFile = new File(outputImagePath);
            ImageIO.write(outputImage, formatName, outputFile);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
