package com.prj2.booksta.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"authors", "subjects", "series"})
@ToString(exclude = {"authors", "subjects", "series"})
public class Book {
    @Id
    @Column(name = "isbn")
    private String isbn;

    @NotNull
    private String title;

    @Column(name = "publishing_year")
    @NotNull
    private Integer publishingYear;

    @NotNull
    @Column(length = 5000)
    private String description;

    @ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_isbn"),
        inverseJoinColumns = @JoinColumn(name = "authors_id")
    )
    @NotNull
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "book_subjects",
        joinColumns = @JoinColumn(name = "book_isbn"),
        inverseJoinColumns = @JoinColumn(name = "subjects_id")
    )
    @NotNull
    private Set<Subject> subjects = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    @JsonIgnore
    private Series series;

    @OneToOne
    private Image image;

    private Long pages;
}
