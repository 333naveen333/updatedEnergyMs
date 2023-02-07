package com.energyms.energyms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.energyms.energyms.model.Room;



@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {

	
	
	Room findByRoomNameAndUserEmailId(String roomName, String emailId );
    Room findByRoomName(String roomName);
	
	
	
	 @Query("select distinct t.roomName from Room t where t.user.emailId= :emailId")
     List<String> findByUser(@Param("emailId")String emailId);
	 
	 List<Room> findByUserEmailId(String emailId);
  
}
