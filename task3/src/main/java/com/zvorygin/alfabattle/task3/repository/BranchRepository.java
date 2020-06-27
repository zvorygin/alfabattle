package com.zvorygin.alfabattle.task3.repository;

import com.zvorygin.alfabattle.task3.model.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Repository
public class BranchRepository {

    private static final String BRANCH_QUERY = "SELECT id, title, lat, lon, address from branches where id = ?";

    /**
     * Relies on
     *
     * CREATE EXTENSION cube;
     * CREATE EXTENSION earthdistance;
     */
    private static final String CLOSEST_BRANCH_QUERY = "SELECT id, title, lat, lon, address, earth_distance(ll_to_earth(?, ?), ll_to_earth(lat, lon)) as distance from branches order by distance limit 1";

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
        return jdbcTemplate.query(CLOSEST_BRANCH_QUERY, this::fetchBranch, lat, lon);
    }

    private Branch fetchBranch(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            throw new NoSuchElementException("branch not found");
        }

        return rowMapper.mapRow(resultSet, 1);
    }

}
