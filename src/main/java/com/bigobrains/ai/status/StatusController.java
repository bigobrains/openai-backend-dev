package com.bigobrains.ai.status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping(value = "/status")
    public String status() {
        return "UP";
    }
}
