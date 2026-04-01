package com.amanda.event_platform.event.service;

import com.amanda.event_platform.event.domain.Event;
import com.amanda.event_platform.event.domain.EventStatus;
import com.amanda.event_platform.event.dto.CreateEventRequest;
import com.amanda.event_platform.event.dto.EventResponse;
import com.amanda.event_platform.event.dto.UpdateEventRequest;
import com.amanda.event_platform.event.exception.EventNotFoundException;
import com.amanda.event_platform.event.exception.InvalidCapacityException;
import com.amanda.event_platform.event.exception.InvalidPeriodException;
import com.amanda.event_platform.event.mapper.EventMapper;
import com.amanda.event_platform.event.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;
    private final EventRepository repository;

    public EventServiceImpl(EventMapper eventMapper, EventRepository repository) {
        this.eventMapper = eventMapper;
        this.repository = repository;
    }

    @Override
    public EventResponse createEvent(CreateEventRequest request) {

        validateDates(request.startDateTime(), request.endDateTime());
        validateCapacity(request.capacity());

        Event event = eventMapper.toEntity(request);
        event.reserveSeat(request.capacity());
        event.setAvailableSeats(request.capacity());
        event.setStatus(EventStatus.PUBLISHED);

        Event savedEvent = repository.save(event);
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    public EventResponse getEventById(UUID id) {

        Event event = findEventOrThrow(id);
        return eventMapper.toResponse(event);
    }

    @Override
    public List<EventResponse> getAllEvents() {
        List<Event> events = repository.findAll();
        return events.stream().map(eventMapper::toResponse).toList();
    }

    @Override
    public EventResponse updateEvent(UUID id, UpdateEventRequest request) {
        Event existingEvent = findEventOrThrow(id);

        validateDates(request.startDateTime(), request.endDateTime());
        validateCapacity(request.capacity());

        existingEvent.setName(request.name());
        existingEvent.setDescription(request.description());
        existingEvent.setLocation(request.location());
        existingEvent.setStartDateTime(request.startDateTime());
        existingEvent.setEndDateTime(request.endDateTime());
        existingEvent.setCapacity(request.capacity());

        existingEvent.setAvailableSeats(request.capacity());
        Event updatedEvent = repository.save(existingEvent);

        return eventMapper.toResponse(updatedEvent);
    }

    @Override
    public void deleteEvent(UUID id) {
        Event existingEvent = findEventOrThrow(id);
        repository.delete(existingEvent);
    }

    private Event findEventOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EventNotFoundException("Event Not found"));
    }

    private void validateDates(LocalDateTime startDateTime, LocalDateTime endDateTime){
        if(!startDateTime.isBefore(endDateTime)){
            throw new InvalidPeriodException("Start date time must be before endDateTime");
        }
    }

    private void validateCapacity(Integer capacity){
        if(capacity == null || capacity <= 0)
        {
            throw  new InvalidCapacityException("capacity must be greater than 0");
        }
    }
}
