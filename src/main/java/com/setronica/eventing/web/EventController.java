package com.setronica.eventing.web;

import com.setronica.eventing.app.EventService;
import com.setronica.eventing.dto.EventDto;
import com.setronica.eventing.dto.EventUpdate;
import com.setronica.eventing.persistence.Event;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("event/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping()
    public List<Event> getAll(){
        return eventService.getAll();
    }

    @GetMapping("/{id}")
    public Event getById(@PathVariable Integer id) {
        return eventService.getById(id);
    }

    @PostMapping("")
    public Event createEvent(@Valid @RequestBody Event event) {
        return eventService.create(event);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable int id, @RequestBody EventUpdate newEvent) {
        Event existingEvent = eventService.getById(id);
        return eventService.update(newEvent, existingEvent);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Integer id) {
        Event existingEvent = eventService.getById(id);
        eventService.delete(existingEvent.getId());
    }
}
