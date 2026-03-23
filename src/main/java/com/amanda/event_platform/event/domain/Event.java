package com.amanda.event_platform.event.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated
    private EventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
