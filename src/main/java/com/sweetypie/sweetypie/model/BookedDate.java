package com.sweetypie.sweetypie.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class BookedDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private LocalDate date;

    @NonNull
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Accommodation accommodation;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        reservation.getBookedDates().add(this);
    }

    public static BookedDate emptyObject() {
        return new BookedDate();
    }
}