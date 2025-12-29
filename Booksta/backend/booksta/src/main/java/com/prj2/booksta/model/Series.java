package com.prj2.booksta.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "series")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"books", "followers"})
@ToString(exclude = {"books", "followers"})
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @JsonIgnore
    private Author author;

    @OneToMany(mappedBy = "series")
    @JsonIgnore
    private Set<Book> books = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "followedSeries")
    @JsonIgnore
    private Set<User> followers = new HashSet<>();
}
