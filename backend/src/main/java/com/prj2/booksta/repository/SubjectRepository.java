package com.prj2.booksta.repository;

import com.prj2.booksta.model.Subject;
import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<Subject, Long> {
}
