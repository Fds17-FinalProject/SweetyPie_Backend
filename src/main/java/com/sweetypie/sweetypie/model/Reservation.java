package com.sweetypie.sweetypie.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    private int totalGuestNum;

    private int adultNum;

    private int childNum;

    private int infantNum;

    private int totalPrice;

    @ColumnDefault("false")
    private boolean isWrittenReview;

    @CreationTimestamp
    private LocalDate paymentDate;

    private String reservationCode;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Accommodation accommodation;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<BookedDate> bookedDates = new ArrayList<>();
}