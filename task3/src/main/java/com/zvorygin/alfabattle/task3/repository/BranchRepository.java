package com.zvorygin.alfabattle.task3.repository;

import com.zvorygin.alfabattle.task3.model.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;

@Repository
public class BranchRepository {

    private static final String BRANCH_QUERY = "SELECT id, title, lat, lon, address from branches where id = ?";

    private static final String CLOSEST_BRANCH_QUERY = "SELECT id, title, lat, lon, address from branches";

    private final BeanPropertyRowMapper<Branch> rowMapper = new BeanPropertyRowMapper<>(Branch.class, false);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BranchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Branch get(long id) {
        return jdbcTemplate.query(BRANCH_QUERY, this::fetchBranch, id);
    }

    public Branch getClosestBranch(double lat, double lon) {
        return jdbcTemplate.query(CLOSEST_BRANCH_QUERY, resultSet -> {
            return fetchClosestBranch(resultSet, lat, lon);
        });
    }

    private Branch fetchClosestBranch(ResultSet resultSet, double lat, double lon) throws SQLException {
        double bestDistance = Double.POSITIVE_INFINITY;
        Branch bestBranch = null;
        while (resultSet.next()) {
            Branch newBranch = Objects.requireNonNull(rowMapper.mapRow(resultSet, 1));
            double newDistance = newBranch.getDistanceTo(lat, lon);
            if (newDistance < bestDistance) {
                bestBranch = newBranch;
                bestBranch.setDistance(Math.round(newDistance));
            }
        }
        return bestBranch;
    }

    private Branch fetchBranch(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            throw new NoSuchElementException("branch not found");
        }

        return rowMapper.mapRow(resultSet, 1);
    }

}
