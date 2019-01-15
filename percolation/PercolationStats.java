/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import java.lang.intrument.Instrumentation;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private double[] thresholds;

    public PercolationStats(int n, int trials)    // perform trials independent experiments on an n-by-n grid
    {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException();
        this.thresholds = new double[trials];
        for (int pos = 0; pos < trials; ++pos)
            runExperiment(pos, n);
    }

    private void runExperiment(int pos, int n)
    {
        Percolation perc = new Percolation(n);
        int count = 0;
        while (!perc.percolates())
        {
            int row = StdRandom.uniform(n) + 1;
            int col = StdRandom.uniform(n) + 1;
            if (!perc.isOpen(row, col))
            {
                perc.open(row, col);
                ++count;
            }
        }
        thresholds[pos] = count / (double) (n * n);
    }

    public double mean()                          // sample mean of percolation threshold
    {
        return StdStats.mean(this.thresholds);
    }

    public double stddev()                        // sample standard deviation of percolation threshold
    {
        return StdStats.stddev(this.thresholds);
    }

    public double confidenceLo()                  // low  endpoint of 95% confidence interval
    {
        return mean() - stddev() * CONFIDENCE_95 / Math.sqrt(this.thresholds.length);
    }

    public double confidenceHi()                  // high endpoint of 95% confidence interval
    {
        return mean() + stddev() * CONFIDENCE_95 / Math.sqrt(this.thresholds.length);
    }

    public static void main(String[] args) {
        Instrumentation.getObjectSize()
        // n-by-n percolation system (read from command-line, default = 10)
        int n = 20;
        int t = 1000;
        if (args.length >= 1) n = Integer.parseInt(args[0]);
        if (args.length >= 2) t = Integer.parseInt(args[1]);

        PercolationStats stats = new PercolationStats(n, t);
        // repeatedly open site specified my mouse click and draw resulting system
        StdOut.printf("mean                    = %.10f\n", stats.mean());
        StdOut.printf("stddev                  = %.10f\n", stats.stddev());
        StdOut.printf("95%% confidence interval = [%.10f, %.10f]\n", stats.confidenceLo(), stats.confidenceHi());
    }
}
