package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Where(clause = "isDeleted = false")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private LocalDate birthDate;

    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private Long socialId;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @JsonManagedReference
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Bookmark> bookmarks;
}
