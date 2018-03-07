package net.suntrans.engineering.mdns.helper;
import org.json.JSONArray;

/**
 * Many javas will use this function Project：MiCOSDK Author：Sin Creat time
 * 2016-01-20
 * 
 * @version 1.0
 */
public class CommonFunc {
	
	/**
	 * Check argument, whether it is null or blank
	 * 
	 * @param param param
	 * @return true false
	 */
	public boolean checkPara(String... param) {
		if (null == param || param.equals("")) {
			return false;
		} else if (param.length > 0) {
			for (String str : param) {
				if (null == str || str.equals("")) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * About mDNS, we will call back to the developer. 
	 * @param code code
	 * @param message message
	 * @param searchdevcb searchdevcb
	 */
	public void successCBmDNS(int code, String message, SearchDeviceCallBack searchdevcb) {
		if (null == searchdevcb)
			return;
		searchdevcb.onSuccess(code, message);
	}
	public void failureCBmDNS(int code, String message, SearchDeviceCallBack searchdevcb) {
		if (null == searchdevcb)
			return;
		searchdevcb.onFailure(code, message);
	}
	public void onDevsFindmDNS(JSONArray deviceStatus, SearchDeviceCallBack searchdevcb) {
		if (null == searchdevcb)
			return;
		searchdevcb.onDevicesFind(MDNSErrCode.MDNS_CODE, deviceStatus);
	}
}
