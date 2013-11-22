package com.twitter.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Twitter implements EntryPoint {

	@Override
	public void onModuleLoad() {
		GreetingServiceAsync service = GWT.create(GreetingService.class);
		final VerticalPanel panel = new VerticalPanel();
		RootPanel.get().add(panel);
		service.greetServer("rsarver", new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				JSONObject jsonObject = JSONParser.parseLenient(result).isObject();
				UsersShow usersShow = new UsersShow(jsonObject);
				panel.add(usersShow.getProfileImage());
				RootPanel.get().getElement().getStyle().setBackgroundImage("url(\""+ usersShow.getBackgroundImageUrl() +"\")");
				RootPanel.get().getElement().getStyle().setBackgroundColor("#" + usersShow.getBackgroundColor());
				Label nameLable = new Label(usersShow.getName());
				nameLable.getElement().getStyle().setColor("#" + usersShow.getTextColor());
				panel.add(nameLable);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO 自動生成されたメソッド・スタブ
				
			}
		});
	}
}
