package se.ekonomipuls.database.staging;

import se.ekonomipuls.proxy.BankDroidTransaction;

import java.util.List;

/**
 * @author Michael Svensson
 */
public interface StagingDbFacade {
    void bulkInsertBdTransactions(final List<BankDroidTransaction> transactions);
    List<BankDroidTransaction> getStagedTransactions();
    int purgeStagingTable();
}
