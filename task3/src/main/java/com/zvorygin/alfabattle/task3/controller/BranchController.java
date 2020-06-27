package com.zvorygin.alfabattle.task3.controller;

import com.zvorygin.alfabattle.task3.model.Branch;
import com.zvorygin.alfabattle.task3.model.ErrorResponse;
import com.zvorygin.alfabattle.task3.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.NoSuchElementException;

@RestController
public class BranchController {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchController(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @GetMapping("/branches/{id}")
    public Branch getBranch(@PathVariable("id") long id) {
        return branchRepository.get(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ErrorResponse handleException(Exception e) {
        return new ErrorResponse(e.getMessage());
    }
}