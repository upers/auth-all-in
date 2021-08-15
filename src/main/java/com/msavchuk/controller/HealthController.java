package com.msavchuk.controller;

import com.msavchuk.config.constant.ApiVersion;
import com.msavchuk.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
@RequestMapping(ApiVersion.API_PREFIX + "/health")
@Slf4j
public class HealthController {


    @GetMapping(value = {""})
    public ResponseEntity<?> users() {
        return ResponseEntity.ok().build();
    }

}
