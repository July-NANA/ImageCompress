import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class NLMDemo {
    public static void main(String[] args) throws Exception {
        // 读取原始彩色图像
        BufferedImage image = ImageIO.read(new File("origin.png"));

        // 转换为灰度图像
        int[][] gray = toGray(image);

        // 添加高斯噪声
        int[][] noisy = addGaussianNoise(gray, 20);

        // 使用非局部均值去噪算法进行图像降噪
        int[][] denoised = nlmeans(noisy, 7, 7, 20, 10);

        // 保存压缩后的图像
        BufferedImage output = toRGB(denoised);
        ImageIO.write(output, "png", new File("output.png"));
    }

    // 将RGB彩色图像转换为灰度图像
    private static int[][] toGray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] gray = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                gray[i][j] = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            }
        }
        return gray;
    }

    // 给灰度图像添加高斯噪声
    private static int[][] addGaussianNoise(int[][] gray, double sigma) {
        int width = gray.length;
        int height = gray[0].length;
        int[][] noisy = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double noise = Math.random() * sigma;
                int value = (int) (gray[i][j] + noise);
                value = Math.max(0, Math.min(value, 255));
                noisy[i][j] = value;
            }
        }
        return noisy;
    }

    // 将灰度图像转换为RGB彩色图像
    private static BufferedImage toRGB(int[][] gray) {
        int width = gray.length;
        int height = gray[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int grayValue = gray[i][j];
                int rgb = (grayValue << 16) | (grayValue << 8) | grayValue;
                image.setRGB(i, j, rgb);
            }
        }
        return image;
    }

    // 使用非局部均值去噪算法降噪
    private static int[][] nlmeans(int[][] image, int patchSize, int searchSize, double sigma, int h) {
        int width = image.length;
        int height = image[0].length;
        int[][] result = new int[width][height];
        double[][] weightSum = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double pixelSum = 0;
                double weightTotal = 0;
                for (int i = -searchSize; i <= searchSize; i++) {
                    for (int j = -searchSize; j <= searchSize; j++) {
                        if (x + i < 0 || x + i >= width || y + j < 0 || y + j >= height) {
                            continue;
                        }
                        double d = distance(image, x, y, x + i, y + j, patchSize, sigma);
                        double weight = Math.exp(-d / h);
                        pixelSum += weight * image[x + i][y + j];
                        weightTotal += weight;
                    }
                }
                result[x][y] = (int) (pixelSum / weightTotal);
                weightSum[x][y] = weightTotal;
            }
        }
        return result;
    }

    // 计算两个点之间的距离
    private static double distance(int[][] image, int x1, int y1, int x2, int y2, int patchSize, double sigma) {
        double sum = 0;
        double count = 0;
        for (int i = -patchSize / 2; i <= patchSize / 2; i++) {
            for (int j = -patchSize / 2; j <= patchSize / 2; j++) {
                int p1 = getPixel(image, x1 + i, y1 + j);
                int p2 = getPixel(image, x2 + i, y2 + j);
                double diff = p1 - p2;
                sum += diff * diff;
                count++;
            }
        }
        return sum / count / sigma / sigma;
    }

    // 获取像素值
    private static int getPixel(int[][] image, int x, int y) {
        int width = image.length;
        int height = image[0].length;
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0;
        } else {
            return image[x][y];
        }
    }
}
