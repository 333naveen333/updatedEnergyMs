package com.energyms.energyms.model;




public class DataStats {

	private String userId;
	private double totalPowerConsumed;
	private double totalCost;
	private double totalCarbon;
	private String byThisDate;
	private long noOfAppliancesOn;
	private long noOfAppliancesOff;
	
	public long getNoOfAppliancesOff() {
		return noOfAppliancesOff;
	}
	public void setNoOfAppliancesOff(long noOfAppliancesOff) {
		this.noOfAppliancesOff = noOfAppliancesOff;
	}
	public long getNoOfAppliancesOn() {
		return noOfAppliancesOn;
	}
	public void setNoOfAppliancesOn(long noOfAppliancesOn) {
		this.noOfAppliancesOn = noOfAppliancesOn;
	}
	public String getByThisDate() {
		return byThisDate;
	}
	public void setByThisDate(String byThisDate) {
		this.byThisDate = byThisDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public double getTotalPowerConsumed() {
		return totalPowerConsumed;
	}
	public void setTotalPowerConsumed(double totalPowerConsumed) {
		this.totalPowerConsumed = totalPowerConsumed;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getTotalCarbon() {
		return totalCarbon;
	}
	public void setTotalCarbon(double totalCarbon) {
		this.totalCarbon = totalCarbon;
	}
	public DataStats( String userId,long totalPowerConsumed, long totalCost,
			double totalCarbon,String byThisDate,long noOfAppliancesOn,long noOfAppliancesOff) {
		super();
		this.userId=userId;
		this.totalPowerConsumed = totalPowerConsumed;
		this.totalCost = totalCost;
		this.totalCarbon = totalCarbon;
		this.byThisDate=byThisDate;
		this.noOfAppliancesOn=noOfAppliancesOn;
		this.noOfAppliancesOff=noOfAppliancesOff;
	}
	public DataStats() {
		super();
	}
}
		
	
		
