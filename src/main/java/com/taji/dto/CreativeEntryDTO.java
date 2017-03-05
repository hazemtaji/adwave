package com.taji.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.taji.model.CreativeEntry;

public class CreativeEntryDTO {

	private int id;
	private String name;
	private String desc;
	private String type;
	private String message;
	private String postId;
	private String imageHash;
	private String uploadedCreativeId;
	private int status;
	private List<String> tags = new ArrayList<>();
	
	public CreativeEntryDTO() {
	}
	
	public CreativeEntryDTO(CreativeEntry src) {
		this.id = src.getId();
		this.name = src.getName();
		this.desc = src.getDesc();
		this.type = src.getType();
		this.status = src.getStatus();
		this.message = src.getMessage();
		this.postId = src.getPostId();
		
		this.imageHash = src.getImageHash();
		this.uploadedCreativeId = src.getUploadedCreativeId();
		
		this.tags.addAll(src.getTags());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getPostId() {
		return postId;
	}
	
	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	public String getUploadedCreativeId() {
		return uploadedCreativeId;
	}
	
	public void setUploadedCreativeId(String uploadedCreativeId) {
		this.uploadedCreativeId = uploadedCreativeId;
	}
	
	public String getImageHash() {
		return imageHash;
	}
	
	public void setImageHash(String imageHash) {
		this.imageHash = imageHash;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
