/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;

public class PointSET {

    private final SET<Point2D> bst;

    // construct an empty set of points
    public PointSET() {
        bst = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return bst.isEmpty();
    }

    // number of points in the set
    public int size() {
        return bst.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        bst.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        return bst.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (Point2D p : bst) p.draw();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        List<Point2D> result = new ArrayList<>();
        for (Point2D p : bst) {
            if (rect.contains(p)) result.add(p);
        }
        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        Point2D result = null;
        double distance = Double.POSITIVE_INFINITY;
        for (Point2D q : bst) {
            double curDistance = p.distanceSquaredTo(q);
            if (curDistance < distance) {
                distance = curDistance;
                result = q;
            }
        }
        return result;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // empty
    }
}
