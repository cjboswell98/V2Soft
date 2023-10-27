package com.product.rating.services;

import com.product.rating.domain.Role;
import com.product.rating.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role createNewRole(Role role) {
       return roleRepository.save(role);
    }
}
