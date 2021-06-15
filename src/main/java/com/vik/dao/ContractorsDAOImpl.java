package com.vik.dao;

import com.vik.common.Logger;
import com.vik.common.LoggerImpl;
import com.vik.models.Contractor;
import com.vik.models.Owner;
import com.vik.service.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of operations with entity Contractor
 */
public class ContractorsDAOImpl implements ContractorDAO {
    private Logger logger = new LoggerImpl();
    private OwnerDAO ownerDAO = new OwnerDAOImpl();

    /**
     * Sets one owner as contractor to other owner
     *
     * @param owner      with id
     * @param contractor with id
     * @return true if add is successful
     */
    @Override
    public boolean addContractor(Owner owner, Owner contractor) {
        if (owner == null || contractor == null || owner.getId() == 0L ||
                contractor.getId() == 0L || owner.getId() == contractor.getId()) {
            logger.write("Fill contractors");
            return false;
        }
        if (ownerDAO.isOwnerInDB(owner) && ownerDAO.isOwnerInDB(contractor)) {
            try (DBConnection dbc = new DBConnection()) {
                Connection connection = dbc.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT CONTRACTOR_ID FROM CONTRACTORS WHERE OWNER_ID = ?");
                preparedStatement.setLong(1, owner.getId());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    long l = resultSet.getLong("contractor_id");
                    if (l == contractor.getId()) {
                        logger.write("Contractor already added");
                        return false;
                    }
                }
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO CONTRACTORS (OWNER_ID, CONTRACTOR_ID) VALUES ( ?, ? )");
                preparedStatement.setLong(1, owner.getId());
                preparedStatement.setLong(2, contractor.getId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                logger.write("Error adding contractors");
                return false;
            }
        } else {
            logger.write("Owner or his contractor not found");
            return false;
        }
        return true;
    }

    /**
     * Get contractor object, containing list of contractors of provided owner
     *
     * @param owner with id
     * @return contractor
     */
    @Override
    public Contractor getContractorsByOwner(Owner owner) {
        Contractor contractor = new Contractor();
        if (owner == null || owner.getId() == 0L) {
            logger.write("Fill owner to get contractors");
            return contractor;
        }

        if (ownerDAO.isOwnerInDB(owner)) {
            contractor.setOwner_id(owner.getId());
            try (DBConnection dbc = new DBConnection()) {
                Connection connection = dbc.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT o.id, name " +
                        "FROM contractors " +
                        "INNER JOIN owners o on o.id = CONTRACTOR_ID " +
                        "WHERE owner_id = ?");
                ps.setLong(1, owner.getId());
                ResultSet rs = ps.executeQuery();
                List<Owner> owners = new ArrayList<>();
                while (rs.next()) {
                    Owner out = new Owner();
                    out.setId(rs.getLong("OWNERS.id"));
                    out.setName(rs.getString("NAME"));
                    owners.add(out);
                }
                contractor.setContractor_id(owners);
                if (contractor.getContractor_id().isEmpty()) {
                    logger.write("No contractors found");
                }
            } catch (SQLException e) {
                logger.write("Error getting contractors");
            }
        } else {
            logger.write("No such owner in DB");
        }
        return contractor;
    }
}
