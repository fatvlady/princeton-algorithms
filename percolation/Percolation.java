/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Percolation {
    private final int   n;
    private int[]       id;
    private int[]       sz;
    private int[]       id2;
    private int[]       sz2;
    private boolean[]   open;
    private int         openCount = 0;
    private final int   totalCount;

    public Percolation(int n)
    {
        if (n <= 0)
            throw new IllegalArgumentException();
        this.n = n;
        this.totalCount = n * n;
        this.id = new int[totalCount + 2];
        this.id2 = new int[totalCount + 2];
        this.sz = new int[totalCount + 2];
        this.sz2 = new int[totalCount + 2];
        this.open = new boolean[totalCount + 2];
        for (int i = 0; i < totalCount + 2; ++i)
        {
            this.open[i] = false;
            this.id[i] = i;
            this.id2[i] = i;
            this.sz[i] = 1;
            this.sz2[i] = 1;
        }
        this.sz[totalCount] = n + 1;
        this.sz[totalCount + 1] = n + 1;
        this.sz2[totalCount] = n + 1;
        this.sz2[totalCount + 1] = n + 1;
        this.open[totalCount] = true;
        this.open[totalCount + 1] = true;
    }

    public    void open(int row, int col)    // open site (row, col) if it is not open already
    {
        if (isOpen(row, col))
            return;
        int thisIndex = index(row, col);
        if (col > 1 && isOpen(row, col - 1))
        {
            join(index(row, col - 1), thisIndex);
            join2(index(row, col - 1), thisIndex);
        }
        if ((col + 1) <= this.n && isOpen(row, col + 1))
        {
            join(index(row, col + 1), thisIndex);
            join2(index(row, col + 1), thisIndex);
        }
        if (row == 1 || row > 1 && isOpen(row - 1, col))
        {
            join(index(row - 1, col), thisIndex);
            join2(index(row - 1, col), thisIndex);
        }
        if ((row + 1) <= this.n && isOpen(row + 1, col))
        {
            join(index(row + 1, col), thisIndex);
            join2(index(row + 1, col), thisIndex);
        }
        if (row == this.n)
        {
            join(index(row + 1, col), thisIndex);
        }
        this.open[thisIndex] = true;
        this.openCount++;
    }
    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        if (row < 1 || col < 1 || row > this.n || col > this.n)
            throw new IllegalArgumentException();
        return this.open[index(row, col)];
    }
    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        if (row < 1 || col < 1 || row > this.n || col > this.n)
            throw new IllegalArgumentException();
        return connected2(index(row, col), this.totalCount);
    }
    public     int numberOfOpenSites()       // number of open sites
    {
        return this.openCount;
    }
    public boolean percolates()              // does the system percolate?
    {
        return connected(this.totalCount, this.totalCount + 1);
    }

    private int index(int row, int col)
    {
        if (row == 0)
            return this.totalCount;
        if (row == this.n + 1)
            return this.totalCount + 1;
        return (row - 1) * this.n + col - 1;
    }

    private int root(int i)
    {
        while (i != id[i])
        {
            id[i] = id[id[i]];
            i = id[i];
        }
        return i;
    }

    private int root2(int i)
    {
        while (i != id2[i])
        {
            id2[i] = id2[id2[i]];
            i = id2[i];
        }
        return i;
    }

    private boolean connected(int p, int q)
    {

        return root(p) == root(q);
    }

    private boolean connected2(int p, int q)
    {

        return root2(p) == root2(q);
    }

    private void join(int p, int q)
    {
        int i = root(p);
        int j = root(q);
        if (i == j) return;
        if (sz[i] < sz[j])
        {
            id[i] = j;
            sz[j] += sz[i];
        }
        else
        {
            id[j] = i;
            sz[i] += sz[j];
        }
    }


    private void join2(int p, int q)
    {
        int i = root2(p);
        int j = root2(q);
        if (i == j) return;
        if (sz2[i] < sz2[j])
        {
            id2[i] = j;
            sz2[j] += sz2[i];
        }
        else
        {
            id2[j] = i;
            sz2[i] += sz2[j];
        }
    }
}
