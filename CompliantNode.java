import java.util.*;


/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private double pGraph;
    private double pMalicious;
    private double pTxDistribution;
    private int numRounds;

    private Set<Transaction> pendingTransactions = new HashSet<>();
    private Set<Transaction> acceptedTransactions = new HashSet<>();
    private boolean[] followees;
    private boolean[] trustedNode;

    private double threshold;


    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.pGraph = p_graph;
        this.pMalicious = p_malicious;
        this.pTxDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
        this.trustedNode = new boolean[followees.length];
        this.threshold = followees.length * this.pGraph * (1 - pMalicious) * pTxDistribution * numRounds;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions.addAll(pendingTransactions);
        this.acceptedTransactions.addAll(pendingTransactions);
    }

    public Set<Transaction> sendToFollowers() {
        return acceptedTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        Map<Transaction, Set<Integer>> nodesProposeTx = new HashMap<>();

        for (Candidate c : candidates) {
            if (pendingTransactions.contains(c.tx)) {
                trustedNode[c.sender] = true;
            }

            Set<Integer> nodes = nodesProposeTx.getOrDefault(c.tx, new HashSet<>());
            nodes.add(c.sender);
            nodesProposeTx.put(c.tx, nodes);
        }

        Transaction maxTx = null;
        int count = 0;

        for (Transaction tx : nodesProposeTx.keySet()) {
            if (count < nodesProposeTx.get(tx).size()) {
                maxTx = tx;
                count = nodesProposeTx.get(tx).size();
            }
        }

        for (Candidate c : candidates) {
            if (c.tx.equals(maxTx)) {
                trustedNode[c.sender] = true;
            }
        }

        Set<Transaction> txOverThreshold = new HashSet<>();
        for (Transaction tx : nodesProposeTx.keySet()) {
            if (nodesProposeTx.get(tx).size() >= threshold) {
                txOverThreshold.add(tx);
            }
        }
        for (Candidate c : candidates) {
            if (txOverThreshold.contains(c.tx)) {
                trustedNode[c.sender] = true;
            }
        }

        for (Candidate c : candidates) {
            if (trustedNode[c.sender]) {
                acceptedTransactions.add(c.tx);
            }
        }
    }
}