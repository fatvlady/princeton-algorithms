/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;

public class KdTree {

    private Node root = null;
    private int size = 0;

    // mutable fields
    private boolean horizontal = true;
    private final double[] currentRect = new double[]{0, 1, 0, 1};
    private RectHV queryRect = null;
    private Point2D queryPoint = null;
    private List<Point2D> rangeResult = null;
    private double nearestDistance = Double.POSITIVE_INFINITY;
    private Point2D nearestResult = null;

    private static class Node {
        private final Point2D p;    // the point
        private Node lb;            // the left/bottom subtree
        private Node rt;            // the right/top subtree
        public Node(Point2D p) {
            this.p = p;
        }
    }

    private double currentRectSquaredDistanceTo(Point2D p) {
        double dx = 0.0, dy = 0.0;
        if      (p.x() < currentRect[0]) dx = p.x() - currentRect[0];
        else if (p.x() > currentRect[1]) dx = p.x() - currentRect[1];
        if      (p.y() < currentRect[2]) dy = p.y() - currentRect[2];
        else if (p.y() > currentRect[3]) dy = p.y() - currentRect[3];
        return dx*dx + dy*dy;
    }

    // insert in tree with `current` root
    private void treeInsert(Node current, Point2D p)
    {
        horizontal = true;
        while (true)
        {
            if (horizontal) {
                if (p.x() < current.p.x()) {
                    if (current.lb == null) {
                        current.lb = new Node(p);
                        return;
                    }
                    current = current.lb;
                } else {
                    if (current.rt == null) {
                        current.rt = new Node(p);
                        return;
                    }
                    current = current.rt;
                }
            }
            else {
                if (p.y() < current.p.y()) {
                    if (current.lb == null) {
                        current.lb = new Node(p);
                        return;
                    }
                    current = current.lb;
                } else {
                    if (current.rt == null) {
                        current.rt = new Node(p);
                        return;
                    }
                    current = current.rt;
                }
            }
            horizontal = !horizontal;
        }
    }

    // checks whether point is within tree with `current` root
    private boolean treeContains(Node current, Point2D p)
    {
        horizontal = true;
        while (true)
        {
            if (Double.compare(p.x(), current.p.x()) == 0 &&
                    Double.compare(p.y(), current.p.y()) == 0) return true;
            if (horizontal) {
                if (p.x() < current.p.x()) {
                    if (current.lb == null) {
                        return false;
                    }
                    current = current.lb;
                } else {
                    if (current.rt == null) {
                        return false;
                    }
                    current = current.rt;
                }
            }
            else {
                if (p.y() < current.p.y()) {
                    if (current.lb == null) {
                        return false;
                    }
                    current = current.lb;
                } else {
                    if (current.rt == null) {
                        return false;
                    }
                    current = current.rt;
                }
            }
            horizontal = !horizontal;
        }
    }

    // draws tree with `current` root
    private void treeDraw(Node current)
    {
        if (current == null) return;
        current.p.draw();
        treeDraw(current.lb);
        treeDraw(current.rt);
    }

    // checks for intersections of tree with `current` root and `queryRect` rectangle
    // stores to `rangeResult` list
    private void treeRangeSearch(Node current) {
        if (current == null) return;
        if (queryRect.xmin() <= current.p.x() && current.p.x() <= queryRect.xmax() &&
                queryRect.ymin() <= current.p.y() && current.p.y() <= queryRect.ymax())
            rangeResult.add(current.p);
        if (horizontal) {
            horizontal = false;
            if (queryRect.xmin() < current.p.x()) {
                treeRangeSearch(current.lb);
            }
            if (current.p.x() <= queryRect.xmax()) {
                treeRangeSearch(current.rt);
            }
            horizontal = true;
        } else {
            horizontal = true;
            if (queryRect.ymin() < current.p.y()) {
                treeRangeSearch(current.lb);
            }
            if (current.p.y() <= queryRect.ymax()) {
                treeRangeSearch(current.rt);
            }
            horizontal = false;
        }
    }

    // finds nearest to the `queryPoint` point in tree with `current` root
    private void treeNearest(Node current) {
        if (current == null) return;
        double currentDistance = queryPoint.distanceSquaredTo(current.p);
        if (currentDistance < nearestDistance) {
            nearestDistance = currentDistance;
            nearestResult = current.p;
        }
        if (horizontal) {
            horizontal = false;
            if (queryPoint.x() < current.p.x()) {
                double tmp = currentRect[1];
                currentRect[1] = current.p.x();
                treeNearest(current.lb);
                currentRect[1] = tmp;
                tmp = currentRect[0];
                currentRect[0] = current.p.x();
                if (currentRectSquaredDistanceTo(queryPoint) < nearestDistance) {
                    treeNearest(current.rt);
                }
                currentRect[0] = tmp;
            } else {
                double tmp = currentRect[0];
                currentRect[0] = current.p.x();
                treeNearest(current.rt);
                currentRect[0] = tmp;
                tmp = currentRect[1];
                currentRect[1] = current.p.x();
                if (currentRectSquaredDistanceTo(queryPoint) < nearestDistance) {
                    treeNearest(current.lb);
                }
                currentRect[1] = tmp;
            }
            horizontal = true;
        } else {
            horizontal = true;
            if (queryPoint.y() < current.p.y()) {
                double tmp = currentRect[3];
                currentRect[3] = current.p.y();
                treeNearest(current.lb);
                currentRect[3] = tmp;
                tmp = currentRect[2];
                currentRect[2] = current.p.y();
                if (currentRectSquaredDistanceTo(queryPoint) < nearestDistance) {
                    treeNearest(current.rt);
                }
                currentRect[2] = tmp;
            } else {
                double tmp = currentRect[2];
                currentRect[2] = current.p.y();
                treeNearest(current.rt);
                currentRect[2] = tmp;
                tmp = currentRect[3];
                currentRect[3] = current.p.y();
                if (currentRectSquaredDistanceTo(queryPoint) < nearestDistance) {
                    treeNearest(current.lb);
                }
                currentRect[3] = tmp;
            }
            horizontal = false;
        }
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        if (contains(p)) return;
        ++size;
        if (root == null) root = new Node(p);
        else treeInsert(root, p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        if (root == null) return false;
        return treeContains(root, p);
    }

    // draw all points to standard draw
    public void draw() {
        treeDraw(root);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        rangeResult = new ArrayList<>();
        queryRect = rect;
        horizontal = true;
        treeRangeSearch(root);
        Iterable<Point2D> result = rangeResult;
        rangeResult = null;
        queryRect = null;
        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Null argument.");
        }
        if (root == null) return null;
        nearestResult = root.p;
        nearestDistance = p.distanceSquaredTo(nearestResult);
        queryPoint = p;
        horizontal = true;
        treeNearest(root);
        Point2D result = nearestResult;
        nearestResult = null;
        return result;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // empty
    }
}
