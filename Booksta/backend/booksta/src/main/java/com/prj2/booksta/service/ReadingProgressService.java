package com.prj2.booksta.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.ReadingProgress;
import com.prj2.booksta.model.ReadingStatus;
import com.prj2.booksta.model.User;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.ReadingProgressRepository;
import com.prj2.booksta.repository.UserRepository;

@Service
public class ReadingProgressService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReadingProgressRepository progressRepository;

    public ReadingProgressService(UserRepository userRepository, BookRepository bookRepository, ReadingProgressRepository progressRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.progressRepository = progressRepository;
    }

    public ReadingProgress createProgress(User userDetails, String bookIsbn, Long currentPage) {
        Book book = bookRepository.findById(bookIsbn).orElseThrow();
        ReadingProgress progress = new ReadingProgress();
        progress.setUser(userDetails);
        progress.setBook(book);
        progress.initializeTotalPages(book.getPages());

        progress.setCurrentPage(currentPage);
        progress.setProgressPercent( progress.getProgressPercent());

        return progressRepository.save(progress);
    }

    public ReadingProgress updateProgress(User userDetails, String bookIsbn, Long currentPage) {

        ReadingProgress progress = progressRepository
            .findByUserIdAndBookIsbn(userDetails.getId(), bookIsbn)
            .orElseThrow(() -> new RuntimeException("Progress does not exist yet"));
        

        progress.setCurrentPage(currentPage);
        progress.setProgressPercent( progress.getProgressPercent());
        return progressRepository.save(progress);
    }


    public List<ReadingProgress> getUserProgress(User userDetails) {

        return progressRepository.findByUserId(userDetails.getId());
    }

}

