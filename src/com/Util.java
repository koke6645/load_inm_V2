package com;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Util {

	public final static String[] picURL = {
		"http://www.idoc.state.il.us/subsections/search/pub_showfront.asp?idoc=",
		"http://www.idoc.state.il.us/subsections/search/pub_showside.asp?idoc="};
	
	public final static String[] fldLst = { 
		"idoc",
		"First",
		"Last",
		"Front Pic",
		"Side Pic",
		"Alias:", 
		"Parent Institution:",
		"Offender Status:", 
		"Location:", 
		"Sex Offender Registry Required",
		"LAST REPORTED ADDRESS",
		"Street Address:", 
		"City, State, Zip:", 
		"County:",
		"PHYSICAL PROFILE", 
		"Date of Birth:", 
		"Weight:", 
		"Hair:", 
		"Sex:",
		"Height:", 
		"Race:", 
		"Eyes:", 
		"MARKS, SCARS, &amp; TATTOOS",
		"ADMISSION / RELEASE / DISCHARGE INFO", 
		"Admission Date:",
		"Electronic Detention Date:", 
		"Parole Date:", 
		"Last Paroled Date:",
		"Projected Discharge Date:", 
		"SENTENCING INFORMATION" };
	
	public static String dtoc(String pattern, java.util.Date date1) {
		SimpleDateFormat fmt1 = new SimpleDateFormat(pattern, Locale.US);
		return fmt1.format(date1);
	}
}
