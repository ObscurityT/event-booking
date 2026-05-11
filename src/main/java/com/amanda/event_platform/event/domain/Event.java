package com.amanda.event_platform.event.domain;

import com.amanda.event_platform.event.exception.InvalidCapacityException;
import com.amanda.event_platform.event.exception.InvalidPeriodException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;
    private String location;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private Integer capacity;
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    public Event (String name, String location, Integer capacity, LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        if(name == null || name.isEmpty())
        {
            throw new RuntimeException("Name must be valid");
        }
        this.name = name;
        this.location = location;

        initializeCapacity(capacity);
        initializeSchedule(startDateTime,endDateTime);

    }


    public void validateCapacity(Integer capacity)
    {
        if(capacity == null || capacity <= 0)
        {
            throw new InvalidCapacityException("Capacity must be greater than zero");
        }
    }

    public void initializeSchedule(LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        validateSchedule(startDateTime,endDateTime);

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

    }

    public void validateSchedule(LocalDateTime startDateTime, LocalDateTime endDateTime)
    {
        if(startDateTime == null || endDateTime == null)
        {
         throw  new InvalidPeriodException("Start and end date are required");
        }

        if(!startDateTime.isBefore(endDateTime)){

            throw new InvalidPeriodException("End date cannot be before start date");

        }

        //maybe a min time of 1-hour period
    }

    public void updateSchedule(LocalDateTime startDateTime, LocalDateTime endDateTime)
    {

            if(LocalDateTime.now().isAfter(this.startDateTime))
            {
                throw new InvalidPeriodException("Event already started and cannot be changed");
            }

            validateSchedule(startDateTime,endDateTime);

            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
    }

    public void initializeCapacity(Integer capacity)
    {
        validateCapacity(capacity);

        this.capacity = capacity;
        this.availableSeats = capacity;
    }

    public void updateCapacity(Integer newCapacity)
    {
        validateCapacity(newCapacity);
        int occupiedSeats = this.capacity - this.availableSeats;

        if(newCapacity < occupiedSeats)
        {
            throw new InvalidCapacityException("Capacity cannot be less than occupied seats");
        }

        this.capacity = newCapacity;
        this.availableSeats = newCapacity - occupiedSeats;
    }

}


