package com.acme.roombooker.controller;

import com.acme.roombooker.dto.AcmeUserDTO;
import com.acme.roombooker.service.AcmeUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AcmeUserController {

    private final AcmeUserService acmeUserService;

    public AcmeUserController(AcmeUserService acmeUserService) {
        this.acmeUserService = acmeUserService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<AcmeUserDTO>> getAllAcmeUsers(Pageable pageable) {
        Page<AcmeUserDTO> response = acmeUserService.getAcmeUsers(pageable);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/toggle-role/{userId}")
    public ResponseEntity<Long> toggleRole(@PathVariable Long userId) {
        Long response = acmeUserService.toggleAcmeUserRole(userId);

        return ResponseEntity.ok().body(response);
    }

}
