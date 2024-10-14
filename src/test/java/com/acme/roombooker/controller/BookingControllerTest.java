package com.acme.roombooker.controller;

import com.acme.roombooker.domain.enums.Room;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Should book a valid meeting")
    void shouldBookMeeting() throws Exception {
        BookingDTO dto = new BookingDTO();
        dto.setRoom(Room.MAIN_CONFERENCE_ROOM);
        dto.setBookedBy("wilecoyote@acme.com");
        dto.setBookingDate(LocalDate.now());
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(15, 0));

        when(bookingService.addBooking(any(BookingDTO.class))).thenReturn(3L);

        mockMvc.perform(post("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("Should return an array of validation errors for invalid BookingDTO")
    void shouldReturnErrorForInvalidBookingDTO() throws Exception {
        BookingDTO dto = new BookingDTO(); // object empty, all required fields null

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isString())
                .andExpect(jsonPath("$[1]").isString())
                .andExpect(jsonPath("$[2]").isString())
                .andExpect(jsonPath("$[3]").isString())
                .andExpect(jsonPath("$[4]").isString());

    }

    @Test
    @DisplayName("Should cancel a booking")
    void shouldCancelBooking() throws Exception {
        BookingDTO dto = new BookingDTO();
        dto.setId(4L);
        dto.setRoom(Room.MAIN_CONFERENCE_ROOM);
        dto.setBookedBy("wilecoyote@acme.com");
        dto.setBookingDate(LocalDate.now());
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(15, 0));

        when(bookingService.cancelBooking(4L)).thenReturn(dto);

        mockMvc.perform(get("/api/cancel/4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));

    }

    @Test
    @DisplayName("Should return an array of validation errors for invalid search filters")
    void shouldReturnErrorForInvalidSearchFilters() throws Exception {
        SearchFiltersDTO filtersDTO = new SearchFiltersDTO(); // object empty, all required fields null

        mockMvc.perform(post("/api/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filtersDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isString())
                .andExpect(jsonPath("$[1]").isString());

    }

}