package com.amanda.event_platform.event.service;

import com.amanda.event_platform.event.domain.Event;
import com.amanda.event_platform.event.domain.EventStatus;
import com.amanda.event_platform.event.dto.CreateEventRequest;
import com.amanda.event_platform.event.dto.EventResponse;
import com.amanda.event_platform.event.dto.UpdateEventRequest;
import com.amanda.event_platform.event.exception.EventNotFoundException;
import com.amanda.event_platform.event.exception.InvalidCapacityException;
import com.amanda.event_platform.event.exception.InvalidPeriodException;
import com.amanda.event_platform.event.repository.EventRepository;
import com.amanda.event_platform.event.mapper.EventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    //Builders - Build reusable test data
    private Event buildEvent() {
        return Event.builder().id(UUID.randomUUID())
                .name("Java Conference")
                .description("Annual Java meetup")
                .location("São Paulo")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .availableSeats(100)
                .status(EventStatus.PUBLISHED)
                .build();
    }

    private CreateEventRequest buildCreateRequest = new CreateEventRequest(
            "Java Conference",
            "Annual Java meetup",
            "São Paulo",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            100
    );

    private UpdateEventRequest buildUpdateRequest = new UpdateEventRequest(
            "Python Conference",
            "Annual Python meetup",
            "Ribeirao Preto",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            50
    );


    private EventResponse buildResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getCapacity(),
                event.getAvailableSeats(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );

    }

    //Create Event
    @Test
    void testCreateEvent() {
        CreateEventRequest request = buildCreateRequest;
        Event event = buildEvent();
        EventResponse expectedResponse = buildResponse(event);

        when(eventMapper.toEntity(request)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(expectedResponse);

        //act
        EventResponse result = eventService.createEvent(request);

        //assert
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void createEvent_whenEndDateBeforeStartDate_throwsInvalidPeriodException() {
        CreateEventRequest request = new CreateEventRequest(
                "Event", "desc", "location",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                50
        );

        assertThrows(InvalidPeriodException.class, () -> eventService.createEvent(request));
    }

    @Test
    void createEvent_whenCapacityIsZero_throwsInvalidCapacityException() {
        CreateEventRequest request = new CreateEventRequest(
                "Event", "desc", "location",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                0
        );

        assertThrows(InvalidCapacityException.class, () -> eventService.createEvent(request));
    }

    @Test
    void createEvent_whenCapacityIsNull_throwsInvalidCapacityException() {
        CreateEventRequest request = new CreateEventRequest(
                "Event", "desc", "location",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null
        );

        assertThrows(InvalidCapacityException.class, () -> eventService.createEvent(request));
    }

    //Getters

    //Get Event by Id
    @Test
    void getEventById_whenEventExists_returnsEventResponse() {
        Event event = buildEvent();
        EventResponse response = buildResponse(event);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.getEventById(event.getId());

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getEventById_whenEventNotFound_throwsEventNotFoundException() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(id));
    }

    //get all Events
    @Test
    void getAllEvents_returnsListOfEventResponses() {
        Event event = buildEvent();
        Event event1 = buildEvent();

        EventResponse response = buildResponse(event);
        EventResponse response1 = buildResponse(event1);

        List<Event> eventList = new ArrayList<>();
        List<EventResponse> expectedResponseList = new ArrayList<>();

        eventList.add(event);
        eventList.add(event1);

        expectedResponseList.add(response);
        expectedResponseList.add(response1);

        when(eventRepository.findAll()).thenReturn(eventList);
        when(eventMapper.toResponse(event)).thenReturn(response);
        when(eventMapper.toResponse(event1)).thenReturn(response1);

        List<EventResponse> result = eventService.getAllEvents();

        assertThat(result).isEqualTo(expectedResponseList);
    }

    @Test
    void getAllEvents_whenEmpty_returnsEmptyList() {
        List<Event> events = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(events);

        List<EventResponse> result = eventService.getAllEvents();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    //Update Event
    @Test
    void updateEvent_whenValidRequest_returnsUpdatedEventResponse() {
        Event existingEvent = buildEvent();
        UpdateEventRequest updateRequest = buildUpdateRequest;
        EventResponse expectedResponse = buildResponse(existingEvent);

        when(eventRepository.findById(existingEvent.getId())).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);
        when(eventMapper.toResponse(existingEvent)).thenReturn(expectedResponse);

        EventResponse result = eventService.updateEvent(existingEvent.getId(), updateRequest);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedResponse);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void updateEvent_whenEndDateBeforeStartDate_throwsInvalidPeriodException() {
        Event existingEvent = buildEvent();
        UpdateEventRequest updateEventRequest = new UpdateEventRequest(
                "Python Conference",
                "Annual Python meetup",
                "Ribeirao Preto",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1),
                50
        );

        when(eventRepository.findById(existingEvent.getId())).thenReturn(Optional.of(existingEvent));

        assertThrows(InvalidPeriodException.class, () -> eventService.updateEvent(existingEvent.getId(), updateEventRequest));
    }

    @Test
    void updateEvent_whenCapacityIsNull_throwsInvalidCapacityException() {
        Event existingEvent = buildEvent();
        UpdateEventRequest updateEventRequest = new UpdateEventRequest(
                "Python Conference",
                "Annual Python meetup",
                "Ribeirao Preto",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null
        );

        when(eventRepository.findById(existingEvent.getId())).thenReturn(Optional.of(existingEvent));
        assertThrows(InvalidCapacityException.class, () -> eventService.updateEvent(existingEvent.getId(), updateEventRequest));
    }

    @Test
    void updateEvent_whenCapacityIsZero_throwsInvalidCapacityException() {
        Event existingEvent = buildEvent();
        UpdateEventRequest updateEventRequest = new UpdateEventRequest(
                "Python Conference",
                "Annual Python meetup",
                "Ribeirao Preto",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                0
        );

        when(eventRepository.findById(existingEvent.getId())).thenReturn(Optional.of(existingEvent));
        assertThrows(InvalidCapacityException.class, () -> eventService.updateEvent(existingEvent.getId(), updateEventRequest));
    }


    @Test
    void updateEvent_whenEventNotFound_throwsEventNotFoundException() {
        UUID id = UUID.randomUUID();
        UpdateEventRequest updateEventRequest = new UpdateEventRequest(
                "Python Conference",
                "Annual Python meetup",
                "Ribeirao Preto",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                0
        );

        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(id, updateEventRequest));
    }

    @Test
    void deleteEvent_whenEventExists_deletesSuccessfully() {
        Event event = buildEvent();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        eventService.deleteEvent(event.getId());

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEvent_whenEventNotFound_throwsEventNotFoundException(){
        UUID id = UUID.randomUUID();

        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(id));
    }

}
