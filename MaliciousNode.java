import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class MaliciousNode implements Node {

    private Set<Transaction> pendingTransactions = new HashSet<>();
    private Set<Transaction> emptySet = new HashSet<>();

    public MaliciousNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
    }

    public void setFollowees(boolean[] followees) {
        return;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions.addAll(pendingTransactions);
    }

    public Set<Transaction> sendToFollowers() {
        if (Math.random() < 0.5) {
            return pendingTransactions;
        } else {
            return emptySet;
        }
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        // System.out.println();
        return;
    }
}
