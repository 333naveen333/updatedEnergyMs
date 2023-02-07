package com.energyms.energyms.dto;

public class ApplianceDto {
	 private String applianceName;
	 private String userEmailId;
	 private String roomName;
	 private long roomId;
	 private String deviceId;
	 private boolean applianceStatus;
	 private String applianceStatusChangingTime;
	 
	public String getApplianceStatusChangingTime() {
		return applianceStatusChangingTime;
	}

	public void setApplianceStatusChangingTime(String applianceStatusChangingTime) {
		this.applianceStatusChangingTime = applianceStatusChangingTime;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public String getApplianceName() {
		return applianceName;
	}

	public void setApplianceName(String applianceName) {
		this.applianceName = applianceName;
	}

	

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserEmailId() {
		return userEmailId;
	}

	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}



	public ApplianceDto() {
		super();
	}

	

	public ApplianceDto(String applianceName, String userEmailId, long roomId, String deviceId, boolean applianceStatus,String roomName) {
	super();
	this.applianceName = applianceName;
	this.userEmailId = userEmailId;
	this.roomId = roomId;
	this.deviceId = deviceId;
	this.applianceStatus = applianceStatus;
	this.roomName=roomName;
}

	public boolean isApplianceStatus() {
		return applianceStatus;
	}

	public void setApplianceStatus(boolean applianceStatus) {
		this.applianceStatus = applianceStatus;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	 
}
