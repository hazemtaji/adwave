package com.taji.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.Campaign;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;

@Configuration
public class FacebookConfig {

	@Autowired
	private Environment env;
	
	public static String THROWBACK_CAMPAIGN_NAME = "Throwback Campaign";
	
	@Bean
	public APIContext apiContext() {
		
		String accessToken = env.getProperty("fb.access_token");
		String appSecret = env.getProperty("fb.app_secret");

		APIContext context = new APIContext(accessToken, appSecret);
		
		return context;
	}

	@Bean
	public AdAccount adAccount(APIContext context) {
		String accountId = env.getProperty("fb.account_id");
		AdAccount account = new AdAccount(accountId, context);
		return account;
	}

	@Bean
	public Campaign throwbackCampaign(AdAccount adAccount) throws APIException {
		
		APINodeList<Campaign> campaigns = adAccount.getCampaigns().requestNameField().execute();
		for(Campaign campaign : campaigns) {
			if(campaign.getFieldName().equals(THROWBACK_CAMPAIGN_NAME)) {
				campaign.update().setObjective(Campaign.EnumObjective.VALUE_BRAND_AWARENESS).execute();
				return campaign;
			}
		}
		
		Campaign campaign = adAccount.createCampaign()
			.setName(THROWBACK_CAMPAIGN_NAME)
			.setObjective(Campaign.EnumObjective.VALUE_BRAND_AWARENESS)
			.setStatus(Campaign.EnumStatus.VALUE_PAUSED)
			.execute();
			
		return campaign;
	}
	
	@Bean
	public Facebook facebookAPI() throws FacebookException {
		
		String accessToken = env.getProperty("fb.page_access_token");
		String appSecret = env.getProperty("fb.app_secret");
		String appId = env.getProperty("fb.app_id");
		
		Facebook facebook = new FacebookFactory().getInstance();
		
		facebook.setOAuthAppId(appId, appSecret);
		facebook.setOAuthPermissions("publish_pages");
		facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
		
		return facebook;
	}
	
	

}
