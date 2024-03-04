package com.conductor.acl.poc.service;

import com.conductor.acl.poc.persistence.dao.ConjuntoARepository;
import com.conductor.acl.poc.persistence.entity.ConjuntoA;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class ServiceConjuntoA {


    /*
        READ (LECTURA): Permiso para leer el objeto.
        Valor de máscara: 1


        WRITE (ESCRITURA): Permiso para modificar el objeto.
        Valor de máscara: 2

        CREATE (CREAR): Permiso para crear nuevos objetos.
        Valor de máscara: 4

        DELETE (ELIMINAR): Permiso para eliminar el objeto.
        Valor de máscara: 8

        ADMINISTRATION (ADMINISTRACIÓN): Permiso de administración que otorga todos los permisos.
        Valor de máscara: 16
     */


    private final MutableAclService aclService;

    private ConjuntoARepository conjuntoARepository;

    public ServiceConjuntoA(MutableAclService aclService, ConjuntoARepository conjuntoARepository) {
        this.aclService = aclService;
        this.conjuntoARepository = conjuntoARepository;
    }

    // @PostFilter("hasPermission(#id, 'com.conductor.acl.poc.persistence.entity.ConjuntoA', 'WRITE')")
    //validame este metodo no esta devolviendo los objetos asicioados al user
    //y si no tiene permisos??


    // @PostFilter("hasPermission(returnObject, 'WRITE')")
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<ConjuntoA> findAll() {

        return conjuntoARepository.findAll();
    }

    //  @PostAuthorize("hasPermission(returnObject, 'READ')")
    @PreAuthorize("hasPermission(#id, 'com.conductor.acl.poc.persistence.entity.ConjuntoA', 'READ')")
    public ConjuntoA findById(Long id) {
        return conjuntoARepository.findById(id).orElseThrow(() -> new NotFoundException("No se encontro el conjunto"));

    }


    @PreAuthorize("@rolService.obtenerRolesDinamicos(#id) == 'ROLE_ADMIN_' + #id")
    public ConjuntoA update(Long id, ConjuntoA conjuntoA) {
        ConjuntoA conjuntoForUpdate = conjuntoARepository.findById(id).orElse(null);
        conjuntoForUpdate.setNombre(conjuntoA.getNombre());
        conjuntoForUpdate.setDescripcion(conjuntoA.getDescripcion());
        return conjuntoARepository.save(conjuntoForUpdate);
    }

    public ConjuntoA save(ConjuntoA conjuntoA) {
        conjuntoA.setId(IdGenerator.generateUniqueId());
        ConjuntoA response = conjuntoARepository.save(conjuntoA);
        updateAcl(response);
        return response;
    }

    public Page<ConjuntoA> findAllWithReadPermission(
        int page,
        int size) {
        String username = getUserName();
        final int readPermission = 1;
        Pageable pageable = PageRequest.of(page, size);
        return conjuntoARepository.findAllWithReadPermissionNative(username, readPermission, pageable);
    }

    private Authentication getUserContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getUserName() {
        return getUserContext().getName();
    }


    private Collection<? extends GrantedAuthority> getAuthorities() {
        return getUserContext().getAuthorities();
    }

    private MutableAcl updateAcl(ConjuntoA change) {

        final ObjectIdentity oi = new ObjectIdentityImpl(change.getClass(), change.getId());
        MutableAcl acl = null;
        try {
            acl = (MutableAcl) aclService.readAclById(oi);
        } catch (final NotFoundException nfe) {
            acl = aclService.createAcl(oi);
        }


        // Crear una nueva entrada de control de acceso (ACE)
        Sid recipient = new PrincipalSid(getUserContext());
        for (GrantedAuthority authority : getAuthorities()) {
            acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, new GrantedAuthoritySid(authority), true);
        }

        
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, recipient, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, recipient, true);
        acl.setEntriesInheriting(true);
        aclService.updateAcl(acl);
        return acl;
    }

}
