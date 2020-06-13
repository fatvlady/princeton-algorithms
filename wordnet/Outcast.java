/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null || nouns.length == 0) {
            throw new IllegalArgumentException("Null or empty input array");
        }
        if (nouns.length == 1) {
            return nouns[0];
        }
        long[] distance = new long[nouns.length];
        for (int i = 0; i < nouns.length; ++i) {
            for (int j = i + 1; j < nouns.length; ++j) {
                int d = wnet.distance(nouns[i], nouns[j]);
                distance[i] += d;
                distance[j] += d;
            }
        }
        int outcast = -1;
        long maxDistance = 0;
        for (int i = 0; i < nouns.length; ++i) {
            if (maxDistance < distance[i]) {
                outcast = i;
                maxDistance = distance[i];
            }
        }
        return nouns[outcast];
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
