package com.prj2.booksta.repository;


import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    Optional<Author> findByUser(User user);

    Author findByUser_Id(Long userId);
}
