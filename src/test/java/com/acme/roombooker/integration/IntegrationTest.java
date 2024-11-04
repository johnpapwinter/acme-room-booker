package com.acme.roombooker.integration;

import com.acme.roombooker.domain.enums.MeetingRoom;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Should return paginated bookings with a 200 status")
    void shouldReturnPaginatedBookings() throws Exception {

        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/get-all"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(3)));

    }

    @Test
    @DisplayName("Should book a meeting")
    void shouldBookMeeting() throws Exception {
        BookingDTO dto = new BookingDTO();
        dto.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        dto.setBookedBy("elmerfudd@acme.com");
        dto.setBookingDate(LocalDate.of(2024, 10, 30));
        dto.setStartTime(LocalTime.of(19, 0, 0));
        dto.setEndTime(LocalTime.of(20, 0, 0));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());

    }

    @Test
    @DisplayName("Should cancel a booking")
    void shouldCancelBooking() throws Exception {
        BookingDTO dto = new BookingDTO();
        dto.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        dto.setBookedBy("elmerfudd@acme.com");
        dto.setBookingDate(LocalDate.now());
        dto.setStartTime(LocalTime.of(9, 0, 0));
        dto.setEndTime(LocalTime.of(10, 0, 0));


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Long bookingId = Long.parseLong(result.getResponse().getContentAsString());

        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/cancel/{id}", bookingId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingId));

    }

    @Test
    @DisplayName("Should search a booking")
    void shouldSearchBooking() throws Exception {
        SearchFiltersDTO filters = new SearchFiltersDTO();
        filters.setMeetingRoom(MeetingRoom.ELMER_FUDD_ROOM);
        filters.setBookingDate(LocalDate.of(2024, 10, 12));

        Pageable pageable = PageRequest.of(0, 10);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filters))
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(1)));

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
