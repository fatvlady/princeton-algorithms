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

public class BruteCollinearPoints {

    private final ArrayList<LineSegment> segments;

    public BruteCollinearPoints(Point[] points)    // finds all line segments containing 4 points
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
        findCollinearPoints(mutPoints);
    }

    private boolean extDCompare(double left, double right)
    {
        if (left == Double.NEGATIVE_INFINITY || right == Double.NEGATIVE_INFINITY)
            return true;
        return Double.compare(left, right) == 0;
    }

    private void findCollinearPoints(Point[] points)
    {
        if (points.length < 4) return;
        for (int p = 0; p < points.length - 3; ++p)
        {
            Point pp = points[p];
            for (int q = p + 1; q < points.length - 2; ++q)
            {
                Point pq = points[q];
                for (int r = q + 1; r < points.length - 1; ++r)
                {
                    Point pr = points[r];
                    for (int s = r + 1; s < points.length; ++s)
                    {
                        Point ps = points[s];
                        double pqSlope = pp.slopeTo(pq);
                        double prSlope = pp.slopeTo(pr);
                        double psSlope = pp.slopeTo(ps);
                        // think of more efficient way of comparison
                        if (extDCompare(pqSlope, prSlope) && extDCompare(pqSlope, psSlope) &&
                                extDCompare(prSlope, psSlope))
                        {
                            Point min;
                            Point max;
                            if (pp.compareTo(pq) < 0)
                            {
                                min = pp;
                                max = pq;
                            }
                            else
                            {
                                min = pq;
                                max = pp;
                            }
                            if (pr.compareTo(ps) < 0)
                            {
                                min = pr.compareTo(min) < 0 ? pr : min;
                                max = ps.compareTo(max) < 0 ? max : ps;
                            }
                            else
                            {
                                max = pr.compareTo(max) < 0 ? pr : min;
                                min = ps.compareTo(min) < 0 ? max : ps;
                            }
                            this.segments.add(new LineSegment(min, max));
                        }
                    }
                }
            }
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
