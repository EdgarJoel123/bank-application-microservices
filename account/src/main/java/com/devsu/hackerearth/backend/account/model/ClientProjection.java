package com.devsu.hackerearth.backend.account.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client_projection")
public class ClientProjection {

    @Id
    private Long id;

    private String name;
    private boolean isActive;
}
