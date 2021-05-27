package com.vik.dao;

import com.vik.models.Contractor;
import com.vik.models.Owner;

public interface ContractorDAO {
    boolean addContractor(Owner owner, Owner contractor);
    Contractor getContractorsByOwner(Owner owner);
}
