package com.mip.sharebnb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "is_Deleted = false")
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
    private String name;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private LocalDate birthDate;

    @ColumnDefault("false")
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @ColumnDefault("false")
    private boolean isSocialMember;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Review> reviews;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Bookmark> bookmarks;
}
