package com.vik.models;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Owner {
    private long id = 0L;
    private String name = "";
    private List<Account> accounts = new ArrayList<>();

    public Owner() {
    }

    public long getId() {
        return id;
    }

    public void setId(@NotNull long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(@NotNull List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner)) return false;
        Owner owner = (Owner) o;
        return id == owner.id && Objects.equals(name, owner.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
