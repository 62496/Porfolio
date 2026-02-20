package com.prj2.booksta.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj2.booksta.model.Subject;
import com.prj2.booksta.model.dto.SubjectRequest;
import com.prj2.booksta.repository.SubjectRepository;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    public Iterable<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public @NotNull Set<Subject> findAllById(List<Long> subjects) {
        Iterable<Subject> iterable = subjectRepository.findAllById(subjects);
        Set<Subject> set = new HashSet<>();

        iterable.forEach(set::add);
        return set;
    }

    @Transactional
    public Subject createSubject(SubjectRequest request) {
        Subject subject = new Subject();
        subject.setName(request.getName());
        return subjectRepository.save(subject);
    }

    @Transactional
    public Subject updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
        subject.setName(request.getName());
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }
}
