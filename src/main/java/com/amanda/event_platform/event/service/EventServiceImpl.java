package com.amanda.event_platform.event.service;

import com.amanda.event_platform.event.domain.Event;
import com.amanda.event_platform.event.dto.CreateEventRequest;
import com.amanda.event_platform.event.dto.EventResponse;
import com.amanda.event_platform.event.mapper.EventMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventServiceImpl implements EventService{

    private final EventMapper eventMapper;

    public EventServiceImpl(EventMapper eventMapper)
    {
        this.eventMapper = eventMapper;
    }

    @Override
    public EventResponse createEvent(CreateEventRequest request) {
        //validate data
        //convert DTO -> Entity
        //apply business logic

        if(request.capacity() > event.getAvailableSeats())
        {
            throw new RuntimeException("The capacity exceeds the available seats");
        }

        if(request){}
        // save on the repo
        Event event = eventMapper.toEntity(request);

        //convert entity to dto
        //return response
    }

    @Override
    public EventResponse getEventById(UUID id) {
        return null;
    }

    @Override
    public List<EventResponse> getAllEvents() {
        return List.of();
    }

    @Override
    public EventResponse updateEvent(UUID id, CreateEventRequest request) {
        return null;
    }

    @Override
    public void deleteEvent(UUID id) {

    }
}
