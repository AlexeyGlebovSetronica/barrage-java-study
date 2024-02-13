package com.setronica.eventing.app;

import com.setronica.eventing.exceptions.NotFoundException;
import com.setronica.eventing.persistence.Event;
import com.setronica.eventing.persistence.EventRepository;
import org.springframework.scheduling.annotation.Async;
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

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(int id) {
        eventRepository.deleteById(id);
    }

    @Async("appThreadPool")
    public void asyncOperation() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("Done.");
    }
}
