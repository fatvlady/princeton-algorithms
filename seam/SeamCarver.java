/* *****************************************************************************
 *  Name: SeamCarver.java
 *  Date: 06/14/2020
 *  Description: SeamCarver implementation as part of Algorithms II course
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private static final double MAX_ENERGY = 1000;

    private int outerDim;
    private int innerDim;
    private int[][] pData;
    private double[][] eData;
    private boolean isOuterY;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Null picture passed as argument to SeamCarver");
        }
        outerDim = picture.width();
        innerDim = picture.height();
        if (outerDim == 0 || innerDim == 0) {
            throw new IllegalArgumentException("Flat image passed");
        }
        isOuterY = false;
        pData = new int[outerDim][innerDim];
        for (int i = 0; i < outerDim; ++i) {
            for (int j = 0; j < innerDim; ++j) {
                pData[i][j] = picture.getRGB(i, j);
            }
        }
        eData = new double[outerDim][innerDim];
        for (int i = 0; i < outerDim; ++i) {
            eData[i][0] = MAX_ENERGY;
            eData[i][innerDim - 1] = MAX_ENERGY;
        }
        for (int j = 0; j < innerDim; ++j) {
            eData[0][j] = MAX_ENERGY;
            eData[outerDim - 1][j] = MAX_ENERGY;
        }
        for (int i = 1; i + 1 < outerDim; ++i) {
            for (int j = 1; j + 1 < innerDim; ++j) {
                double dx2 = calculateDelta(picture.get(i - 1, j), picture.get(i + 1, j));
                double dy2 = calculateDelta(picture.get(i, j - 1), picture.get(i, j + 1));
                eData[i][j] = Math.sqrt(dx2 + dy2);
            }
        }
    }

    private int[][] getPrevArray() {
        return new int[outerDim][innerDim];
    }

    private double[][] getDistArray() {
        double[][] distToBuffer = new double[2][];
        distToBuffer[0] = new double[Math.max(innerDim, outerDim)];
        distToBuffer[1] = new double[Math.max(innerDim, outerDim)];
        return distToBuffer;
    }

    private double calculateDelta(Color c1, Color c2) {
        double dr = c1.getRed() - c2.getRed();
        double dg = c1.getGreen() - c2.getGreen();
        double db = c1.getBlue() - c2.getBlue();
        return dr * dr + dg * dg + db * db;
    }

    private double calculateDelta(int rgb1, int rgb2) {
        Color c1 = new Color(rgb1);
        Color c2 = new Color(rgb2);
        return calculateDelta(c1, c2);
    }

    // current picture
    public Picture picture() {
        Picture pic = new Picture(width(), height());
        if (isOuterY) {
            for (int i = 0; i < outerDim; ++i) {
                for (int j = 0; j < innerDim; ++j) {
                    pic.setRGB(j, i, pData[i][j]);
                }
            }
        } else {
            for (int i = 0; i < outerDim; ++i) {
                for (int j = 0; j < innerDim; ++j) {
                    pic.setRGB(i, j, pData[i][j]);
                }
            }
        }
        return pic;
    }

    private static void validateVerticalSeam(int[] seam, int width, int height) {
        if (seam == null || seam.length != height) {
            throw new IllegalArgumentException("null seam or of wrong size");
        }
        if (width <= 1) {
            throw new IllegalArgumentException("cannot remove last pixels");
        }
        int prev = seam[0];
        for (int i : seam) {
            if (i < 0 || i >= width) {
                throw new IllegalArgumentException("invalid seam sequence, index out of bounds");
            }
            if (i - prev > 1 || prev - i > 1) {
                throw new IllegalArgumentException("invalid seam sequence, pixels not connected");
            }
            prev = i;
        }
    }

    private double calculateEnergy(int outer, int inner) {
        if (inner <= 0 || inner + 1 >= innerDim || outer <= 0 || outer + 1 >= outerDim) {
            return MAX_ENERGY;
        }
        double dx2 = calculateDelta(pData[outer - 1][inner], pData[outer + 1][inner]);
        double dy2 = calculateDelta(pData[outer][inner - 1], pData[outer][inner + 1]);
        return Math.sqrt(dx2 + dy2);
    }

    private int[] findSeam() {
        int[] result = new int[outerDim];
        int[][] prev = getPrevArray();
        double[][] distTo = getDistArray();
        Arrays.fill(distTo[0], 0, innerDim, MAX_ENERGY);
        for (int i = 1; i < outerDim; ++i) {
            Arrays.fill(distTo[1], 0, innerDim, Double.POSITIVE_INFINITY);
            for (int j = 0; j < innerDim; ++j) {
                if (distTo[1][j] > distTo[0][j] + eData[i][j]) {
                    distTo[1][j] = distTo[0][j] + eData[i][j];
                    prev[i][j] = j;
                }
                if (j > 0 && distTo[1][j - 1] > distTo[0][j] + eData[i][j - 1]) {
                    distTo[1][j - 1] = distTo[0][j] + eData[i][j - 1];
                    prev[i][j - 1] = j;
                }
                if (j + 1 < innerDim && distTo[1][j + 1] > distTo[0][j] + eData[i][j + 1]) {
                    distTo[1][j + 1] = distTo[0][j] + eData[i][j + 1];
                    prev[i][j + 1] = j;
                }
            }
            double[] tmp = distTo[0];
            distTo[0] = distTo[1];
            distTo[1] = tmp;
        }
        double minDistTo = Double.POSITIVE_INFINITY;
        for (int j = 0; j < innerDim; ++j) {
            if (distTo[0][j] < minDistTo) {
                minDistTo = distTo[0][j];
                result[outerDim - 1] = j;
            }
        }
        for (int i = 1; i < outerDim; ++i) {
            result[outerDim - i - 1] = prev[outerDim - i][result[outerDim - i]];
        }
        return result;
    }

    private void removeSeam(int[] seam) {
        --innerDim;
        for (int i = 0; i < outerDim; ++i) {
            int s = seam[i];
            for (int j = s; j < innerDim; ++j) {
                pData[i][j] = pData[i][j + 1];
                eData[i][j] = eData[i][j + 1];
            }
        }
        for (int i = 1; i + 1 < outerDim; ++i) {
            int s = seam[i];
            if (s > 0) {
                eData[i][s - 1] = calculateEnergy(i, s - 1);
            }
            if (s < innerDim) {
                eData[i][s] = calculateEnergy(i, s);
            }
        }
    }

    private void transposeRepresentation() {
        int[][] pDataT = new int[innerDim][outerDim];
        double[][] eDataT = new double[innerDim][outerDim];
        for (int i = 0; i < outerDim; ++i) {
            for (int j = 0; j < innerDim; ++j) {
                pDataT[j][i] = pData[i][j];
                eDataT[j][i] = eData[i][j];
            }
        }
        pData = pDataT;
        eData = eDataT;
        int tmp = outerDim;
        outerDim = innerDim;
        innerDim = tmp;
        isOuterY = !isOuterY;
    }

    // width of current picture
    public int width() {
        return isOuterY ? innerDim : outerDim;
    }

    // height of current picture
    public int height() {
        return isOuterY ? outerDim : innerDim;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (isOuterY) {
            int tmp = x;
            x = y;
            y = tmp;
        }
        if (x < 0 || x >= outerDim) {
            throw new IllegalArgumentException("x coordinate out of range");
        }
        if (y < 0 || y >= innerDim) {
            throw new IllegalArgumentException("y coordinate out of range");
        }
        return eData[x][y];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (isOuterY) transposeRepresentation();
        return findSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (!isOuterY) transposeRepresentation();
        return findSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateVerticalSeam(seam, height(), width());
        if (isOuterY) transposeRepresentation();
        removeSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateVerticalSeam(seam, width(), height());
        if (!isOuterY) transposeRepresentation();
        removeSeam(seam);
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        int width = args.length > 1 ? Integer.parseInt(args[0]) : 100;
        int length = args.length > 1 ? Integer.parseInt(args[1]) : 200;
        Picture picture = SCUtility.randomPicture(width, length);
        SeamCarver sc = new SeamCarver(picture);
        Picture energyPicture = SCUtility.toEnergyPicture(sc);
        energyPicture.show();
        int[] hSeam = sc.findHorizontalSeam();
        int[] vSeam = sc.findVerticalSeam();
        Picture hSeamPicture = SCUtility.seamOverlay(energyPicture, true, hSeam);
        Picture vSeamPicture = SCUtility.seamOverlay(energyPicture, false, vSeam);
        hSeamPicture.show();
        vSeamPicture.show();
    }
}
