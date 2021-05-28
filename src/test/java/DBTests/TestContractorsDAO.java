package DBTests;

import com.vik.dao.ContractorDAO;
import com.vik.dao.ContractorsDAOImpl;
import com.vik.models.Contractor;
import com.vik.models.Owner;
import com.vik.service.DBConnection;
import com.vik.service.InitType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestContractorsDAO {
    ContractorDAO contractorDAO = new ContractorsDAOImpl();

    @BeforeClass
    public static void dbInit() {
        if (!DBConnection.initialized)
            DBConnection.init(InitType.TEST);
    }

    @Test
    public void shouldFindContractors() {
        Owner owner = new Owner();
        owner.setId(1L);
        Contractor contractor = contractorDAO.getContractorsByOwner(owner);
        Assert.assertFalse(contractor.getContractor_id().isEmpty());
    }

    @Test
    public void shouldAddContractor() {
        Owner o1 = new Owner();
        Owner o2 = new Owner();
        o1.setId(2L);
        o2.setId(3L);

        Assert.assertTrue(contractorDAO.addContractor(o1, o2));
    }
}
