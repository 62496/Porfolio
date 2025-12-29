package com.prj2.booksta.repository;

import com.prj2.booksta.model.BookCollection;
import com.prj2.booksta.model.CollectionVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookCollectionRepository extends JpaRepository<BookCollection, Long> {

    List<BookCollection> findByOwnerId(Long userId);

    List<BookCollection> findByVisibility(CollectionVisibility visibility);

    @Query("SELECT bc FROM BookCollection bc WHERE bc.visibility = 'PUBLIC'")
    List<BookCollection> findAllPublic();

    @Query("SELECT bc FROM BookCollection bc JOIN bc.sharedWith u WHERE u.id = :userId")
    List<BookCollection> findSharedWithUser(@Param("userId") Long userId);

    @Query("SELECT bc FROM BookCollection bc WHERE bc.visibility = 'PUBLIC' " +
           "OR bc.owner.id = :userId " +
           "OR :userId IN (SELECT u.id FROM bc.sharedWith u)")
    List<BookCollection> findAccessibleByUser(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(bc) > 0 THEN true ELSE false END FROM BookCollection bc " +
           "WHERE bc.id = :collectionId AND " +
           "(bc.visibility = 'PUBLIC' OR bc.owner.id = :userId OR :userId IN (SELECT u.id FROM bc.sharedWith u))")
    boolean canUserAccess(@Param("collectionId") Long collectionId, @Param("userId") Long userId);

    @Query("SELECT bc FROM BookCollection bc JOIN bc.books b WHERE b.isbn = :isbn")
    List<BookCollection> findByBooksIsbn(@Param("isbn") String isbn);
}
