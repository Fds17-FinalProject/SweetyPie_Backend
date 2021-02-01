package com.mip.sharebnb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private float rating;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDate createdDate;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ACCOMMODATION_ID")
    private Accommodation accommodation;
}