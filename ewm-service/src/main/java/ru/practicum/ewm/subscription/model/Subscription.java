package ru.practicum.ewm.subscription.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private User person;

}
