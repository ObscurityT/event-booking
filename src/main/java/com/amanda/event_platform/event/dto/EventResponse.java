package com.amanda.event_platform.event.dto;


import com.amanda.event_platform.event.domain.EventStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponse(

        UUID id,
        String name,
        String description,
        String location,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Integer capacity,
        Integer availableSeats,
        EventStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
