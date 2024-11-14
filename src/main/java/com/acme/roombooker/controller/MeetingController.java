package com.acme.roombooker.controller;

import com.acme.roombooker.dto.MeetingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.security.AcmePrincipal;
import com.acme.roombooker.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }


    @GetMapping("/get-all")
    public ResponseEntity<Page<MeetingDTO>> getAllMeetings(Pageable pageable) {
        Page<MeetingDTO> response = meetingService.getAllMeetings(pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/book")
    public ResponseEntity<Long> bookMeeting(@Valid @RequestBody MeetingDTO dto,
                                            @AuthenticationPrincipal AcmePrincipal acmePrincipal) {
        Long result = meetingService.addMeeting(dto, acmePrincipal);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/cancel/{id}")
    public ResponseEntity<MeetingDTO> cancelMeeting(@PathVariable Long id) {
        MeetingDTO result = meetingService.cancelMeeting(id);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<MeetingDTO>> searchMeetings(@Valid @RequestBody SearchFiltersDTO filters,
                                                           Pageable pageable) {
        Page<MeetingDTO> response = meetingService.search(filters, pageable);

        return ResponseEntity.ok(response);
    }

}
