package com.conductor.acl.poc.persistence.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RolService {
    public String obtenerRolesDinamicos(Long objectId) {
        // Construir el rol dinámico

        System.out.println("objectId: " + objectId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String rolDinamico = "ROLE_ADMIN_" + objectId;
        // Devolver el rol dinámico
        return rolDinamico;
    }
}
