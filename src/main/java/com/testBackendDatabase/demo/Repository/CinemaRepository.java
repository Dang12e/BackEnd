package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testBackendDatabase.demo.model.Cinema;

public interface CinemaRepository extends JpaRepository<Cinema,Long> {
    boolean existsByNameAndAddress(String name, String address);
}
