package com.acme.roombooker.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchFiltersDTO {

    private String room;
    private LocalDate bookingDate;

}
