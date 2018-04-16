package com.shadego.kcs.util;

import org.apache.commons.lang3.StringUtils;

public class GlobalConst {
    public static final Integer threadNum;
	public static final Integer retryTime;
	public static final Integer downlodMode;
	public static final String filePath;
	public static final String token;

	static {
	    threadNum= setValue(10, "threads");
		retryTime = setValue(3, "retryTime");
		downlodMode= setValue(1, "downlodMode");
		filePath=setValue("", "filePath");
		token=setValue("", "token");
	}

	private static String setValue(String param, String value) {
		String str = ConfigUtil.loadProperty(value);
		if (!StringUtils.isEmpty(str)) {
			return str;
		}
		return param;
	}

	private static Integer setValue(Integer param, String value) {
		String str = ConfigUtil.loadProperty(value);
		if (!StringUtils.isEmpty(str)) {
			return Integer.parseInt(str);
		}
		return param;
	}
}
