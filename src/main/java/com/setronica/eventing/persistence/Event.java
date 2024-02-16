package com.setronica.eventing.persistence;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "\"events\"")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @Column
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Column
    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_id")
    private List<EventSchedule> eventSchedules = new ArrayList<>();

    // Method to set the date before persisting the entity
    @PrePersist
    public void prePersist() {
        // Set the date to the current date-time
        this.date = LocalDate.now();
    }
}