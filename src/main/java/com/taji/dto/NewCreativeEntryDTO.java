package com.taji.dto;

import com.taji.model.CreativeEntry;

public class NewCreativeEntryDTO {

	private String name;
	private String desc;
	private String type;
	private String message;
	
	public NewCreativeEntryDTO() {
	}
	
	public NewCreativeEntryDTO(CreativeEntry src) {
		this.name = src.getName();
		this.desc = src.getDesc();
		this.type = src.getType();
		this.message = src.getMessage();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
