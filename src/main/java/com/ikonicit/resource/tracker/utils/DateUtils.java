package com.ikonicit.resource.tracker.utils;

import com.ikonicit.resource.tracker.exception.MailSendFailedException;
import org.springframework.mail.SimpleMailMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateUtils {

	
	public static String getDateAsStringFormat(Date anyDate) {

		if(anyDate!=null) {

				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				return dateFormat.format(anyDate);
		} else {

		}
		return null;
     }

	public static Date getDateFromString(String anyStringDate) throws ParseException {

		if(anyStringDate != null) {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date anyDate = dateFormat.parse(anyStringDate);
			return anyDate;
		}
		else {
			return null;
		}
	}




}