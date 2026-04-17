package com.amanda.event_platform.event.controller;

import com.amanda.event_platform.event.domain.EventStatus;
import com.amanda.event_platform.event.dto.CreateEventRequest;
import com.amanda.event_platform.event.dto.EventResponse;
import com.amanda.event_platform.event.dto.UpdateEventRequest;
import com.amanda.event_platform.event.exception.EventNotFoundException;
import com.amanda.event_platform.event.exception.InvalidPeriodException;
import com.amanda.event_platform.event.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    public void createEvent_whenInvalidRequest_returnsBadRequest() throws Exception {

        CreateEventRequest request = new CreateEventRequest(
                null,
                "Annual Java meetup",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100);


        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any());
    }

    @Test
    public void createEvent_whenInvalidPeriod_returnsBadRequest() throws Exception {

        CreateEventRequest request = new CreateEventRequest(
                "Java Conference",
                "Annual Java meetup",
                "São Paulo",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                100);


        String jsonRequest = objectMapper.writeValueAsString(request);

        when(eventService.createEvent(any(CreateEventRequest.class))).thenThrow(new InvalidPeriodException("End date must be after start date"));
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(eventService).createEvent(any(CreateEventRequest.class));
    }

    @Test
    public void createEvent_whenInvalidCapacity_returnsBadRequest() throws Exception {

        CreateEventRequest request = new CreateEventRequest(
                "Java Conference",
                "Annual Java meetup",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null);


        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(eventService,never()).createEvent(any(CreateEventRequest.class));
    }

    @Test
    public void createEvent_whenBlankLocation_returnsBadRequest() throws Exception {

        CreateEventRequest request = new CreateEventRequest(
                "Java Conference",
                "Annual Java meetup",
                "",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100);


        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any());
    }

    @Test
    public void getEventById_whenEventExists_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();

        EventResponse response = new EventResponse(
                id,
                "Java Conference",
                "Annual Java meetup",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100,
                100,
                EventStatus.PUBLISHED,
                LocalDateTime.now(),
                LocalDateTime.now());


        when(eventService.getEventById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java Conference"))
                .andExpect(jsonPath("$.location").value("São Paulo"));
    }


    @Test
    public void getEventById_WhenEventNotFound_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(eventService.getEventById(id)).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(get("/api/v1/events/{id}", id))
                .andExpect(status().isNotFound());

        verify(eventService).getEventById(id);
    }

    @Test
    public void getAllEvents_whenEventsExist_returnsListOfEventResponses() throws Exception {
        EventResponse response = new EventResponse(
                UUID.randomUUID(),
                "Java Conference",
                "Annual Java meetup",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100,
                100,
                EventStatus.PUBLISHED,
                LocalDateTime.now(),
                LocalDateTime.now());

        EventResponse response1 = new EventResponse(
                UUID.randomUUID(),
                "Python Conference",
                "Annual Python meetup",
                "Ribeirão Preto",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100,
                100,
                EventStatus.PUBLISHED,
                LocalDateTime.now(),
                LocalDateTime.now());

        List<EventResponse> eventResponsesList = List.of(response, response1);

        when(eventService.getAllEvents()).thenReturn(eventResponsesList);

        mockMvc.perform(get("/api/v1/events")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Java Conference"))
                .andExpect(jsonPath("$[1].name").value("Python Conference"))
                .andExpect(jsonPath("$[0].location").value("São Paulo"))
                .andExpect(jsonPath("$[1].location").value("Ribeirão Preto"));
    }

    @Test
    public void getAllEvents_whenEmpty_returnsEmptyList() throws Exception
    {
        when(eventService.getAllEvents()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/events")).andExpect(status().isOk()).andExpect(jsonPath("$")
                .isEmpty());

        verify(eventService).getAllEvents();
    }


    @Test
    public void updateEvent_whenEventExist_returnEventResponses() throws Exception {

        UUID id = UUID.randomUUID();

        UpdateEventRequest request = new UpdateEventRequest(
                "Python Conference",
                "Annual Python meetup",
                "Ribeirão Preto",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                50
        );

        EventResponse response = new EventResponse(
                id,
                "Python Conference",
                "Annual Python meetup",
                "Ribeirão Preto",
                request.startDateTime(),
                request.endDateTime(),
                50,
                50,
                EventStatus.PUBLISHED,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );


        when(eventService.updateEvent(eq(id), any(UpdateEventRequest.class))).thenReturn(response);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/v1/events/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Python Conference"))
                .andExpect(jsonPath("$.location").value("Ribeirão Preto"))
                .andExpect(jsonPath("$.capacity").value(50))
                .andExpect(jsonPath("$.availableSeats").value(50));
    }

    @Test
    public void updateEvent_whenEventNotFound_returnsNotFound() throws Exception {

        UUID id = UUID.randomUUID();

        UpdateEventRequest request = new UpdateEventRequest(
                "Updated Event",
                "Description",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100);

        when(eventService.updateEvent(eq(id), any(UpdateEventRequest.class))).thenThrow(new EventNotFoundException("Event not found"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/v1/events/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());

        verify(eventService).updateEvent(eq(id), any(UpdateEventRequest.class));
    }

    @Test
    public void updateEvent_whenInvalidRequest_returnsBadRequest() throws Exception
    {
        UUID id = UUID.randomUUID();

        UpdateEventRequest request = new UpdateEventRequest(
                null,
                "Description",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/v1/events/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isBadRequest());

        verify(eventService,never()).updateEvent(eq(id), any(UpdateEventRequest.class));
    }

    @Test
    public void updateEvent_whenInvalidPeriod_returnsBadRequest() throws Exception
    {
        UUID id = UUID.randomUUID();

        UpdateEventRequest request = new UpdateEventRequest(
                "Java Conference",
                "Description",
                "São Paulo",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                100);

        String jsonRequest = objectMapper.writeValueAsString(request);

        when(eventService.updateEvent(eq(id),any(UpdateEventRequest.class))).thenThrow(new InvalidPeriodException("The period is invalid"));

        mockMvc.perform(put("/api/v1/events/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isBadRequest());

        verify(eventService).updateEvent(eq(id), any(UpdateEventRequest.class));
    }


    @Test
    public void updateEvent_whenInvalidCapacity_returnsBadRequest() throws Exception
    {
        UUID id = UUID.randomUUID();

        UpdateEventRequest request = new UpdateEventRequest(
                "Java Conference",
                "Description",
                "São Paulo",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/v1/events/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isBadRequest());

        verify(eventService, never()).updateEvent(eq(id), any(UpdateEventRequest.class));
    }

    @Test
    public void deleteEvent_whenEventExists_returnsNoContent() throws Exception{

        UUID id = UUID.randomUUID();

        doNothing().when(eventService).deleteEvent(id);
        mockMvc.perform(delete("/api/v1/events/{id}", id))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(id);
    }



    @Test
    public void deleteEvent_whenEventNotFound_returnsNotFound() throws Exception{

        UUID id = UUID.randomUUID();

        doThrow(new EventNotFoundException("Event not found")).when(eventService).deleteEvent(id);

        mockMvc.perform(delete("/api/v1/events/{id}", id))
                .andExpect(status().isNotFound());

        verify(eventService).deleteEvent(id);
    }

}
