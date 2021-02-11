package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Accommodation accommodation;

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
        accommodation.getBookmarks().add(this);
    }
}