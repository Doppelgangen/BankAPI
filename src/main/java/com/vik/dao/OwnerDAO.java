package com.vik.dao;

import com.vik.models.Owner;

import java.util.List;

public interface OwnerDAO {
    List<Owner> getAllOwners();
    boolean persistOwner(Owner owner);
    Owner getOwnerByName(String name);
    Owner getOwnerById(Long id);
    void updateOwnerName(Owner owner);
    boolean isOwnerInDB(Owner owner);
}
