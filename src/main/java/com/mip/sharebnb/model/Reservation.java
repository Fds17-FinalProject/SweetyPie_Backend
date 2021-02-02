package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_Canceled = false")
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

//    @JsonManagedReference
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @JsonManagedReference
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ACCOMMODATION_ID")
    private Accommodation accommodation;
}