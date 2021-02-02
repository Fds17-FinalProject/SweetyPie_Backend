package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ACCOMMODATION_ID")
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String gu;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int bathroomNum;

    @Column(nullable = false)
    private int bedroomNum;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String contact;

    private float latitude;

    private float longitude;

    @Column(columnDefinition = "TEXT")
    private String locationDesc;

    @Column(columnDefinition = "TEXT")
    private String transportationDesc;

    @Column(columnDefinition = "TEXT")
    private String accommodationDesc;

    @Column(columnDefinition = "TEXT")
    private String hostDesc;

    private float rating;

    private int reviewNum;

    @Column(nullable = false)
    private String accommodationType;

    @Column(nullable = false)
    private String buildingType;

    private String hostName;

    private float hostRating;

    private int hostReviewNum;


    @JsonBackReference
    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private List<Bookmark> bookmarks;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOMMODATION_ID")
    private List<BookedDate> bookedDates;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOMMODATION_ID")
    private List<AccommodationPicture> accommodationPictures;
}