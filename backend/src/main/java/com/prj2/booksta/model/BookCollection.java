package com.prj2.booksta.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"books", "sharedWith"})
@ToString(exclude = {"books", "sharedWith"})
public class BookCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectionVisibility visibility = CollectionVisibility.PRIVATE;

    @ManyToMany
    @JoinTable(
            name = "book_collection_books",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "book_isbn")
    )
    private Set<Book> books = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "book_collection_shared_users",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> sharedWith = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;
}
