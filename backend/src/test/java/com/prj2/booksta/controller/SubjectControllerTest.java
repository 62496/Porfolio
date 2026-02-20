package com.prj2.booksta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj2.booksta.model.Subject;
import com.prj2.booksta.model.dto.SubjectRequest;
import com.prj2.booksta.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private SubjectController subjectController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subjectController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllSubjects_returnsList() throws Exception {
        Subject subject1 = new Subject();
        subject1.setId(1L);
        subject1.setName("Fantasy");

        Subject subject2 = new Subject();
        subject2.setId(2L);
        subject2.setName("Science Fiction");

        List<Subject> subjects = Arrays.asList(subject1, subject2);

        when(subjectService.getAllSubjects()).thenReturn(subjects);

        mockMvc.perform(get("/api/subjects")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Fantasy"))
                .andExpect(jsonPath("$[1].name").value("Science Fiction"));

        verify(subjectService).getAllSubjects();
    }

    @Test
    void testGetAllSubjects_emptyList() throws Exception {
        when(subjectService.getAllSubjects()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/subjects")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(subjectService).getAllSubjects();
    }

    @Test
    void testGetSubjectById_Found() throws Exception {
        Subject subject = new Subject();
        subject.setId(1L);
        subject.setName("History");

        when(subjectService.getSubjectById(1L)).thenReturn(Optional.of(subject));

        mockMvc.perform(get("/api/subjects/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("History")));

        verify(subjectService).getSubjectById(1L);
    }

    @Test
    void testGetSubjectById_NotFound() throws Exception {
        when(subjectService.getSubjectById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/subjects/{id}", 99L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(subjectService).getSubjectById(99L);
    }

    @Test
    void testCreateSubject() throws Exception {
        SubjectRequest request = new SubjectRequest();
        request.setName("Thriller");

        Subject createdSubject = new Subject();
        createdSubject.setId(5L);
        createdSubject.setName("Thriller");

        when(subjectService.createSubject(any(SubjectRequest.class))).thenReturn(createdSubject);

        mockMvc.perform(post("/api/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("Thriller")));

        verify(subjectService).createSubject(any(SubjectRequest.class));
    }

    @Test
    void testUpdateSubject() throws Exception {
        SubjectRequest request = new SubjectRequest();
        request.setName("Updated Fantasy");

        Subject updatedSubject = new Subject();
        updatedSubject.setId(1L);
        updatedSubject.setName("Updated Fantasy");

        when(subjectService.updateSubject(eq(1L), any(SubjectRequest.class))).thenReturn(updatedSubject);

        mockMvc.perform(put("/api/subjects/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Fantasy")));

        verify(subjectService).updateSubject(eq(1L), any(SubjectRequest.class));
    }

    @Test
    void testDeleteSubject() throws Exception {
        doNothing().when(subjectService).deleteSubject(1L);

        mockMvc.perform(delete("/api/subjects/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(subjectService).deleteSubject(1L);
    }
}