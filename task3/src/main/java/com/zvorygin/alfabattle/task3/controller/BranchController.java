package com.zvorygin.alfabattle.task3.controller;

import com.zvorygin.alfabattle.task3.model.Branch;
import com.zvorygin.alfabattle.task3.model.ErrorResponse;
import com.zvorygin.alfabattle.task3.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@RestController
public class BranchController {
    private static final Pattern LAT_LON = Pattern.compile("lat=[0-9.]*&lon=[0-9.]*");

    private final BranchRepository branchRepository;

    @Autowired
    public BranchController(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @GetMapping("/branches/{id}")
    public Branch getBranch(@PathVariable("id") long id) {
        return branchRepository.get(id);
    }

    @GetMapping("/branches/{id}/predict")
    public Branch getBranch(@PathVariable("id") long id,
            @RequestParam("dayOfWeek") int dayOfWeek,
            @RequestParam("hourOfDay") int hourOfDay) {
        Branch branch = branchRepository.get(id);

        branch.setPredicting(branchRepository.getServiceTime(branch, dayOfWeek, hourOfDay));

        return branch;
    }

    @GetMapping("/branches")
    public Branch getClosestBranch(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        return branchRepository.getClosestBranch(lat, lon);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse(e.getMessage());
    }
}