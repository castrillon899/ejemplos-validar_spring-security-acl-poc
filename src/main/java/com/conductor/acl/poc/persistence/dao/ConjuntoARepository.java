package com.conductor.acl.poc.persistence.dao;

import com.conductor.acl.poc.persistence.entity.ConjuntoA;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.acls.model.Sid;

public interface ConjuntoARepository extends JpaRepository<ConjuntoA, Long> {



    @Query(value = "SELECT c.* FROM conjunto_a c " +
        "JOIN acl_object_identity acl ON c.id = acl.object_id_identity " +
        "JOIN acl_entry entry ON acl.id = entry.acl_object_identity " +
        "JOIN acl_sid sid ON entry.sid = sid.id " +
        "WHERE sid.sid = :user AND entry.mask >= :readPermission",
        countQuery = "SELECT count(1) FROM conjunto_a c " +
            "JOIN acl_object_identity acl ON c.id = acl.object_id_identity " +
            "JOIN acl_entry entry ON acl.id = entry.acl_object_identity " +
            "JOIN acl_sid sid ON entry.sid = sid.id " +
            "WHERE sid.sid = :user AND entry.mask >= :readPermission",
        nativeQuery = true)
    Page<ConjuntoA> findAllWithReadPermissionNative(@Param("user") String user,
                                                    @Param("readPermission") int readPermission,
                                                    Pageable pageable);


    List<ConjuntoA> findAll();

    Optional<ConjuntoA> findById(Long id);

}
