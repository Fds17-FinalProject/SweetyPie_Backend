package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "isCanceled = false")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private int guestNum;

    @Column(nullable = false)
    private int totalPrice;

    private boolean isCanceled;

    @CreationTimestamp
    private LocalDate paymentDate; // 결제일

    private String reservationCode; // 우리가 만들어 줘야 함.

    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ACCOMMODATION_ID")
    private Accommodation accommodation;
}