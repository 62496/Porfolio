package com.prj2.booksta.service;

import com.prj2.booksta.repository.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivilegeService {
    @Autowired
    private PrivilegeRepository privilegeRepository;
}
