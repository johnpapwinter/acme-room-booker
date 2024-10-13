package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.Room;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchFiltersDTO {

    @NotNull
    private Room room;
    @NotNull
    private LocalDate bookingDate;

}
