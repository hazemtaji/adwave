package com.taji.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;

import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINode;
import com.facebook.ads.sdk.Ad;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.AdCreative;
import com.facebook.ads.sdk.AdCreativeLinkData;
import com.facebook.ads.sdk.AdCreativeObjectStorySpec;
import com.facebook.ads.sdk.AdCreativeVideoData;
import com.facebook.ads.sdk.AdImage;
import com.facebook.ads.sdk.AdSet;
import com.facebook.ads.sdk.Campaign;
import com.facebook.ads.sdk.CustomAudience;
import com.facebook.ads.sdk.LookalikeSpec;
import com.facebook.ads.sdk.Targeting;
import com.facebook.ads.sdk.TargetingGeoLocation;
import com.facebook.ads.sdk.AdSet.EnumBillingEvent;
import com.facebook.ads.sdk.AdSet.EnumOptimizationGoal;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.facebook.ads.sdk.CustomAudience.EnumSubtype;
import com.facebook.ads.sdk.IDName;
import com.taji.dto.CheckCreativesResultDTO;
import com.taji.dto.NewCreativeEntryDTO;
import com.taji.model.CreativeEntry;
import com.taji.repository.CreativeEntryRepository;
import com.taji.service.CreativeEntryService;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Like;
import facebook4j.Media;
import facebook4j.PostUpdate;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.VideoUpdate;

@Service
public class CreativeEntryServiceImpl implements CreativeEntryService {
	
	private CreativeEntryRepository ceRepo;
	private Facebook facebook;
	private Campaign campaign;
	private AdAccount adAccount;
	
	private String appId;
	private String pageId;

	@Autowired
	public CreativeEntryServiceImpl(CreativeEntryRepository ceRepo, Facebook facebook, Campaign campaign, AdAccount adAccount, Environment env) {
		this.ceRepo = ceRepo;
		this.facebook = facebook;
		this.campaign = campaign;
		this.adAccount = adAccount;

		appId = env.getProperty("fb.app_id");
		pageId = env.getProperty("fb.page_id");
		
		populateData();
	}
	
	public void populateData() {
		CreativeEntry entry1 = new CreativeEntry(
				"Panda Ad",
				"Photographer at Zoo fully ready set up with camera on tripod waits in eager anticipation to capture images of the Pandas. After while, he decides to indulge in a KitKat, and as he does the Pandas emerge on roller skates and give a display of dancing around the enclosure. By the time he turns around they have skated off back into their accommodation and the photographer settles back waiting for 'the shot' oblivious to what has happened.",
				"video",
				"Throwback to that Panda Ad! Have a break, have a Kit Kat!",
				null,
				0,
				"panda", "zoo", "photography"
				);
		

		entry1.setStatus(2);
		//entry1.setPostId("183772012116745");
		entry1.setPostId("183408605486419_183772012116745");
		entry1.setUploadedCreativeId("120950791765090");
		entry1.setImageHash("ed0d096a0eb2ce783bf017d87a15072b");
		
		ceRepo.save(entry1);
		
	}

	@Override
	public CreativeEntry addNew(NewCreativeEntryDTO newCreativeEntry) {
		
		CreativeEntry entry = new CreativeEntry();
		entry.setName(newCreativeEntry.getName());
		entry.setDesc(newCreativeEntry.getDesc());
		entry.setType(newCreativeEntry.getType());
		entry.setMessage(newCreativeEntry.getMessage());
		entry.setStatus(0);
		
		ceRepo.save(entry);
		
		return entry;
	}

	@Override
	public Iterable<CreativeEntry> getAll() {
		return ceRepo.findAll();
	}

	@Override
	public CreativeEntry getById(int creativeId) {
		return ceRepo.findOne(creativeId);
	}

	@Override
	public String addTag(int creativeId, String tag) {
		CreativeEntry ce = getById(creativeId);
		
		if(ce.getTags().contains(tag)) {
			return null;
		}
		else {
			ce.getTags().add(tag);
			ceRepo.save(ce);
			return tag;
		}
	}
	
	public CreativeEntry activate(int creativeId) throws FacebookException {
		CreativeEntry ce = getById(creativeId);
		
		PostUpdate newPost = new PostUpdate(ce.getMessage());
		String postId = facebook.postFeed(newPost);
		
		ce.setStatus(1);
		ce.setPostId(postId);
		ceRepo.save(ce);
		
		return ce;
	}
	
