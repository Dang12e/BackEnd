package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testBackendDatabase.demo.model.ShowRoom;

import java.util.List;

public interface ShowRoomRepository extends JpaRepository<ShowRoom,Long> {
        List<ShowRoom> findByCinema_Id(Long cinemaId);
}
