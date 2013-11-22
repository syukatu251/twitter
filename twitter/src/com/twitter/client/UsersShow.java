package com.twitter.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Image;

public class UsersShow {
	JSONObject jsonObject;

	public UsersShow(JSONObject jsonObject) {
		super();
		this.jsonObject = jsonObject;
	}
	
	public Image getProfileImage() {
		String profileImageUrl = jsonObject.get("profile_image_url_https").isString().stringValue();
		return new Image(profileImageUrl);
	}
	
	public String getBackgroundImageUrl() {
		return jsonObject.get("profile_background_image_url_https").isString().stringValue();
	}
	
	public String getBackgroundColor() {
		return jsonObject.get("profile_background_color").isString().stringValue();
	}
	
	public String getName() {
		return jsonObject.get("name").isString().stringValue();
	}
	
	public String getTextColor() {
		return jsonObject.get("profile_text_color").isString().stringValue();
	}
}
