package com.taji.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINode;
import com.taji.dto.CheckCreativesResultDTO;
import com.taji.dto.CreativeEntryDTO;
import com.taji.dto.NewCreativeEntryDTO;
import com.taji.dto.NewTagDTO;
import com.taji.exceptions.ServerSideException;
import com.taji.model.CreativeEntry;
import com.taji.service.CreativeEntryService;

import facebook4j.FacebookException;

@Controller
public class AdsController {
	
	private CreativeEntryService creativeEntryService;

	@Autowired
	public AdsController(CreativeEntryService creativeEntryService) {
		this.creativeEntryService = creativeEntryService;
	}
	
	@RequestMapping("/ads/")
    public String getAdsPage(Model model) {
		model.addAttribute("creatives", creativeEntryService.getAll());
        return "ads";
    }
	
	@RequestMapping(value = "/ads/", method = RequestMethod.POST)
	public @ResponseBody CreativeEntryDTO addCreativeEntry(@RequestBody NewCreativeEntryDTO newCreativeEntry) {
		CreativeEntry newEntry = creativeEntryService.addNew(newCreativeEntry);
		return new CreativeEntryDTO(newEntry);
	}
	
	@RequestMapping("/ads/{creativeid}")
    public String getAdPage(Model model, @PathVariable("creativeid") int creativeId) {
		CreativeEntry creative = creativeEntryService.getById(creativeId);
		model.addAttribute("creative", new CreativeEntryDTO(creative));
        return "ads/view";
    }
	
	@RequestMapping(value = "/ads/{creativeid}/tags/", method = RequestMethod.POST)
	public @ResponseBody NewTagDTO addCreativeEntry(@PathVariable("creativeid") int creativeId, @RequestBody NewTagDTO tag) {
		String newTag = creativeEntryService.addTag(creativeId, tag.getTag());
		if(newTag != null)
			return tag;
		
		return null;
	}
	
	@RequestMapping(value = "/ads/{creativeid}/activate", method = RequestMethod.POST)
	public @ResponseBody CreativeEntryDTO activateCreativeEntry(@PathVariable("creativeid") int creativeId) {
		try {
			CreativeEntry ce = creativeEntryService.activate(creativeId);
			return new CreativeEntryDTO(ce);
		} catch (FacebookException e) {
			throw new ServerSideException("Could not create Facebook Post: \"" + e.getMessage() + "\"");
		}
	}
	
	@RequestMapping(value = "/ads/check/{threshold}", method = RequestMethod.GET)
	public @ResponseBody CheckCreativesResultDTO checkCreatives(@PathVariable("threshold") int threshold) {
		CheckCreativesResultDTO result = creativeEntryService.checkCreatives(threshold);
		
		return result;
	}
	
	@PostMapping("/ads/{creativeid}/creative/video/")
    public String handleFileUpload(@PathVariable("creativeid") int creativeId, @RequestParam("vidfile") MultipartFile vidFile, @RequestParam("imgfile") MultipartFile imgFile, RedirectAttributes redirectAttributes) throws IllegalStateException, IOException, APIException {

		creativeEntryService.uploadVideo(creativeId, vidFile, imgFile);
		
        return "redirect:/ads/" + creativeId + "/";
    }
}
