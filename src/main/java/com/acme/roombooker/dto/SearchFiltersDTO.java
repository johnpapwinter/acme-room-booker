package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.Room;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchFiltersDTO {

    private Room room;
    private LocalDate bookingDate;

}
