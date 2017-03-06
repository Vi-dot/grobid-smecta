package org.grobid.service.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

	public static String currentDateISO() {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(timeZone);
		return dateFormat.format(new Date());
	}
	
	public static String findValueInText(String text, String label, String end) {
		int startIndex = text.indexOf(label) + label.length();
		int endIndex = text.indexOf(end, startIndex);
		
		return text.substring(startIndex, endIndex);
	}
	
	public static String addSpaces(String str, int totalLen) {
		while(str.length() < totalLen)
			str += " ";
		return str;
	}
}
