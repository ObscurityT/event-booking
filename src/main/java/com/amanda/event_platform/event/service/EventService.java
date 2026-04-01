package com.amanda.event_platform.event.service;

import com.amanda.event_platform.event.domain.Event;
import com.amanda.event_platform.event.dto.CreateEventRequest;
import com.amanda.event_platform.event.dto.EventResponse;
import com.amanda.event_platform.event.dto.UpdateEventRequest;

import java.util.List;
import java.util.UUID;


public interface EventService {
    EventResponse createEvent(CreateEventRequest request);
    EventResponse getEventById(UUID id);
    List<EventResponse> getAllEvents();
    EventResponse updateEvent(UUID id, UpdateEventRequest request);
    void deleteEvent(UUID id);
}
