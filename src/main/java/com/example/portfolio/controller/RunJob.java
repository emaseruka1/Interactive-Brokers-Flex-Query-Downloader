package com.example.portfolio.controller;

import com.example.portfolio.service.GetFlexXmlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RunJob {

    private GetFlexXmlStatement getFlexXmlStatement;

    private static final Logger log = LoggerFactory.getLogger(RunJob.class);

    @GetMapping("/run")
    public String runJob(){
        System.out.println(">>> SYSTEM PRINT TEST");
        log.info("Run job started");

        getFlexXmlStatement.downloadFlexXmlStatement();

        return "Job completed";
    }
}

