package se.ekonomipuls.mocks;

import se.ekonomipuls.database.staging.StagingDbFacade;
import se.ekonomipuls.proxy.BankDroidTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Svensson
 */
public class FakeStagingDbFacadeImpl implements StagingDbFacade {
    @Override
    public void bulkInsertBdTransactions(List<BankDroidTransaction> transactions) {
        //nothing
    }

    @Override
    public List<BankDroidTransaction> getStagedTransactions() {
        return new ArrayList<BankDroidTransaction>();
    }

    @Override
    public int purgeStagingTable() {
        return 0;
    }
}
