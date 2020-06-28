/* *****************************************************************************
 *  Name: BaseballElimination.java
 *  Date: 06/28/2020
 *  Description: Maxflow-based baseball elimination implementation.
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

import java.util.Collections;
import java.util.NoSuchElementException;

public class BaseballElimination {
    private final int numberOfTeams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] against;
    private final RedBlackBST<String, Integer> nameMap = new RedBlackBST<>();
    private final String[] teamNames;
    private int maxWins = 0;
    private String bestTeam = null;
    private EliminationHelper eliminationHelper;

    private class EliminationHelper {
        private static final double FLOATING_POINT_EPSILON = 1.0E-11;

        private int checkedTeam;
        private long targetFlow = 0;
        private FordFulkerson maxFlow;

        public EliminationHelper(int checkedTeam) {
            this.checkedTeam = checkedTeam;
            int vertexCount = 2 + numberOfTeams;
            for (int i = 0; i < numberOfTeams; ++i) {
                for (int j = i + 1; j < numberOfTeams; ++j) {
                    if (i != checkedTeam && j != checkedTeam && against[i][j] > 0) {
                        ++vertexCount;
                        targetFlow += against[i][j];
                    }
                }
            }
            FlowNetwork network = new FlowNetwork(vertexCount);
            int s = vertexCount - 2;
            int t = vertexCount - 1;
            int gameVertex = numberOfTeams;
            int maxCheckedWins = wins[checkedTeam] + remaining[checkedTeam];
            for (int i = 0; i < numberOfTeams; ++i) {
                if (i != checkedTeam) {
                    network.addEdge(new FlowEdge(i, t, maxCheckedWins - wins[i]));
                    for (int j = i + 1; j < numberOfTeams; ++j) {
                        if (j != checkedTeam && against[i][j] > 0) {
                            network.addEdge(new FlowEdge(s, gameVertex, against[i][j]));
                            network.addEdge(new FlowEdge(gameVertex, i, Double.POSITIVE_INFINITY));
                            network.addEdge(new FlowEdge(gameVertex, j, Double.POSITIVE_INFINITY));
                            ++gameVertex;
                        }
                    }
                }
            }
            maxFlow = new FordFulkerson(network, s, t);
        }

        boolean isEliminated() {
            return !(Math.abs(maxFlow.value() - targetFlow) < FLOATING_POINT_EPSILON);
        }

        Iterable<String> certificateOfElimination() {
            Queue<String> certificates = new Queue<>();
            for (int i = 0; i < numberOfTeams; ++i) {
                if (maxFlow.inCut(i)) {
                    certificates.enqueue(teamNames[i]);
                }
            }
            return certificates;
        }
    }

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        // try to read from file in working directory
        In in = new In(filename);
        try {
            numberOfTeams = in.readInt();
            wins = new int[numberOfTeams];
            losses = new int[numberOfTeams];
            remaining = new int[numberOfTeams];
            against = new int[numberOfTeams][numberOfTeams];
            teamNames = new String[numberOfTeams];
            for (int i = 0; i < numberOfTeams; ++i) {
                String name = in.readString();
                nameMap.put(name, i);
                teamNames[i] = name;
                wins[i] = in.readInt();
                if (wins[i] > maxWins) {
                    maxWins = wins[i];
                    bestTeam = name;
                }
                losses[i] = in.readInt();
                remaining[i] = in.readInt();
                for (int j = 0; j < numberOfTeams; ++j) {
                    against[i][j] = in.readInt();
                }
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Failed to parse game file", e);
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return nameMap.keys();
    }

    private void validate(String team) {
        if (!nameMap.contains(team)) {
            throw new IllegalArgumentException("Specified team not found");
        }
    }

    // number of wins for given team
    public int wins(String team) {
        validate(team);
        return wins[nameMap.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        validate(team);
        return losses[nameMap.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validate(team);
        return remaining[nameMap.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validate(team1);
        validate(team2);
        return against[nameMap.get(team1)][nameMap.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validate(team);
        int teamIndex = nameMap.get(team);
        if (wins[teamIndex] + remaining[teamIndex] < maxWins) {
            return true;
        }
        if (eliminationHelper == null || eliminationHelper.checkedTeam != teamIndex) {
            eliminationHelper = new EliminationHelper(teamIndex);
        }
        return eliminationHelper.isEliminated();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!nameMap.contains(team)) {
            throw new IllegalArgumentException("Specified team not found");
        }
        int teamIndex = nameMap.get(team);
        if (!isEliminated(team)) {
            return null;
        }
        if (wins[teamIndex] + remaining[teamIndex] < maxWins) {
            return Collections.singletonList(bestTeam);
        }
        if (eliminationHelper == null || eliminationHelper.checkedTeam != teamIndex) {
            eliminationHelper = new EliminationHelper(teamIndex);
        }
        return eliminationHelper.certificateOfElimination();
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
