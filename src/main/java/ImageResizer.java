import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageResizer {

    public static List<double[]> enlarge(double x, double max, double s) {
        List<double[]> v = new ArrayList<>();
        s = Math.round(s);
        if (s < 2) {
            v.add(new double[]{Math.round(x)});
            return v;
        }
        double[] f = new double[(int) s];
        double step = (s - 1) / 2.0;
        for (int i = 0; i < s; i++) {
            f[i] = Math.max(0, Math.min(Math.round(x + ((i - step) / step)), max));
        }
        v.add(f);
        return v;
    }

    public static double[][] bitCompress(double[][][] x, double k, double s) {
        int height = x.length;
        int width = x[0].length;
        int depth = x[0][0].length;
        int selCount = height * width;
        boolean[] sel = new boolean[selCount];
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x0 = 0; x0 < width; x0++) {
                    if (x[y][x0][z] < 1) {
                        sel[y * width + x0] = true;
                    }
                }
            }
        }

        int[] selIndexes = new int[selCount];
        int index = 0;
        for (int i = 0; i < selCount; i++) {
            if (sel[i]) {
                selIndexes[index++] = i;
            }
        }

        int newHeight = (int) Math.ceil(height * k) + 1;
        int newWidth = (int) Math.ceil(width * k) + 1;
        double[][][] result = new double[newHeight][newWidth][depth];

        List<double[]> col = new ArrayList<>();
        List<double[]> row = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            double y = (selIndexes[i] / width) * k;
            double x0 = (selIndexes[i] % width) * k;
            col.addAll(enlarge(y, newHeight - 1, s));
            row.addAll(enlarge(x0, newWidth - 1, s));
        }

        for (int z = 0; z < depth; z++) {
            for (int j = 0; j < col.size(); j++) {
                double[] column = col.get(j);
                double[] rows = row.get(j);
                for (int k0 = 0; k0 < rows.length; k0++) {
                    for (int p = 0; p < column.length; p++) {
                        int msel = (int) Math.round(column[p] * newWidth + rows[k0]);
                        if (msel >= 0 && msel < result.length * result[0].length) {
                            result[p][k0][z] = x[selIndexes[j] / width][selIndexes[j] % width][z];
                        }
                    }
                }
            }
        }

        double[][] result2D = new double[newHeight * newWidth][depth];
        for (int z = 0; z < depth; z++) {
            int index2D = 0;
            for (int y = 0; y < newHeight; y++) {
                for (int x0 = 0; x0 < newWidth; x0++) {
                    result2D[index2D++][z] = result[y][x0][z];
                }
            }
        }
        return result2D;
    }

    public static void main(String[] args) throws Exception {
        double k = 0.5; // 缩放系数
        double s = 200; // 扩展的大小

        // 读取原始图像
        BufferedImage image = ImageIO.read(new File("origin.png"));

        // 转换为三维数组
        double[][][] x = new double[image.getHeight()][image.getWidth()][3];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x0 = 0; x0 < image.getWidth(); x0++) {
                int rgb = image.getRGB(x0, y);
                x[y][x0][0] = (rgb >> 16) & 0xff;
                x[y][x0][1] = (rgb >> 8) & 0xff;
                x[y][x0][2] = (rgb) & 0xff;
            }
        }

        // 选择需要压缩的像素
        boolean[] sel = new boolean[image.getHeight() * image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x0 = 0; x0 < image.getWidth(); x0++) {
                if (x[y][x0][0] < 128 && x[y][x0][1] < 128 && x[y][x0][2] < 128) {
                    sel[y * image.getWidth() + x0] = true;
                }
            }
        }
        int[] selIndexes = new int[sel.length];
        int index = 0;
        for (int i = 0; i < sel.length; i++) {
            if (sel[i]) {
                selIndexes[index++] = i;
            }
        }
        selIndexes = Arrays.copyOf(selIndexes, index);

        // 计算压缩后的图像尺寸
        int newHeight = (int) Math.ceil(image.getHeight() * k) + 1;
        int newWidth = (int) Math.ceil(image.getWidth() * k) + 1;

        // 执行压缩
        double[][] result2D = ImageResizer.bitCompress(x, k, s);

        // 转换为图像
        BufferedImage output = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        for (int index2D = 0; index2D < result2D.length; index2D++) {
            int y = index2D / newWidth;
            int x0 = index2D % newWidth;
            int r = (int) Math.round(result2D[index2D][0]);
            int g = (int) Math.round(result2D[index2D][1]);
            int b = (int) Math.round(result2D[index2D][2]);
            int rgb = (r << 16) | (g << 8) | b;
            output.setRGB(x0, y, rgb);
        }

        // 保存图像
        ImageIO.write(output, "png", new File("output.png"));
    }
}
