package com.conductor.acl.poc.persistence.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "conjunto_c")
public class ConjuntoC {

  @Id
  @Column
  private Long id;

  @Column(name = "nombre")
  private String nombre;

  @ManyToOne(fetch = FetchType.EAGER)
  private ConjuntoB conjuntoB;
}