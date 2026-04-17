package com.amanda.event_platform.event.integration;


import com.amanda.event_platform.event.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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



    @AfterEach
    void cleanUp(){
        eventRepository.deleteAll();
    }

    @Test
    public void createEventSucessfully() throws Exception
    {
     String requestBody = """
           {
            "name": "Java Conference",
            "description": "Annual Java meetup",
            "location": "Sao Paulo",
            "startDateTime": "2026-12-20T10:00:00",
            "endDateTime": "2026-12-20T18:00:00",
            "capacity": 100           }
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


}
