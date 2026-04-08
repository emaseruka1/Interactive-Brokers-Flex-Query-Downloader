package com.example.portfolio.controller;

import com.example.portfolio.service.GetFlexXmlStatement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RunJob {

    private GetFlexXmlStatement getFlexXmlStatement;

    @GetMapping("/run")
    public String runJob(){

        getFlexXmlStatement.downloadFlexXmlStatement();

        return "Job completed";
    }
}

