package com.amanda.event_platform.event.mapper;

import com.amanda.event_platform.event.domain.Event;
import com.amanda.event_platform.event.dto.CreateEventRequest;
import com.amanda.event_platform.event.dto.EventResponse;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(CreateEventRequest eventRequest){
        Event event = new Event();

        event.setName(eventRequest.name());
        event.setDescription(eventRequest.description());
        event.setLocation(eventRequest.location());
        event.setStartDateTime(eventRequest.startDateTime());
        event.setEndDateTime(eventRequest.endDateTime());
        event.setCapacity(eventRequest.capacity());

        return event;
    }

    public EventResponse toResponse(Event event)
    {
         return new EventResponse(event.getId(), event.getName(), event.getDescription(),
                event.getLocation(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getCapacity(),
                event.getAvailableSeats(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getUpdatedAt());
    }
}

