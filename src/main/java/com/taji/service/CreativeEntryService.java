package com.taji.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.facebook.ads.sdk.APIException;
import com.taji.dto.CheckCreativesResultDTO;
import com.taji.dto.NewCreativeEntryDTO;
import com.taji.model.CreativeEntry;

import facebook4j.FacebookException;

public interface CreativeEntryService {
	
	public CreativeEntry addNew(NewCreativeEntryDTO newCreativeEntry);

	public Iterable<CreativeEntry> getAll();

	public CreativeEntry getById(int creativeId);

	public String addTag(int creativeId, String tag);
	
	public CreativeEntry activate(int creativeId) throws FacebookException;

	public CheckCreativesResultDTO checkCreatives(int countThreashold);

	public void uploadVideo(int creativeId, MultipartFile vidFile, MultipartFile imgFile) throws IllegalStateException, IOException, APIException;

}
