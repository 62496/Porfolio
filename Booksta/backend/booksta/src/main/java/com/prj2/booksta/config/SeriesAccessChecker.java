package com.prj2.booksta.config;

import com.prj2.booksta.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("seriesAccessChecker")
public class SeriesAccessChecker {

    @Autowired
    private SeriesService seriesService;

    public boolean isAuthorOfSeries(Authentication authentication, Long seriesId) {
        if (authentication == null || seriesId == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return seriesService.isAuthorOfSeries(userDetails.getUsername(), seriesId);
        }

        return false;
    }
}
