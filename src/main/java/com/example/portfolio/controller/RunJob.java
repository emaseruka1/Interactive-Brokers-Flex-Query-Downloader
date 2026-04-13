package com.example.portfolio.controller;

import com.example.portfolio.service.GcsGetFlexXmlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Profile("!test")
public class RunJob {

    private final GcsGetFlexXmlStatement getFlexXmlStatement;

    public RunJob(GcsGetFlexXmlStatement getFlexXmlStatement) {
        this.getFlexXmlStatement = getFlexXmlStatement;
    }

    private static final Logger log = LoggerFactory.getLogger(RunJob.class);

    @GetMapping("/run")
    public String runJob(){

        log.info("Run job started");

        getFlexXmlStatement.downloadFlexXmlStatement();

        return "Job completed";
    }
}

