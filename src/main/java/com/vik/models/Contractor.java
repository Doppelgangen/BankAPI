package com.vik.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Contractor {

    private long owner_id = 0L;
    private List<Owner> contractor_id = new ArrayList<>();

    public Contractor() {
    }

    public long getOwner_id() {
        return owner_id;
    }

    @JsonProperty("id")
    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public List<Owner> getContractor_id() {
        return contractor_id;
    }

    public void setContractor_id(List<Owner> contractor_id) {
        this.contractor_id = contractor_id;
    }
}
