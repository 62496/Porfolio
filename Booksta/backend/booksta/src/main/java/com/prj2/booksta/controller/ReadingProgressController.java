package com.prj2.booksta.controller;

import java.util.List;

import com.prj2.booksta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj2.booksta.model.ReadingProgress;
import com.prj2.booksta.model.User;
import com.prj2.booksta.service.ReadingProgressService;


@RestController
@RequestMapping("/api/progress")
@CrossOrigin("*")
public class ReadingProgressController {
    @Autowired
    private ReadingProgressService progressService;

    @Autowired
    private UserService userService;

    @PostMapping("/update/{bookIsbn}")
    public ResponseEntity<ReadingProgress> updateProgress(
            @PathVariable String bookIsbn,
            @RequestParam Long currentPage,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        ReadingProgress progress = progressService.updateProgress(user, bookIsbn, currentPage);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/create/{bookIsbn}")
    public ResponseEntity<ReadingProgress> createProgress(
            @PathVariable String bookIsbn,
            @RequestParam Long currentPage,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        ReadingProgress progress = progressService.createProgress(user, bookIsbn, currentPage);
        return ResponseEntity.ok(progress);
    }

    @GetMapping()
    public ResponseEntity<List<ReadingProgress>> getUserProgress(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(progressService.getUserProgress(user));
    }
}
