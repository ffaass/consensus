import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private double pGraph;
    private double pMalicious;
    private double pTxDistribution;
    private int numRounds;

    private Set<Transaction> pendingTransactions = new HashSet<>();
    private Map<Integer, Set<Transaction>> candidateHistory = new HashMap<>();
    private boolean[] followees;
    private boolean[] blacklist;



    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.pGraph = p_graph;
        this.pMalicious = p_malicious;
        this.pTxDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
        this.blacklist = new boolean[followees.length];
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions.addAll(pendingTransactions);
    }

    public Set<Transaction> sendToFollowers() {
        Set<Transaction> result = new HashSet<>(pendingTransactions);
        pendingTransactions.clear();
        return result;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        Map<Integer, Long> proposeCount =
                candidates.stream()
                    .collect(Collectors.groupingBy(c -> c.sender, Collectors.counting()));

        Long maxSize = proposeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getValue).orElse(0L);

        Set<Integer> blacklistSet = proposeCount.entrySet().stream().filter(e -> e.getValue() < maxSize * pGraph)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (int i : blacklistSet) {
            blacklist[i] = true;
        }


        for (Candidate c : candidates) {
            if (!blacklist[c.sender]) {
                pendingTransactions.add(c.tx);
            }
        }

        /*
        int blackCount = 0;
        int fCount = 0;
        System.out.print("followees ");
        for (int i = 0; i < followees.length; i++) {
            if (followees[i]) {
                System.out.print(i + " ");
                fCount++;
            }
        }
        System.out.print("blacks ");
        for (int i = 0; i < blacklist.length; i++) {
            if (blacklist[i]) {
                System.out.print(i + " ");
                blackCount++;
            }
        }

        System.out.println("" + fCount + " followees, " + blackCount + " malicious node");
        */
    }
}