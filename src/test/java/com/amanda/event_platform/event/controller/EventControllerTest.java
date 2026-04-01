package com.amanda.event_platform.event.controller;

import com.amanda.event_platform.event.domain.EventStatus;
import com.amanda.event_platform.event.dto.CreateEventRequest;
import com.amanda.event_platform.event.dto.EventResponse;
import com.amanda.event_platform.event.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @MockitoBean
    EventService eventService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception {

        CreateEventRequest request = new CreateEventRequest(
                "Java Conference",
                "Annual Java meetup",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100);

        EventResponse response = new EventResponse(
                UUID.randomUUID(),
                request.name(),
                request.description(),
                request.location(),
                request.startDateTime(),
                request.endDateTime(),
                request.capacity(),
                request.capacity(),
                EventStatus.PUBLISHED,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(eventService.createEvent(any(CreateEventRequest.class))).thenReturn(response);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.location").value(request.location()));
    }
}
