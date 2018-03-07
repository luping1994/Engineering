package net.suntrans.engineering.mdns.helper;

import org.json.JSONArray;

public abstract class SearchDeviceCallBack {
	public void onSuccess(int code, String message){}
	public void onFailure(int code, String message){}
	public void onDevicesFind(int code, JSONArray deviceStatus){}
}
