package com.prj2.booksta.service;

import com.prj2.booksta.model.Subject;
import com.prj2.booksta.model.dto.SubjectRequest;
import com.prj2.booksta.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTests {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    private Subject horror;
    private Subject fantasy;

    @BeforeEach
    void setUp() {
        horror = new Subject(1L, "Horror", new HashSet<>());
        fantasy = new Subject(2L, "Fantasy", new HashSet<>());
    }

    @Test
    void testGetAllSubjects() {
        List<Subject> mockSubjects = Arrays.asList(horror, fantasy);
        when(subjectRepository.findAll()).thenReturn(mockSubjects);

        Iterable<Subject> result = subjectService.getAllSubjects();

        assertNotNull(result);
        List<Subject> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(horror));
        assertTrue(resultList.contains(fantasy));

        verify(subjectRepository).findAll();
    }

    @Test
    void testGetSubjectById_Found() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(horror));

        Optional<Subject> result = subjectService.getSubjectById(1L);

        assertTrue(result.isPresent());
        assertEquals("Horror", result.get().getName());
        verify(subjectRepository).findById(1L);
    }

    @Test
    void testGetSubjectById_NotFound() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Subject> result = subjectService.getSubjectById(99L);

        assertFalse(result.isPresent());
        verify(subjectRepository).findById(99L);
    }

    @Test
    void testFindAllById() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Subject> repoResponse = Arrays.asList(horror, fantasy);

        when(subjectRepository.findAllById(ids)).thenReturn(repoResponse);

        Set<Subject> result = subjectService.findAllById(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(horror));
        assertTrue(result.contains(fantasy));
        assertTrue(result instanceof Set);

        verify(subjectRepository).findAllById(ids);
    }

    @Test
    void testFindAllById_Empty() {
        List<Long> ids = Collections.emptyList();
        when(subjectRepository.findAllById(ids)).thenReturn(Collections.emptyList());

        Set<Subject> result = subjectService.findAllById(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateSubject() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Sci-Fi");

        Subject savedSubject = new Subject(3L, "Sci-Fi", new HashSet<>());

        when(subjectRepository.save(any(Subject.class))).thenReturn(savedSubject);

        Subject result = subjectService.createSubject(request);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Sci-Fi", result.getName());
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void testUpdateSubject_Success() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Horror Updated");

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(horror));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subject result = subjectService.updateSubject(1L, request);

        assertEquals("Horror Updated", result.getName());
        verify(subjectRepository).findById(1L);
        verify(subjectRepository).save(horror);
    }

    @Test
    void testUpdateSubject_NotFound() {
        SubjectRequest request = new SubjectRequest();
        request.setName("New Name");

        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> subjectService.updateSubject(99L, request));
        verify(subjectRepository).findById(99L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    void testDeleteSubject_Success() {
        when(subjectRepository.existsById(1L)).thenReturn(true);
        doNothing().when(subjectRepository).deleteById(1L);

        subjectService.deleteSubject(1L);

        verify(subjectRepository).existsById(1L);
        verify(subjectRepository).deleteById(1L);
    }

    @Test
    void testDeleteSubject_NotFound() {
        when(subjectRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> subjectService.deleteSubject(99L));

        verify(subjectRepository).existsById(99L);
        verify(subjectRepository, never()).deleteById(anyLong());
    }
}