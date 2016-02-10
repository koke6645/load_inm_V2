package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class GetDetail {

	static void processListFile(String runCode, java.util.Properties sprop,
			java.util.Date rdate, File lstFile) throws Exception {
		//				
		java.util.Scanner scn1 = new java.util.Scanner(lstFile);
		int guess = (int) ((lstFile.length()/50) + (lstFile.length() * 0.003));
		int count = 0;
		int current =0;
		int percent = 10;
		
		System.out.printf("process -> [%s]: ", lstFile.getName());
		
		while (scn1.hasNext()) {
			String line1 = scn1.nextLine().trim();
			
			if ("[EOD]".equals(line1)) {
				break;
			}
			if (!line1.contains("|")) continue;
			
			String[] dts = line1.split("[|]");

			String idoc = dts[0].trim();
			//String idob = dts[1].trim();
			//String iname = dts[2].trim();
			//System.out.printf("[%s] [%s] [%s]\n", idoc, idob, iname);
			//System.out.printf("process -> [%s] [%s] [%s]\n", idoc, iname, Util.dtoc("HH:mm:ss", new java.util.Date()));

			//Display progress state
			count++;
			current = (100 * count)/guess;
			if(current >= percent && current <= 100) {
				System.out.printf("%d ", current);
				percent = percent + 10;
			}
			
			String dtFileName = String.format("%s/%s/%s_%s.html", sprop.getProperty("DETAIL_FOLDER"), runCode, runCode, idoc);
			File dtFile = new File(dtFileName);
			if (fromURLtoFile(runCode, idoc, dtFile)) {
				String dtOutName = String.format("%s/%s/%s_%s_parsed.txt", sprop.getProperty("DETAIL_FOLDER"), runCode, runCode, idoc);
				File dtOut = new File(dtOutName);
				if (processDetailFile(runCode, idoc, dtFile, dtOut)) {
					//System.out.printf("[%s] parsed to [%s]\n", idoc, dtOut.getAbsolutePath());
				} else {
					// not success ... log
					Log.logMsg(runCode, String.format("[ERROR] can't process detail idoc=[%s]", idoc));
				}
			} else {
				// not success ... log
				Log.logMsg(runCode, String.format("[ERROR] can't get detail idoc=[%s]", idoc));
			}
			//System.out.println();
		}
		scn1.close();
		System.out.printf("|Complete|\n");

	}

	// -----------------------------------------------------------------------------------

	private static String url_idoc_info = "http://www.idoc.state.il.us/subsections/search/ISinms2.asp?idoc=%s";
	
	private static boolean fromURLtoFile(String runCode, String idoc, File outFile) {
		boolean retFlg = false;
		try {
			FileOutputStream fout = new FileOutputStream(outFile);

			URL url = new URL(String.format(url_idoc_info, idoc));
			InputStream is = url.openStream();
			int in;
			char prvChr = '-';
			while ((in = is.read()) != -1) {
				char tmpChr = (char) in;
				if (tmpChr == '>' || tmpChr == '<') {
					if (prvChr == '>' && tmpChr == '<') {
						fout.write('\n');
					}
					prvChr = tmpChr;
				}
				fout.write(tmpChr);
			}
			is.close();

			fout.close();

			//System.out.printf("[%s] saved to [%s].\n", idoc, outFile.getAbsolutePath());

			retFlg = true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.logMsg(runCode, e.getMessage());
		}
		return retFlg;
	}

	private static boolean processDetailFile(String runCode, String idoc, File dtFile, File dtOut) {
		boolean retFlg = false;
		try {
			boolean notFnd = false;
			
			java.util.HashMap<String, String> values = new java.util.HashMap<String, String>();
			String key = ""; //First key
			int lastIdx = -1;

			StringBuilder stb = new StringBuilder();
			boolean clrFlg = false;

			String name = "";
			String chkNameSuffix = String.format(">%s - ", idoc);
			boolean foundName = false;

			FileInputStream fis = new FileInputStream(dtFile);
			int in = -1;
			while ((in = fis.read()) != -1) {
				char chr1 = (char) in;
				stb.append(chr1);

				if (name.isEmpty()) {
					if (foundName) {
						if (stb.toString().endsWith("</font>")) {
							name = stb.toString().substring(0, stb.length() - 7).trim();
						}
					} else {
						if (stb.toString().endsWith(chkNameSuffix)) {
							stb.delete(0, stb.length());
							foundName = true;
						} else if (stb.toString().endsWith("Inmate NOT found")) {
							notFnd = true;
							Log.logMsg(runCode, String.format("[ERROR] Inmate NOT found [%s]", idoc));
							break;
						}
					}
				} else {
					if (stb.length() < key.length())
						continue;
					if (key.isEmpty()) {	
						for (int i = 5; i < Util.fldLst.length; i++) {
							if (stb.toString().endsWith(Util.fldLst[i])) {
								lastIdx = i;
								key = Util.fldLst[i];
								clrFlg = true;
								break;
							}
						}
					} else {
						String tmpKey = ""; // Next key
						for (int i = lastIdx + 1; i < Util.fldLst.length; i++) {
							if (stb.length() < Util.fldLst[i].length())
								continue;
							if (stb.toString().endsWith(Util.fldLst[i])) {
								lastIdx = i;
								tmpKey = Util.fldLst[i];
								clrFlg = true;
								break;
							}
						}
						if (!tmpKey.isEmpty()) {
							values.put(key, stb.toString().substring(0, stb.length() - tmpKey.length()));
							
							//value for Sex Offender Registry Required
							if(key == Util.fldLst[9]) { values.put(key, "True"); }
							
							key = tmpKey;
						}
					}
					if (clrFlg) {
						stb.delete(0, stb.length());
						clrFlg = false;
					}
				}
			}
			fis.close();
			
			if (notFnd) {
				//Inmate NOT found
				return false;
			}
			
			//Part first and last name
			String last = name.substring(0, name.indexOf(","));
			String first = name.substring(name.indexOf(",") + 2, name.length());			
			
			//Get front and side URL pic
			String frontPic = Util.picURL[0] + idoc;
			String sidePic = Util.picURL[1] + idoc;
			
			//Store detail
			FileOutputStream fout = new FileOutputStream(dtOut);
			fout.write(String.format("idoc: = [%s]\nFirst: = [%s]\nLast: = [%s]\n", idoc, first, last).getBytes());			
			fout.write(String.format("Front Pic: = [%s]\nSide Pic: = [%s]\n", frontPic, sidePic).getBytes());
			for (int i=5; i < Util.fldLst.length; i++) {
				fout.write(String.format("%s = [%s]\n", Util.fldLst[i], cleanValue2(values.get(Util.fldLst[i]))).getBytes());
			}
			fout.write('\n');
			fout.flush();
			fout.close();

			retFlg = true;

		} catch (Exception e) {
			e.printStackTrace();
			Log.logMsg(runCode, e.getMessage());
		}
		return retFlg;
	}

	private static String cleanValue2(String rawVal) {
		if (null == rawVal) return "";
		if (rawVal == "True") return "True";

		StringBuilder stb = new StringBuilder();

		int loc1 = -1;
		int loc2 = rawVal.length();
		while ((loc1 = rawVal.lastIndexOf("</font>", loc2)) >= 0) {
			loc2 = rawVal.lastIndexOf("\">", loc1);
			if (loc2 < 0) break;
			if (stb.length() > 0) {
				stb.append("\r\n");
			}
			String tmpStr = rawVal.substring(loc2 + 2, loc1).trim();
			if (tmpStr.startsWith("<b>")) {
				tmpStr = tmpStr.substring(3);
			}
			if (tmpStr.endsWith("</b>")) {
				tmpStr = tmpStr.substring(0, tmpStr.length() - 4);
			}
			if (tmpStr.endsWith("|")) {
				tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
			}
			if (tmpStr.indexOf("|") > -1) {
				tmpStr = tmpStr.replace('|', ',');
			}
			stb.append(tmpStr.trim());
			loc1 = rawVal.lastIndexOf("</font>", loc2);
		}
		return stb.toString();
	}
}