	public CheckCreativesResultDTO checkCreatives(int countThreashold) {
		CheckCreativesResultDTO result = new CheckCreativesResultDTO();
		List<CreativeEntry> creatives = ceRepo.findByStatus(1);
		
		for(CreativeEntry creative : creatives){
			try {
				ResponseList<Like> likes = facebook.getPostLikes(creative.getPostId(), new Reading().limit(500));
				result.incrementChecked();
				if(likes.size() > countThreashold) {
					JsonArray data = new JsonArray();
					
					JsonArray schema = new JsonArray();
					schema.add(new JsonPrimitive("APPUID"));
					
					JsonArray appIds = new JsonArray();
					appIds.add(new JsonPrimitive(appId));
					
					for(Like like : likes) {
						JsonArray person = new JsonArray();
						person.add(new JsonPrimitive(sha256(like.getId())));
						data.add(person);
					}
					
					JsonObject payload = new JsonObject();
					payload.add("schema", schema);
					payload.add("data", data);
					payload.add("app_ids", appIds);
					
					CustomAudience customAudience = adAccount.createCustomAudience()
						.setName(creative.getName() + " Custom Audience")
						.setSubtype(EnumSubtype.VALUE_CUSTOM)
						.execute();
					
					customAudience.createUser()
						.setPayload(payload.toString())
						.execute();
					
					JsonObject lookalikeSpec = new JsonObject();
					lookalikeSpec.add("type", new JsonPrimitive("similarity"));
					lookalikeSpec.add("country", new JsonPrimitive("AE"));
					
					CustomAudience finalAudience = customAudience;
					
					if(likes.size() > 100) {
						CustomAudience lookalikeAudience = adAccount.createCustomAudience()
							.setName(creative.getName() + " Lookalike Audience")
							.setSubtype(EnumSubtype.VALUE_LOOKALIKE)
							.setLookalikeSpec(lookalikeSpec.toString())
							.setOriginAudienceId(customAudience.getId())
							.execute();
						
						finalAudience = lookalikeAudience;
					}
					
					AdSet adset = adAccount.createAdSet()
						.setName(creative.getName() + " Adset")
						.setOptimizationGoal(EnumOptimizationGoal.VALUE_BRAND_AWARENESS)
						.setBillingEvent(EnumBillingEvent.VALUE_IMPRESSIONS)
						.setIsAutobid(true)
						.setDailyBudget(1000L)
						.setCampaignId(campaign.getId())
						.setTargeting(
							new Targeting()
							.setFieldCustomAudiences(Arrays.asList(new IDName().setFieldId(finalAudience.getId())))
							.setFieldGeoLocations(new TargetingGeoLocation().setFieldCountries(Arrays.asList("AE")))
						)
						.setStatus(AdSet.EnumStatus.VALUE_PAUSED)
						.execute();
					
					AdCreative adCreative = null;
					if(creative.getType().equals("image")) {
						
						adCreative = adAccount.createAdCreative()
							.setName(creative.getName() + " Creative")
							.setObjectStorySpec(
								new AdCreativeObjectStorySpec()
									.setFieldLinkData(
								        new AdCreativeLinkData()
								          .setFieldCaption(creative.getName())
								          .setFieldImageHash(creative.getImageHash())
								          .setFieldMessage(creative.getMessage())
								      )
								      .setFieldPageId(pageId)
								  )
							.execute();
					} else if (creative.getType().equals("video")) {
						
						adCreative = adAccount.createAdCreative()
								.setName(creative.getName() + " Creative")
								.setObjectStorySpec(
									new AdCreativeObjectStorySpec()
										.setFieldVideoData(
											new AdCreativeVideoData()
											.setFieldDescription("Try it out")
									        .setFieldImageHash(creative.getImageHash())
									        .setFieldVideoId(creative.getUploadedCreativeId())
										)
									    .setFieldPageId(pageId)
									)
								.execute();
					}
					
					if(adCreative != null) {
						Ad ad = adAccount.createAd()
							.setName(creative.getName() + " Ad")
							.setAdsetId(adset.getId())
							.setCreative(
									new AdCreative()
									.setFieldId(adCreative.getId())
									)
							.setStatus(Ad.EnumStatus.VALUE_PAUSED)
							.execute();
					}
					
					creative.setStatus(2);
					ceRepo.save(creative);
					
					result.incrementAdded();
				}
			} catch (FacebookException e) {
				e.printStackTrace();
			} catch (APIException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public void uploadVideo(int creativeId, MultipartFile vidFile, MultipartFile imgFile) throws IllegalStateException, IOException, APIException {
		CreativeEntry creative = getById(creativeId);
		
		File convVidFile = new File( "/tmp/" + vidFile.getOriginalFilename());
		vidFile.transferTo(convVidFile);
		
		File convImgFile = new File( "/tmp/" + imgFile.getOriginalFilename());
		imgFile.transferTo(convImgFile);
		
		AdImage adImage = adAccount.createAdImage()
				.addUploadFile(convImgFile.getName(), convImgFile)
				.execute();
		
		APINode adVideo = adAccount.createAdVideo()
				.setName(creative.getName())
				.setDescription(creative.getDesc())
				.setTitle(creative.getMessage())
				.addUploadFile(convVidFile.getName(), convVidFile)
				.execute();
		
		creative.setUploadedCreativeId(adVideo.getId());
		creative.setImageHash(adImage.getFieldHash());
		
		ceRepo.save(creative);
	}
	
	public static String sha256(String message) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
			return toHex(hash);
		} catch (Exception e) {
			return null;
		}
	}

	public static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%1$02x", b));
		}
		return sb.toString();
	}

}
