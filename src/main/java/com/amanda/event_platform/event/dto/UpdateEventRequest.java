package com.amanda.event_platform.event.dto;

import com.amanda.event_platform.event.domain.EventStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateEventRequest(
        @NotBlank
        String name,

        String description,

        @NotBlank
        String location,

        @NotNull
        @Future
        LocalDateTime startDateTime,

        @NotNull
        @Future
        LocalDateTime endDateTime,

        @NotNull
        @Min(1)
        Integer capacity) {
}
