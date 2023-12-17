package com.example.ordersservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LivenessController {

    @GetMapping("/")
    public ResponseEntity<?> isServiceReady() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
