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
                    // 最近不是白色像素算法：寻找最近的不是白色像素的像素点
                    int sourceX = (int) Math.round((double) x * inputImage.getWidth() / newWidth);
                    int sourceY = (int) Math.round((double) y * inputImage.getHeight() / newHeight);
                    Color nearestColor = findNearestNonWhitePixel(inputImage, sourceX, sourceY);
                    outputImage.setRGB(x, y, nearestColor.getRGB());
                }
            }

            // 写入缩小后的图像
            File outputFile = new File(outputImagePath);
            ImageIO.write(outputImage, formatName, outputFile);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * 寻找最近的非白色像素
     *
     * @param image 原图像
     * @param x     像素点的x坐标
     * @param y     像素点的y坐标
     * @return 最近的非白色像素的颜色值
     */
    private static Color findNearestNonWhitePixel(BufferedImage image, int x, int y) {
        Color pixelColor;
        int width = image.getWidth();
        int height = image.getHeight();
        int maxOffset = Math.min(width, height)/200;
        for (int i = 1; i <= maxOffset; i++) {
            // 搜索周围的像素点
            for (int offsetX = -i; offsetX <= i; offsetX++) {
                for (int offsetY = -i; offsetY <= i; offsetY++) {
                    int checkX = x + offsetX;
                    int checkY = y + offsetY;
                    // 边缘像素点不能超出原图像尺寸
                    if (checkX < 0 || checkY < 0 || checkX >= width || checkY >= height)
                        continue;
                    pixelColor = new Color(image.getRGB(checkX, checkY), true);
                    if (pixelColor.getAlpha() == 0)  // 跳过透明像素
                        continue;
                    if (pixelColor.getRed() <= 230 || pixelColor.getGreen() <= 230 || pixelColor.getBlue() <= 230)
                        return pixelColor;  // 返回最近的非白色像素
                }
            }
        }
        return Color.WHITE;  // 如果没有找到非白色像素，返回白色作为默认值
    }
}
