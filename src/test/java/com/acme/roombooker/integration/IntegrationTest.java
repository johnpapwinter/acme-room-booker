package com.acme.roombooker.integration;

import com.acme.roombooker.enums.MeetingRoom;
import com.acme.roombooker.dto.MeetingDTO;
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
    @DisplayName("Should return paginated meetings with a 200 status")
    void getAllMeetings_ShouldReturnPaginatedMeetings() throws Exception {

        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/meetings/get-all"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(3)));

    }

    @Test
    @DisplayName("Should book a meeting")
    void bookMeeting_ShouldBookNewMeeting() throws Exception {
        MeetingDTO dto = MeetingDTO.builder()
                .meetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM)
                .bookedBy("elmerfudd@acme.com")
                .bookingDate(LocalDate.of(2024, 10, 30))
                .startTime(LocalTime.of(19, 0, 0))
                .endTime(LocalTime.of(20, 0, 0))
                .build();

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/meetings/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());

    }

    @Test
    @DisplayName("Should cancel a meeting")
    void cancelMeeting_ShouldCancelExistingMeeting() throws Exception {
        MeetingDTO dto = MeetingDTO.builder()
                .meetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM)
                .bookedBy("elmerfudd@acme.com")
                .bookingDate(LocalDate.now())
                .startTime(LocalTime.of(9, 0, 0))
                .endTime(LocalTime.of(10, 0, 0))
                .build();


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/meetings/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Long bookingId = Long.parseLong(result.getResponse().getContentAsString());

        mockMvc
                .perform(MockMvcRequestBuilders.get("/api/meetings/cancel/{id}", bookingId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingId));

    }

    @Test
    @DisplayName("Should search a meeting and return findings")
    void searchMeeting_ShouldReturnFoundMeetings() throws Exception {
        SearchFiltersDTO filters = new SearchFiltersDTO();
        filters.setMeetingRoom(MeetingRoom.ELMER_FUDD_ROOM);
        filters.setBookingDate(LocalDate.of(2024, 10, 12));

        Pageable pageable = PageRequest.of(0, 10);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/api/meetings/search")
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
    void bookMeeting_ShouldReturnErrorForInvalidDTO() throws Exception {
        MeetingDTO dto = MeetingDTO.builder() // object empty, all required fields null
                .meetingRoom(null)
                .bookedBy(null)
                .bookingDate(null)
                .startTime(null)
                .endTime(null)
                .build();

        mockMvc.perform(post("/api/meetings/book")
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
    void searchMeetings_ShouldReturnErrorForInvalidSearchFilters() throws Exception {
        SearchFiltersDTO filtersDTO = new SearchFiltersDTO(); // object empty, all required fields null

        mockMvc.perform(post("/api/meetings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filtersDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isString())
                .andExpect(jsonPath("$[1]").isString());

    }

}
