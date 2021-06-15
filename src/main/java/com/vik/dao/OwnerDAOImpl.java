package com.vik.dao;

import com.vik.common.Logger;
import com.vik.common.LoggerImpl;
import com.vik.models.Owner;
import com.vik.service.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of operations with an entity Owner via DB
 */

public class OwnerDAOImpl implements OwnerDAO {
    private Logger logger = new LoggerImpl();

    /**
     * Returns set of all owners
     *
     * @return set of all owners
     */
    @Override
    public List<Owner> getAllOwners() {
        List<Owner> ownerSet = new LinkedList<>();
        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM owners");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Owner owner = new Owner();
                owner.setId(resultSet.getLong("id"));
                owner.setName(resultSet.getString("name"));
                ownerSet.add(owner);
            }
        } catch (SQLException e) {
            logger.write("Error getting all owners");
        }
        return ownerSet;
    }

    /**
     * Persists an owner to DB,
     * if the owner is blank or null - returns false,
     * (owner!=null && !owner.getName().equals(""))
     *
     * @param owner to save to DB
     */
    @Override
    public boolean persistOwner(Owner owner) {
        if (owner == null || owner.getName().equals("")) {
            logger.write("Owner is empty");
            return false;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO owners (name) VALUES ( ? )");
            ps.setString(1, owner.getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.write("Error persist owner");
        }
        return true;
    }

    /**
     * Get an owner from DB by its name,
     * if the name is blank || null || name not found returns blank owner
     *
     * @param name to search for in DB
     * @return owner
     */
    public Owner getOwnerByName(String name) {

        Owner owner = new Owner();
        if (name == null || name.equals("")) {
            logger.write("Name should be valid");
            return owner;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM owners WHERE name = ? ORDER BY id DESC");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                logger.write("No owner with such name");
                return owner;
            }
            owner.setId(rs.getLong("id"));
            owner.setName(rs.getString("name"));

            if (rs.next())
                logger.write("Found multiple owners");

        } catch (SQLException e) {
            logger.write("Error get owner by name");
        }
        return owner;
    }

    /**
     * Get an owner from DB by its ID,
     * If input id is 0 || null || owner not found returns blank owner
     * (id!=null) && (id!=0)
     *
     * @param id to search for in DB
     * @return owner
     */
    @Override
    public Owner getOwnerById(Long id) {

        Owner owner = new Owner();

        if (id == null || id == 0) {
            logger.write("Owner Id should be valid");
            return owner;
        }

        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM owners WHERE id = ?");
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                logger.write("No owner with such id");
                return owner;
            }
            owner.setName(rs.getString("name"));
            owner.setId(rs.getLong("id"));

        } catch (SQLException e) {
            logger.write("Error get owner by Id");
        }
        return owner;
    }

    /**
     * Updates an owner's name by its id
     * (owner.name != null && owner.name != "" && owner.id != 0)
     *
     * @param owner to update
     */
    @Override
    public void updateOwnerName(Owner owner) {
        if (owner.getName() == null || owner.getName().equals("") || owner.getId() == 0) {
            logger.write("Wrong owner data");
            return;
        }
        try (DBConnection dbc = new DBConnection()) {
            Connection connection = dbc.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE owners SET name = ? WHERE id = ?");
            ps.setString(1, owner.getName());
            ps.setLong(2, owner.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.write("Error update owner");
        }
    }

    /**
     * Checks if an owner is in DB
     *
     * @param owner id
     * @return true if owner is in DB
     */
    @Override
    public boolean isOwnerInDB(Owner owner) {
        if (owner == null || owner.getId() == 0)
            return false;
        owner = getOwnerById(owner.getId());
        return owner.getId() != 0;
    }
}
