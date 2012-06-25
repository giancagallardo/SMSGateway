package com.quiputech.sms.server.rest;

public class SingleSMSResponse {

	private String status;
	private double available;
	private String uuid;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getAvailable() {
		return available;
	}

	public void setAvailable(double available) {
		this.available = available;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
