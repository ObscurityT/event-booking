package com.amanda.event_platform.event.repository;

import com.amanda.event_platform.event.domain.Event;
import com.amanda.event_platform.event.domain.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByStatus(EventStatus status);

    UUID id(UUID id);
}
