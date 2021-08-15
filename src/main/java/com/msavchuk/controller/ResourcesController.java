package com.msavchuk.controller;

import com.msavchuk.config.constant.ApiVersion;
import com.msavchuk.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping(ApiVersion.API_PREFIX + "/resources")
@Slf4j
public class ResourcesController {


    @PreAuthorize("hasAuthority(read)")
    @GetMapping(value = {"/test-read"})
    public ResponseEntity<MessageDto> users() {
        return ResponseEntity.ok(new MessageDto("ok"));
    }


    @PreAuthorize("hasAuthority(write)")
    @GetMapping(value = {"/test-write"})
    public ResponseEntity<MessageDto> user() {
        return ResponseEntity.ok(new MessageDto("ok"));
    }
}
