package com.taji.dto;

public class CheckCreativesResultDTO {
	
	private int creativesChecked = 0;
	private int creativesAddedToCampaign = 0;
	
	public int getCreativesAddedToCampaign() {
		return creativesAddedToCampaign;
	}
	
	public int getCreativesChecked() {
		return creativesChecked;
	}
	
	public void incrementChecked() {
		creativesChecked++;
	}
	
	public void incrementAdded() {
		creativesAddedToCampaign++;
	}

}
