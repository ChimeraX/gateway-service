package org.chimerax.chimeraxgateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Author: Silviu-Mihnea Cucuiet
 * Date: 11-Jun-20
 * Time: 10:44 AM
 */

@RestController
@RequestMapping("/csrf")
public class FallbackController {

    @GetMapping("/csrf")
    public ResponseEntity<Object> csrf() {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .body(new HashMap<>());

    }
}
