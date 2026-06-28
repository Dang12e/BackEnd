package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testBackendDatabase.demo.model.ShowRoom;

public interface ShowRoomRepository extends JpaRepository<ShowRoom,Long> {
    
}
