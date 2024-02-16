package com.setronica.eventing.app;

import com.setronica.eventing.dto.EventUpdate;
import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.Event;
import com.setronica.eventing.persistence.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event getById(Integer id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found with id=" + id));
    }

    public Event create(Event event) {
        return eventRepository.save(event);
    }

    public Event update(EventUpdate eventUpdate, Event existingEvent) {
        existingEvent.setTitle(eventUpdate.getTitle());
        existingEvent.setDescription(eventUpdate.getDescription());
        existingEvent.setDate(eventUpdate.getDate());
        return eventRepository.save(existingEvent);
    }

    public void delete(int id) {
        eventRepository.deleteById(id);
    }
}
