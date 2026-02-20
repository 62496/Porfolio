package com.prj2.booksta.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.BookCollection;
import com.prj2.booksta.model.CollectionVisibility;
import com.prj2.booksta.model.Image;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.BookCollectionRepository;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BookCollectionServiceTest {

    @Mock
    private BookCollectionRepository collectionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ImageService imageService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private BookCollectionService collectionService;

    private User currentUser;
    private User otherUser;
    private Book book;
    private BookCollection myCollection;
    private BookCollection otherCollection;
    
    private final String USER_EMAIL = "me@test.com";

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail(USER_EMAIL);
        currentUser.setOwnedBooks(new HashSet<>());

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");

        book = new Book();
        book.setIsbn("12345");
        book.setTitle("Test Book");

        myCollection = new BookCollection();
        myCollection.setId(10L);
        myCollection.setName("My Favorites");
        myCollection.setOwner(currentUser);
        myCollection.setSharedWith(new HashSet<>());
        myCollection.setBooks(new HashSet<>());
        myCollection.setVisibility(CollectionVisibility.PRIVATE);

        otherCollection = new BookCollection();
        otherCollection.setId(20L);
        otherCollection.setName("Other's List");
        otherCollection.setOwner(otherUser);
        otherCollection.setSharedWith(new HashSet<>());
        otherCollection.setBooks(new HashSet<>());
        otherCollection.setVisibility(CollectionVisibility.PUBLIC);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn(USER_EMAIL);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(currentUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAuthenticatedUser_NotFound() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> collectionService.getAllOwnCollections());
    }

    @Test
    void testCreateCollection_NoImage() {
        when(collectionRepository.save(any(BookCollection.class))).thenReturn(myCollection);

        BookCollection newCol = new BookCollection();
        newCol.setName("New List");

        BookCollection result = collectionService.createCollection(newCol, null);

        assertNotNull(result);
        assertEquals(currentUser, newCol.getOwner());
        assertEquals(CollectionVisibility.PRIVATE, newCol.getVisibility()); 
        verify(collectionRepository).save(newCol);
        verifyNoInteractions(fileStorageService, imageService);
    }

    @Test
    void testCreateCollection_WithImage() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        
        when(collectionRepository.save(any(BookCollection.class))).thenReturn(myCollection);
        when(fileStorageService.saveCollectionImage(mockFile, 10L)).thenReturn("path/img.jpg");
        when(imageService.createImage(any(Image.class))).thenReturn(new Image("path/img.jpg"));
        
        myCollection.setImage(new Image("path/img.jpg"));
        when(collectionRepository.save(myCollection)).thenReturn(myCollection);

        BookCollection result = collectionService.createCollection(myCollection, mockFile);

        assertNotNull(result.getImage());
        assertEquals("path/img.jpg", result.getImage().getUrl());
        verify(fileStorageService).saveCollectionImage(mockFile, 10L);
        verify(collectionRepository, times(2)).save(any(BookCollection.class));
    }

    @Test
    void testCreateCollection_ImageFailure() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(collectionRepository.save(any(BookCollection.class))).thenReturn(myCollection);
        when(fileStorageService.saveCollectionImage(mockFile, 10L)).thenThrow(new IOException("Error"));

        assertThrows(RuntimeException.class, () -> collectionService.createCollection(myCollection, mockFile));
    }

    @Test
    void testUpdateCollection_Success() {
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(collectionRepository.save(any(BookCollection.class))).thenReturn(myCollection);

        BookCollection updates = new BookCollection();
        updates.setName("Updated Name");
        updates.setDescription("New Desc");
        updates.setVisibility(CollectionVisibility.PUBLIC);

        BookCollection result = collectionService.updateCollection(10L, updates, null);

        assertEquals("Updated Name", result.getName());
        assertEquals("New Desc", result.getDescription());
        assertEquals(CollectionVisibility.PUBLIC, result.getVisibility());
    }

    @Test
    void testUpdateCollection_WithNewImage_NoExistingImage() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(collectionRepository.save(any(BookCollection.class))).thenReturn(myCollection);
        when(fileStorageService.saveCollectionImage(mockFile, 10L)).thenReturn("new/img.jpg");
        when(imageService.createImage(any(Image.class))).thenReturn(new Image("new/img.jpg"));

        collectionService.updateCollection(10L, new BookCollection(), mockFile);

        assertNotNull(myCollection.getImage());
        assertEquals("new/img.jpg", myCollection.getImage().getUrl());
    }

    @Test
    void testUpdateCollection_WithNewImage_ExistingImage() throws IOException {
        Image existingImage = new Image("old.jpg");
        myCollection.setImage(existingImage);
        
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(collectionRepository.save(any(BookCollection.class))).thenReturn(myCollection);
        when(fileStorageService.saveCollectionImage(mockFile, 10L)).thenReturn("updated.jpg");

        collectionService.updateCollection(10L, new BookCollection(), mockFile);

        assertEquals("updated.jpg", myCollection.getImage().getUrl());
        verify(imageService, never()).createImage(any()); 
    }

    @Test
    void testUpdateCollection_ImageFailure() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(fileStorageService.saveCollectionImage(mockFile, 10L)).thenThrow(new IOException());

        assertThrows(RuntimeException.class, () -> collectionService.updateCollection(10L, new BookCollection(), mockFile));
    }

    @Test
    void testUpdateCollection_NotOwner() {
        when(collectionRepository.findById(20L)).thenReturn(Optional.of(otherCollection));
        assertThrows(AccessDeniedException.class, () -> 
            collectionService.updateCollection(20L, new BookCollection(), null)
        );
    }

    @Test
    void testDeleteCollection_Success() throws IOException {
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        collectionService.deleteCollection(10L);
        verify(fileStorageService).deleteCollectionImage(10L);
        verify(collectionRepository).deleteById(10L);
    }

    @Test
    void testDeleteCollection_ImageDeletionFailure_ShouldNotThrow() throws IOException {
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        doThrow(new IOException()).when(fileStorageService).deleteCollectionImage(10L);

        collectionService.deleteCollection(10L);
        
        verify(collectionRepository).deleteById(10L);
    }

    @Test
    void testDeleteCollection_NotOwner() {
        when(collectionRepository.findById(20L)).thenReturn(Optional.of(otherCollection));
        assertThrows(AccessDeniedException.class, () -> collectionService.deleteCollection(20L));
    }

    @Test
    void testGetAllOwnCollections() {
        when(collectionRepository.findByOwnerId(1L)).thenReturn(Collections.singletonList(myCollection));
        List<BookCollection> result = collectionService.getAllOwnCollections();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllCollectionsAllowed() {
        when(collectionRepository.findAccessibleByUser(1L)).thenReturn(Collections.singletonList(myCollection));
        List<BookCollection> result = collectionService.getAllCollectionsAllowed();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllPublicCollections() {
        when(collectionRepository.findAllPublic()).thenReturn(Collections.singletonList(otherCollection));
        List<BookCollection> result = collectionService.getAllPublicCollections();
        assertEquals(1, result.size());
    }

    @Test
    void testGetSharedWithMe() {
        when(collectionRepository.findSharedWithUser(1L)).thenReturn(Collections.emptyList());
        List<BookCollection> result = collectionService.getSharedWithMe();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCollectionIfAllowed_Allowed() {
        when(collectionRepository.canUserAccess(10L, 1L)).thenReturn(true);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        Optional<BookCollection> result = collectionService.getCollectionIfAllowed(10L);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetCollectionIfAllowed_Denied() {
        when(collectionRepository.canUserAccess(20L, 1L)).thenReturn(false);
        Optional<BookCollection> result = collectionService.getCollectionIfAllowed(20L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testShareWith_Success() {
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));
        when(collectionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        collectionService.shareWith(10L, "other@test.com");
        assertTrue(myCollection.getSharedWith().contains(otherUser));
    }

    @Test
    void testShareWith_UserNotFound() {
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> collectionService.shareWith(10L, "unknown@test.com"));
    }

    @Test
    void testUnshareWith_Success() {
        myCollection.getSharedWith().add(otherUser);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(collectionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        collectionService.unshareWith(10L, 2L);
        assertFalse(myCollection.getSharedWith().contains(otherUser));
    }

    @Test
    void testAddBook_Success() {
        currentUser.getOwnedBooks().add(book);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(bookRepository.findById("12345")).thenReturn(Optional.of(book));
        when(collectionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        collectionService.addBook(10L, "12345");
        assertTrue(myCollection.getBooks().contains(book));
    }

    @Test
    void testAddBook_BookNotFound() {
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(bookRepository.findById("12345")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> collectionService.addBook(10L, "12345"));
    }

    @Test
    void testAddBook_NotOwnedByCollectionOwner() {
        currentUser.getOwnedBooks().clear();
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(bookRepository.findById("12345")).thenReturn(Optional.of(book));

        assertThrows(AccessDeniedException.class, () -> collectionService.addBook(10L, "12345"));
    }

    @Test
    void testRemoveBook_Success() {
        myCollection.getBooks().add(book);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        when(collectionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        collectionService.removeBook(10L, "12345");
        assertFalse(myCollection.getBooks().contains(book));
    }

    @Test
    void testCollectionContainsBook_True() {
        myCollection.getBooks().add(book);
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        assertTrue(collectionService.collectionContainsBook(10L, "12345"));
    }

    @Test
    void testCollectionContainsBook_False() {
        when(collectionRepository.findById(10L)).thenReturn(Optional.of(myCollection));
        assertFalse(collectionService.collectionContainsBook(10L, "12345"));
    }
    
    @Test
    void testCollectionContainsBook_CollectionNotFound() {
        when(collectionRepository.findById(99L)).thenReturn(Optional.empty());
        assertFalse(collectionService.collectionContainsBook(99L, "12345"));
    }

    @Test
    void testCanAccess() {
        when(collectionRepository.canUserAccess(10L, 1L)).thenReturn(true);
        assertTrue(collectionService.canAccess(10L));
    }
}