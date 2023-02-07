package com.energyms.energyms.service;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.energyms.energyms.dto.RoomDto;

import com.energyms.energyms.model.Room;
import com.energyms.energyms.model.User;

import com.energyms.energyms.repository.RoomRepository;
import com.energyms.energyms.repository.UserRepository;

@Service
public class RoomService {
	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private UserRepository userRepository;
	

	public RoomService(RoomRepository roomRepository) {

		this.roomRepository = roomRepository;
	}

	public Room save(RoomDto roomDto) {
		User user = userRepository.findByEmailId(roomDto.getUserEmailId())
				.orElseThrow(() -> new UsernameNotFoundException(" email not found" + roomDto.getUserEmailId()));

		Room room = new Room(roomDto.getRoomName(), user);

		return roomRepository.save(room);
		 

	}

	
	public List<String> getAllRooms(String emailId) {
		

		return roomRepository.findByUser(emailId);

	}


}
