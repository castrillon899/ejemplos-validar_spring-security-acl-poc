package com.conductor.acl.poc.web;

import com.conductor.acl.poc.persistence.entity.ConjuntoA;
import com.conductor.acl.poc.service.ServiceConjuntoA;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class ServiceConjuntoAController {

    private final ServiceConjuntoA serviceConjuntoA;

    public ServiceConjuntoAController(ServiceConjuntoA serviceConjuntoA
    ) {
        this.serviceConjuntoA = serviceConjuntoA;
    }

    @GetMapping(value = "/conjuntoA", produces = "application/json")
    public List<ConjuntoA> findAll() {
        return serviceConjuntoA.findAll();
    }

    @GetMapping(value = "/conjuntoA/{id}", produces = "application/json")
    public ConjuntoA findById(@PathVariable Long id) {
        return serviceConjuntoA.findById(id);
    }

    @PostMapping(value = "/conjuntoA", produces = "application/json")
    public ConjuntoA save(@RequestBody ConjuntoA conjuntoA) {
        return serviceConjuntoA.save(conjuntoA);
    }

    @PutMapping(value = "/conjuntoA/{id}", produces = "application/json")
    public ConjuntoA update(@PathVariable Long id, @RequestBody ConjuntoA conjuntoA) {
        return serviceConjuntoA.update(id, conjuntoA);
    }

    @GetMapping(value = "/conjuntoA/findAllWithReadPermission", produces = "application/json")
    public Page<ConjuntoA> findAllWithReadPermission(@RequestParam int page,
                                                     @RequestParam int size) {
        return serviceConjuntoA.findAllWithReadPermission(page, size);
    }
}
