package com.setronica.eventing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Integer> {
    List<EventSchedule> findByEventId(Integer eventId);
}