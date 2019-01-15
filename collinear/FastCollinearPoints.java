/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FastCollinearPoints {

    private final ArrayList<LineSegment> segments;

    public FastCollinearPoints(Point[] points)    // finds all line segments containing 4 points
    {
        if (points == null) {
            throw new IllegalArgumentException("Null points array");
        }
        if (Arrays.stream(points).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Some points of the array are null");
        }
        Point[] mutPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(mutPoints);
        for (int p = 1; p < mutPoints.length; ++p) {
            if (mutPoints[p - 1].compareTo(mutPoints[p]) == 0) {
                throw new IllegalArgumentException("Repeated points detected");
            }
        }
        this.segments = new ArrayList<>();
        findCollinearPoints(points, mutPoints);
    }

    private void processCollinearRange(int begin, int end, Point[] points)
    {
        if (end - begin < 2) return;
        Point minPoint = points[0];
        Point maxPoint = points[0];
        for (int i = begin; i <= end; ++i)
        {
            Point curPoint = points[i];
            if (curPoint.compareTo(minPoint) < 0)
                return;
            maxPoint = maxPoint.compareTo(curPoint) < 0 ? curPoint : maxPoint;
        }
        this.segments.add(new LineSegment(minPoint, maxPoint));
    }

    private void findCollinearPoints(Point[] points, Point[] mutPoints)
    {
        if (points.length < 4) return;
        for (int p = 0; p < points.length; ++p)
        {
            Point point = points[p];
            Arrays.sort(mutPoints, point.slopeOrder());
            double currentSlope = Double.NEGATIVE_INFINITY;
            int curBegin = 0;
            for (int i = 1; i < mutPoints.length; ++i)
            {
                double nextSlope = point.slopeTo(mutPoints[i]);
                if (Double.compare(currentSlope, nextSlope) != 0)
                {
                    processCollinearRange(curBegin, i - 1, mutPoints);
                    currentSlope = nextSlope;
                    curBegin = i;
                }
            }
            processCollinearRange(curBegin, points.length - 1, mutPoints);
        }
    }

    public int numberOfSegments()        // the number of line segments
    {
        return segments.size();
    }

    public LineSegment[] segments()                // the line segments
    {
        return segments.toArray(new LineSegment[segments.size()]);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
