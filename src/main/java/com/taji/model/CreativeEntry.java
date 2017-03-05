package com.taji.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "creative_entry")
public class CreativeEntry {

	private int id;
	private String name;
	private String desc;
	private String type;
	private String message;
	private String postId;
	private String imageHash;
	private String uploadedCreativeId;
	private int status;
	private Set<String> tags = new HashSet<>();
	
	public CreativeEntry() {
	}
	
	public CreativeEntry(String name, String desc, String type, String message, String postId, int status, String... tags) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.message = message;
		this.postId = postId;
		this.status = status;
		
		for(String tag : tags) {
			this.tags.add(tag);
		}
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "desc", nullable = false, length = 4000)
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "type", nullable = false, length = 15)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "message", nullable = false)
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	@Column(name = "status")
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "postId")
	public String getPostId() {
		return postId;
	}
	
	public void setPostId(String postId) {
		this.postId = postId;
	}

	@Column(name = "uploadedCreative")
	public String getUploadedCreativeId() {
		return uploadedCreativeId;
	}
	
	public void setUploadedCreativeId(String uploadedCreativeId) {
		this.uploadedCreativeId = uploadedCreativeId;
	}

	@Column(name = "imageHash")
	public String getImageHash() {
		return imageHash;
	}
	
	public void setImageHash(String imageHash) {
		this.imageHash = imageHash;
	}

	@ElementCollection
	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

}
