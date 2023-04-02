import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class WaveletCompressionDemo {
    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("origin.png"));
        int width = image.getWidth();
        int height = image.getHeight();
        int scaler=4;
        // 读取像素点并保存在二维数组中
        Raster raster = image.getData();
        int[][][] pixels = new int[height][width][3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i][j][0] = raster.getSample(j, i, 0);
                pixels[i][j][1] = raster.getSample(j, i, 1);
                pixels[i][j][2] = raster.getSample(j, i, 2);
            }
        }

        int newWidth = width / scaler;
        int newHeight = height / scaler;

        int[][][] newPixels = new int[newHeight][newWidth][3];

        for (int c = 0; c < 3; c++) {
            // 处理每个通道的像素值
            double[][] coeffs = new double[height][width];
            for (int i = 0; i < height; i++) {
                double[] row = new double[width];
                for (int j = 0; j < width; j++) {
                    row[j] = pixels[i][j][c];
                }
                double[] rowCoeffs = DWT1D.forwardDaubechies4(row);
                for (int j = 0; j < width; j++) {
                    coeffs[i][j] = rowCoeffs[j];
                }
            }

            for (int j = 0; j < width; j++) {
                double[] col = new double[height];
                for (int i = 0; i < height; i++) {
                    col[i] = coeffs[i][j];
                }
                double[] colCoeffs = DWT1D.forwardDaubechies4(col);
                for (int i = 0; i < height; i++) {
                    coeffs[i][j] = colCoeffs[i];
                }
            }

            // 缩小图像
            for (int i = 0; i < newHeight; i++) {
                for (int j = 0; j < newWidth; j++) {
                    newPixels[i][j][c] = (int) coeffs[i * 2][j * 2];
                }
            }
        }


        // 将新图像保存为文件
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        WritableRaster newRaster = newImage.getRaster();
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newRaster.setSample(j, i, 0, newPixels[i][j][0]);
                newRaster.setSample(j, i, 1, newPixels[i][j][1]);
                newRaster.setSample(j, i, 2, newPixels[i][j][2]);
            }
        }
        ImageIO.write(newImage, "png", new File("output.png"));
    }
}
