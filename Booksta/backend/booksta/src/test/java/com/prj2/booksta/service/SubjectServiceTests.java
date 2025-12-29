package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.prj2.booksta.model.Subject;
import com.prj2.booksta.repository.SubjectRepository;

@SpringBootTest
class SubjectServiceTests {

	@Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSubjects() {

		List<Subject> mockSubjects = Arrays.asList(

                new Subject(1L, "Horror",new HashSet<>()),
                new Subject(2L, "Fantasy",new HashSet<>())
        );
        when(subjectRepository.findAll()).thenReturn(mockSubjects);

        Iterable<Subject> result = subjectService.getAllSubjects();

        assertNotNull(result);
        assertEquals(2, ((List<Subject>) result).size());
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void testGetSubjectById_Found() {

		Subject mockSubject = new Subject(1L, "History",new HashSet<>());
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mockSubject));

		Optional<Subject> result = subjectService.getSubjectById(1L);

        assertTrue(result.isPresent());
        assertEquals("History", result.get().getName());
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSubjectById_NotFound() {

		when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

		Optional<Subject> result = subjectService.getSubjectById(99L);

        assertFalse(result.isPresent());
        verify(subjectRepository, times(1)).findById(99L);
    }

}
