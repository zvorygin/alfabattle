package com.zvorygin.alfabattle.task3.repository;

import com.zvorygin.alfabattle.task3.model.Branch;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Repository
public class BranchRepository {

    private static final String BRANCH_QUERY = "SELECT id, title, lat, lon, address from branches where id = ?";

    private static final String CLOSEST_BRANCH_QUERY = "SELECT id, title, lat, lon, address from branches";

    private static final String QUEUE_LOG =
            "select extract('epoch' from end_time_of_wait - start_time_of_wait) as wait_time from queue_log "
            + "where branches_id = ? "
                    + "and date_part('isodow', data) = ? "
                    + "and date_part('hour', end_time_of_service) = ?";

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
                bestDistance = newDistance;
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

    public long getServiceTime(Branch branch, int dayOfWeek, int hourOfDay) {
        Long waitTime = jdbcTemplate.query(QUEUE_LOG, this::fetchWaitTimeMedian, branch.getId(), dayOfWeek, hourOfDay);

        if (waitTime == null) {
            throw new NoSuchElementException("wait time not found");
        }

        return waitTime;
    }

    private Long fetchWaitTimeMedian(ResultSet resultSet) throws SQLException {
        List<Long> waitTimes = new ArrayList<>();

        while (resultSet.next()) {
            waitTimes.add(resultSet.getLong(1));
        }

        if (waitTimes.isEmpty())
            return null;

        double[] doubles = waitTimes.stream().mapToDouble(Double::valueOf).toArray();
        if (doubles.length == 0) {
            return null;
        }

        return (long)new Median().evaluate(doubles);
    }
}
