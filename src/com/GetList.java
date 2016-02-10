package com;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetList {
	
	static void saveListToFile(String runCode, char lnamePrf, File outFile1, File outFile2) {
		//System.out.printf("Processing [%s]\nRaw [%s]\nParsed [%s]\n", lnamePrf, outFile1.getAbsolutePath(), outFile2.getAbsolutePath());
		Log.logMsg(runCode, String.format("Processing [%s]\nRaw [%s]\nParsed [%s]", lnamePrf, outFile1.getAbsolutePath(), outFile2.getAbsolutePath()));
		try {
			String urlParameters = "selectlist1=Last&idoc="+ lnamePrf +"&submit=Inmate%20Search";
			String request = "http://www.idoc.state.il.us/subsections/search/ISListInmates2.asp";
			
			URL url = new URL(request);
			
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setInstanceFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("charset", "utf-8");
			con.setRequestProperty("Content-Length", ""+Integer.toString(urlParameters.getBytes().length));
			con.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			//int responseCode = con.getResponseCode();
			//System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Post parameters : " + urlParameters);
			//System.out.println("Response Code : " + responseCode);
			//System.out.println();
			
			FileOutputStream fout = new FileOutputStream(outFile1);
			
			InputStream is = con.getInputStream();
			int rd1 = -1;
			char prvChr = '-';
			while ((rd1 = is.read()) != -1) {
				char chr1 = (char) rd1;
				if (chr1 == '<' || chr1 == '>') {
					if (prvChr == '>' && chr1 == '<') {
						//System.out.println();
						fout.write('\n');
					}
					prvChr = chr1;
				}
				//System.out.print(chr1);
				fout.write(chr1);
			}
			is.close();
			
			fout.flush();
			fout.close();
			
			processRawFile(runCode, outFile1, outFile2);
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.logMsg(runCode, e.getMessage());
		}
		//System.out.println();
	}
	
	private static void processRawFile(String runCode, File inpFile, File outFile) {
		try {
			FileOutputStream fout = new FileOutputStream(outFile);
			
			boolean foundData = false;
			int totalData = 0;
			int dataParsed = 0;
			
			java.util.Scanner scn1 = new java.util.Scanner(inpFile);
			while (scn1.hasNext()) {
				String line1 = scn1.nextLine().trim();
				if (foundData) {
					if (line1.startsWith("</font>")) {
						//System.out.printf("[%s]\n", line1.substring(7));
						fout.write(line1.substring(7).getBytes());
						fout.write('\n');
						dataParsed += 1;
						foundData = false;
					}
				} else {
					if (line1.startsWith("<OPTION")) {
						foundData = true;
					}
				}
				if (line1.startsWith("<br>Matches found: ")) {
					totalData = Integer.parseInt(line1.substring(19));
				}
			}
			scn1.close();
			
			//System.out.printf("Total [%d]\nParsed [%d]\nCorrect [%s]\n", totalData, dataParsed, (totalData==dataParsed ? "Yes" : "No"));
			Log.logMsg(runCode, String.format("Total [%d]\nParsed [%d]\nCorrect [%s]", totalData, dataParsed, (totalData==dataParsed ? "Yes" : "No")));		

			fout.write('\n');
			fout.write("[EOD]".getBytes());
			
			fout.write('\n');
			
			//System.out.printf("process -> [%s] total: %s\n", runCode, totalData);
			String sum1 = String.format("\nTotal [%d]\nParsed [%d]\nCorrect [%s]\n",
					totalData, dataParsed, (totalData==dataParsed ? "Yes" : "No"));
			fout.write(sum1.getBytes());
			fout.write('\n');

			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.logMsg(runCode, e.getMessage());
		}
	}

}
