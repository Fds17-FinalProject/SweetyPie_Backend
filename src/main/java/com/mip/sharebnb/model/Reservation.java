package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_Canceled = false")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    private int guestNum;

    private int totalPrice;

    private boolean isCanceled;

    @ColumnDefault(value = "false")
    private Boolean isWrittenReview;

    @CreationTimestamp
    private LocalDate paymentDate; // 결제일

    private String reservationCode; // 우리가 만들어 줘야 함.

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOMMODATION_ID")
    private Accommodation accommodation;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<BookedDate> bookedDates = new ArrayList<>();
}