package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.prj2.booksta.model.Image;
import com.prj2.booksta.repository.ImageRepository;

class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    private Image image;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        image = new Image();
        image.setId(1L);
        image.setUrl("http://example.com/test-image.png");
    }

    @Test
    void testCreateImage() {
        when(imageRepository.save(image)).thenReturn(image);

        Image result = imageService.createImage(image);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("http://example.com/test-image.png", result.getUrl());
        verify(imageRepository, times(1)).save(image);
    }
}