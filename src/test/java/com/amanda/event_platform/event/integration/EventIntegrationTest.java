package com.amanda.event_platform.event.integration;


import com.amanda.event_platform.event.domain.Event;
import com.amanda.event_platform.event.domain.EventStatus;
import com.amanda.event_platform.event.dto.UpdateEventRequest;
import com.amanda.event_platform.event.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private  EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp(){
        eventRepository.deleteAll();
    }

    @Test
    public void createEventSuccessfully() throws Exception
    {
     String requestBody = """
           {
            "name": "Java Conference",
            "description": "Annual Java meetup",
            "location": "Sao Paulo",
            "startDateTime": "2026-12-20T10:00:00",
            "endDateTime": "2026-12-20T18:00:00",
            "capacity": 100}
           """;

     mockMvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(requestBody))
             .andExpect(status().isCreated())
             .andExpect(jsonPath("$.name").value("Java Conference"))
             .andExpect(jsonPath("$.location").value("Sao Paulo"))
             .andExpect(jsonPath("$.capacity").value(100))
             .andExpect(jsonPath("$.availableSeats").value(100))
             .andExpect(jsonPath("$.status").value("PUBLISHED"))
             .andExpect(jsonPath("$.id").exists());

        assertThat(eventRepository.count()).isEqualTo(1);

    }

    @Test
    public void createEventInvalidPeriod() throws Exception
    {
        String requestBody = """
           {
            "name": "Java Conference",
            "description": "Annual Java meetup",
            "location": "Sao Paulo",
            "startDateTime": "2026-12-20T10:00:00",
            "endDateTime": "1992-12-20T18:00:00",
            "capacity": 100           
            }
           """;

        mockMvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());

        assertThat(eventRepository.count()).isEqualTo(0);

    }

    @Test
    public void createEventInvalidCapacity() throws Exception
    {
        String requestBody = """
           {
            "name": "Java Conference",
            "description": "Annual Java meetup",
            "location": "Sao Paulo",
            "startDateTime": "2026-12-20T10:00:00",
            "endDateTime": "2026-12-20T18:00:00",
            "capacity": 0 }
           """;

        mockMvc.perform(post("/api/v1/events").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());

        assertThat(eventRepository.count()).isEqualTo(0);

    }

    @Test
    public void getEventSuccessfully() throws Exception
    {
        Event event = new Event();

        event.setName("Java Conference");
        event.setDescription("Annual Java meetup");
        event.setLocation("Sao Paulo");
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setEndDateTime(LocalDateTime.now().plusDays(2));
        event.setCapacity(100);
        event.setAvailableSeats(100);
        event.setStatus(EventStatus.PUBLISHED);

        Event savedEvent = eventRepository.save(event);

        UUID id = savedEvent.getId();

        mockMvc.perform(get("/api/v1/events/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Java Conference"))
                .andExpect(jsonPath("$.location").value("Sao Paulo"))
                .andExpect(jsonPath("$.capacity").value(100));

        assertThat(eventRepository.findById(id)).isPresent();

    }

    @Test
    public void getInexistentEvent() throws Exception
    {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/events/{id}", id)).andExpect(status().isNotFound());

    }

    @Test
    public void getAllEventsSuccessfully() throws Exception
    {
        List<Event> eventList = new ArrayList<>();

        Event event = new Event();

        event.setName("Java Conference");
        event.setDescription("Annual Java meetup");
        event.setLocation("Sao Paulo");
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setEndDateTime(LocalDateTime.now().plusDays(2));
        event.setCapacity(100);
        event.setAvailableSeats(100);
        event.setStatus(EventStatus.PUBLISHED);

        Event event1 = new Event();

        event1.setName("Python Conference");
        event1.setDescription("Annual Python meetup");
        event1.setLocation("Recife");
        event1.setStartDateTime(LocalDateTime.now().plusDays(1));
        event1.setEndDateTime(LocalDateTime.now().plusDays(2));
        event1.setCapacity(100);
        event1.setAvailableSeats(100);
        event1.setStatus(EventStatus.PUBLISHED);

        eventList.add(event);
        eventList.add(event1);

        eventRepository.saveAll(eventList);

        mockMvc.perform(get("/api/v1/events")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java Conference"))
                .andExpect(jsonPath("$[1].name").value("Python Conference"))
                .andExpect(jsonPath("$[0].location").value("Sao Paulo"))
                .andExpect(jsonPath("$[1].location").value("Recife"))
                .andExpect(jsonPath("$[0].capacity").value(100))
                .andExpect(jsonPath("$[1].capacity").value(100));


    }

    @Test
    public void getAllEventEmpty() throws Exception
    {
        mockMvc.perform(get("/api/v1/events")).andExpect(status().isOk());
    }

    @Test
    public void updateEventSuccessfully() throws Exception
    {
        Event event = new Event();

        event.setName("Java Conference");
        event.setDescription("Annual Java meetup");
        event.setLocation("Sao Paulo");
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setEndDateTime(LocalDateTime.now().plusDays(2));
        event.setCapacity(100);
        event.setAvailableSeats(100);
        event.setStatus(EventStatus.PUBLISHED);

        Event savedEvent = eventRepository.save(event);

        UUID id = savedEvent.getId();

        String requestBody = """
           {
            "name": "Python Conference",
            "description": "Annual Python meetup",
            "location": "Recife",
            "startDateTime": "2026-12-20T10:00:00",
            "endDateTime": "2026-12-20T18:00:00",
            "capacity": 100 }
           """;

        mockMvc.perform(put("/api/v1/events/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Python Conference"))
                .andExpect(jsonPath("$.description").value("Annual Python meetup"))
                .andExpect(jsonPath("$.location").value("Recife"))
                .andExpect(jsonPath("$.capacity").value(100));;

        Event updatedEvent = eventRepository.findById(id).orElseThrow();

        assertThat(updatedEvent.getName()).isEqualTo("Python Conference");
        assertThat(updatedEvent.getDescription()).isEqualTo("Annual Python meetup");
        assertThat(updatedEvent.getLocation()).isEqualTo("Recife");
        assertThat(updatedEvent.getCapacity()).isEqualTo(100);
    }

    @Test
    public void updateEventNotFound() throws Exception
    {
        UUID id = UUID.randomUUID();

        UpdateEventRequest eventRequest = new UpdateEventRequest(
                "Python Conference",
                "Annual Python meetup",
                "Recife",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                100
        );

        mockMvc.perform(put("/api/v1/events/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isNotFound());

    }

    @Test
    public void updateInvalidEvent() throws Exception
    {
        UUID id = UUID.randomUUID();

        UpdateEventRequest eventRequest = new UpdateEventRequest(
                "",
                "",
                "",
                LocalDateTime.of(1990,10,10,1,1),
                LocalDateTime.of(1990,10,10,1,1),
                null
        );

        mockMvc.perform(put("/api/v1/events/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void updateEventWithInvalidPeriod() throws Exception
    {
        Event event = new Event();

        event.setName("Java Conference");
        event.setDescription("Annual Java meetup");
        event.setLocation("Sao Paulo");
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setEndDateTime(LocalDateTime.now().plusDays(2));
        event.setCapacity(100);
        event.setAvailableSeats(100);
        event.setStatus(EventStatus.PUBLISHED);

        Event savedEvent = eventRepository.save(event);

        UUID id = savedEvent.getId();

        String requestBody = """
           {
            "name": "Python Conference",
            "description": "Annual Python meetup",
            "location": "Recife",
            "startDateTime": "2026-12-20T18:00:00",
            "endDateTime": "2026-12-20T10:00:00",
            "capacity": 100 }
           """;

        mockMvc.perform(put("/api/v1/events/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void updateEventWithInvalidCapacity() throws Exception
    {
        Event event = new Event();

        event.setName("Java Conference");
        event.setDescription("Annual Java meetup");
        event.setLocation("Sao Paulo");
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setEndDateTime(LocalDateTime.now().plusDays(2));
        event.setCapacity(100);
        event.setAvailableSeats(20);
        event.setStatus(EventStatus.PUBLISHED);

        Event savedEvent = eventRepository.save(event);

        UUID id = savedEvent.getId();

        String requestBody = """
           {
            "name": "Python Conference",
            "description": "Annual Python meetup",
            "location": "Recife",
            "startDateTime": "2026-12-20T10:00:00",
            "endDateTime": "2026-12-20T18:00:00",
            "capacity": 50 }
           """;

        mockMvc.perform(put("/api/v1/events/{id}", id).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());

    }
    @Test
    public void deleteEventSuccessfully() throws Exception
    {
        Event event = new Event();

        event.setName("Java Conference");
        event.setDescription("Annual Java meetup");
        event.setLocation("Sao Paulo");
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        event.setEndDateTime(LocalDateTime.now().plusDays(2));
        event.setCapacity(100);
        event.setAvailableSeats(20);
        event.setStatus(EventStatus.PUBLISHED);

        Event savedEvent = eventRepository.save(event);

        UUID id = savedEvent.getId();

        mockMvc.perform(delete("/api/v1/events/{id}", id)).andExpect(status().isNoContent());

        assertThat(eventRepository.findById(id).isEmpty());
    }

    @Test
    public void deleteEventNotFound() throws Exception
    {

        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/events/{id}", id)).andExpect(status().isNotFound());

    }
}
