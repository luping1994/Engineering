package net.suntrans.engineering.mdns.helper;

public class MDNSErrCode {

	public static int SUCCESS_CODE = 0;
	public static int MDNS_CODE = 4100;
	public static int EMPTY_CODE = 4101;
	public static int CONTEXT_CODE = 4102;
	public static int BUSY_CODE = 4103;
	public static int CLOSED_CODE = 4104;
	public static int EXCEPTION_CODE = 4106;
	
	public static String SUCCESS = toJsonM("success");
	public static String EMPTY = toJsonM("invalid param");
	public static String CONTEXT = toJsonM("invalid context");
	public static String BUSY = toJsonM("mdns busy");
	public static String CLOSED = toJsonM("mdns closed");

	private static String toJsonM(String message){
		return "{\"message\":\""+ message +"\"}";
	}
}
