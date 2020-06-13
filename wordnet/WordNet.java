/* *****************************************************************************
 *  Name: WordNet.java
 *  Date: 06/11/2020
 *  Description: WordNet implementation
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class WordNet {
    private final RedBlackBST<String, Bag<Integer>> wiMap;
    private final RedBlackBST<Integer, String> iwMap;
    private final SAP sapHelper;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        In synsetsFile = new In(synsets);
        wiMap = new RedBlackBST<>();
        iwMap = new RedBlackBST<>();
        int lineCounter = 0;
        for (String line : synsetsFile.readAllLines()) {
            String[] tokens = line.split(",");
            if (tokens.length < 2) {
                throw new IllegalArgumentException("Bad synsets file");
            }
            int synsetId = Integer.parseInt(tokens[0]);
            iwMap.put(synsetId, tokens[1]);
            String[] aliases = tokens[1].split(" ");
            for (String alias : aliases)
            {
                Bag<Integer> idList;
                if (wiMap.contains(alias)) {
                    idList = wiMap.get(alias);
                }
                else {
                    idList = new Bag<>();
                    wiMap.put(alias, idList);
                }
                idList.add(synsetId);
            }
            lineCounter++;
        }
        In hypernymsFile = new In(hypernyms);
        Digraph wnet = new Digraph(lineCounter);
        int notRootCount = 0;
        for (String line : hypernymsFile.readAllLines()) {
            String[] tokens = line.split(",");
            if (tokens.length < 2) {
                continue;
            }
            int synsetId = Integer.parseInt(tokens[0]);
            String[] parents = Arrays.stream(tokens).skip(1).toArray(String[]::new);
            ++notRootCount;
            for (String parent : parents) {
                wnet.addEdge(synsetId, Integer.parseInt(parent));
            }
        }
        if (notRootCount + 1 != wnet.V()) {
            throw new IllegalArgumentException("Multiple roots in hypernym graph");
        }
        DirectedCycle cycleChecker = new DirectedCycle(wnet);
        if (cycleChecker.hasCycle()) {
            throw new IllegalArgumentException("Hypernym graph is not a DAG.");
        }
        sapHelper = new SAP(wnet);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return wiMap.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return wiMap.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        Iterable<Integer> synsetsA = wiMap.get(nounA);
        Iterable<Integer> synsetsB = wiMap.get(nounB);
        return sapHelper.length(synsetsA, synsetsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        Iterable<Integer> synsetsA = wiMap.get(nounA);
        Iterable<Integer> synsetsB = wiMap.get(nounB);
        return iwMap.get(sapHelper.ancestor(synsetsA, synsetsB));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        StdOut.println(wn.isNoun("abracadabra1"));
        StdOut.println(wn.isNoun("binary_file"));
        StdOut.println(wn.sap("binary_file", "operating_system"));
    }
}
