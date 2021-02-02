package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOMMODATION_ID")
    private Long id;

    private String city;

    private String gu;

    private String title;

    private Integer bathroomNum;

    private Integer bedroomNum;

    private Integer bedNum;

    private Integer price;

    private Integer capacity;

    private String contact;

    private String latitude;

    private String longitude;

    @Column(columnDefinition = "TEXT")
    private String locationDesc;

    @Column(columnDefinition = "TEXT")
    private String transportationDesc;

    @Column(columnDefinition = "TEXT")
    private String accommodationDesc;

    @Column(columnDefinition = "TEXT")
    private String hostDesc;

    private String rating;

    private Integer reviewNum;

    private String accommodationType;

    private String buildingType;

    private String hostName;

    private Integer hostReviewNum;

    @JsonBackReference
    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @JsonIgnore
    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY)
    private List<Bookmark> bookmarks;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOMMODATION_ID")
    private List<BookedDate> bookedDates;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOMMODATION_ID")
    private List<AccommodationPicture> accommodationPictures;

    public static Accommodation emptyObject() {
        return new Accommodation();
    }
}