package se.ekonomipuls.database;

import se.ekonomipuls.proxy.bankdroid.BankDroidTransaction;

import java.util.List;

/**
 * @author Michael Svensson
 */
public interface StagingDbFacade {
    void bulkInsertBdTransactions(final List<BankDroidTransaction> transactions);
    List<BankDroidTransaction> getStagedTransactions();
    int purgeStagingTable();
}
