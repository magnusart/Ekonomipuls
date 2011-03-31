package se.ekonomipuls.database;

import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.model.Transaction;

import java.util.List;

/**
 * @author Michael Svensson
 */
public interface AnalyticsTransactionsDbFacade {
    void insertTransactionsAssignTags(final List<ApplyFilterTagAction> actions);
    List<Transaction> getUnverifiedTransactions();
}
