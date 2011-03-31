package se.ekonomipuls.mocks;

import se.ekonomipuls.actions.ApplyFilterTagAction;
import se.ekonomipuls.database.AnalyticsTransactionsDbFacade;
import se.ekonomipuls.model.Transaction;

import java.util.List;

/**
 * @author Michael Svensson
 */
public class FakeAnalyticsTransactionsDbFacade implements AnalyticsTransactionsDbFacade {
    @Override
    public void insertTransactionsAssignTags(final List<ApplyFilterTagAction> actions) {
        //do nothing
    }

    @Override
    public List<Transaction> getUnverifiedTransactions() {
        return null;  //Nothing here
    }
}
