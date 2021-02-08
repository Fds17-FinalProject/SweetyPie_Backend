package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Builder
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

    private String address;

    private String title;

    @ColumnDefault("0")
    private int bathroomNum;

    @ColumnDefault("0")
    private int bedroomNum;

    @ColumnDefault("0")
    private int bedNum;

    @ColumnDefault("0")
    private int price;

    @ColumnDefault("0")
    private int capacity;

    private String contact;

    @Column(columnDefinition = "DECIMAL(11,7)")
    private float latitude;

    @Column(columnDefinition = "DECIMAL(11,7)")
    private float longitude;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String locationDesc;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String transportationDesc;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String accommodationDesc;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String hostDesc;

    @Column(columnDefinition = "DECIMAL(5,3)")
    private float rating;

    @ColumnDefault("0")
    private int reviewNum;

    private String accommodationType;

    private String buildingType;

    private String hostName;

    @JsonIgnore
    @ColumnDefault("0")
    private int hostReviewNum;
    
    @JsonIgnore
    @OneToMany(mappedBy = "accommodation")
    private List<Reservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "accommodation")
    private List<Review> reviews;

    @JsonIgnore
    @OneToMany(mappedBy = "accommodation")
    private List<BookedDate> bookedDates;

    @JsonIgnore
    @OneToMany(mappedBy = "accommodation")
    private List<AccommodationPicture> accommodationPictures;
}