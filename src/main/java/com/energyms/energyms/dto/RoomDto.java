package com.energyms.energyms.dto;



public class RoomDto {
	private String roomName;

	private String userEmailId;
	

	public String getUserEmailId() {
		return userEmailId;
	}

	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}


	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public RoomDto(String roomName, String userEmailId) {
		super();
		this.roomName = roomName;

		this.userEmailId = userEmailId;
	}

}
