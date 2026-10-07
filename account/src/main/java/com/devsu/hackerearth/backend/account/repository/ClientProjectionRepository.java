package com.devsu.hackerearth.backend.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsu.hackerearth.backend.account.model.ClientProjection;

@Repository
public interface ClientProjectionRepository extends JpaRepository<ClientProjection, Long> {
}
