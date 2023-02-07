package com.energyms.energyms.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;





@Entity
@Table(name = "appliances")
public class Appliance {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long applianceId;

	@NotBlank
	private String applianceName;
	
	@Column(nullable = true)
	private boolean applianceStatus=false;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_email_id", referencedColumnName = "emailId")
	private User user;

	@ManyToOne
	@JoinColumn(name = "room_id", referencedColumnName = "roomId")//name
	private Room room;

	@OneToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
	@JoinColumn(name = "deviceId", referencedColumnName = "deviceId")
	private Device device;
    
	private String applianceStatusChangingTime;
	
	public Appliance() {
		super();
	}

	public Appliance(String applianceName, boolean applianceStatus, User user, Room room, Device device ,String applianceStatusChangingTime) {
		super();
		this.applianceName = applianceName;
		this.applianceStatus = applianceStatus;
		this.user = user;
		this.room = room;
		this.device = device;
		this.applianceStatusChangingTime=applianceStatusChangingTime;

	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public long getApplianceId() {
		return applianceId;
	}

	public void setApplianceId(long applianceId) {
		this.applianceId = applianceId;
	}

	public String getApplianceName() {
		return applianceName;
	}

	public void setApplianceName(String applianceName) {
		this.applianceName = applianceName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isApplianceStatus() {
		return applianceStatus;
	}

	public void setApplianceStatus(boolean applianceStatus) {
		this.applianceStatus = applianceStatus;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getApplianceStatusChangingTime() {
		return applianceStatusChangingTime;
	}

	public void setApplianceStatusChangingTime(String applianceStatusChangingTime) {
		this.applianceStatusChangingTime = applianceStatusChangingTime;
	}
	



}
