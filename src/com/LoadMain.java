package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Properties;

/**
 * This class is a main class for program load_Iinm
 *  that connect to Illinois Department of Corrections.
 * Then download and store all information of all inmate from database,
 * also it generate spreadsheet (.csv) from the data that was collected.
 * @author Red, Tangratanavalee (koke6645@gmail.com)
 * @version 2.0
*/
public class LoadMain {
	
	private static java.util.Date runDate;
	private static String runCode;
	private static Properties sprop;
	private static String listFld;
	private static String detailFld;
	private static String csvFld;	
	private static char[] lastList;
	private static String lName;
	
	
	private static void init() {
		runDate = new java.util.Date();
		runCode = Util.dtoc("yyMMdd", runDate);
		sprop = new Properties();
		try {
			sprop.load(new FileInputStream(new File("config.ini")));
			
			listFld = sprop.getProperty("LIST_FOLDER");
			initFolder(listFld);
			
			detailFld = sprop.getProperty("DETAIL_FOLDER");
			initFolder(detailFld);
			
			csvFld = sprop.getProperty("CSV_FOLDER");
			initFolder(csvFld);
			
			lName = sprop.getProperty("LASTNAME");
			initList(lName);
			
			File logFld = new File("log");
			if (!logFld.isDirectory()) {
				logFld.mkdirs();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initFolder(String fldPath) {
		File fld1 = new File(fldPath);
		if (!fld1.isDirectory()) {
			fld1.mkdirs();
		}
	}

	private static void initList(String list) {
		list = list.replaceAll("[^A-Z]+","");
		lastList = list.toCharArray();
	}
	
	private static boolean isWantToRun(String input) {
		char isRun = sprop.getProperty(input).charAt(0);
		if(isRun != 'Y') {
			if(isRun != 'N') {
				System.out.printf("\n[ERROR]\tIncorrect command in config.ini: -> Check %s input.", input);
				Log.logMsg(runCode, String.format("[ERROR]\tIncorrect command in config.ini: -> Check %s input.", input));
			}
			System.out.printf("\n%s is not running.\n", input);
			Log.logMsg(runCode, String.format("%s is not running.", input));
			return false;
		} 
		else return true;
	}
	
	// ------------------------------------------------------------------------
	
	private static void runGetList() {		

		if(!isWantToRun("GET_LIST")){
			return;
		}
		Log.logMsg(runCode, String.format("Start creating list."));
		System.out.printf("\nprocess -> ");
		
		File outFld = new File(String.format("%s/%s", listFld, runCode));
		if (outFld.isDirectory()) {
			outFld.delete();
		}
		if (!outFld.isDirectory()) {
			outFld.mkdirs();
		}
		
		// Save raw file to local.
		for (char chr1: lastList) { 
			//
			System.out.printf("[%s] ", chr1);
			File outFile1 = new File(String.format("%s/%s/%s_%s_raw.html", listFld, runCode, runCode, chr1));
			File outFile2 = new File(String.format("%s/%s/%s_%s.txt", listFld, runCode, runCode, chr1));
			GetList.saveListToFile(runCode, chr1, outFile1, outFile2);
			//
		}
		System.out.printf("|Complete|\n\n");
		//System.out.printf("Finish creating list [%s].\n\n", Util.dtoc("HH:mm:ss", new java.util.Date()));
		Log.logMsg(runCode, String.format("Finish creating list."));
	}
	
	private static void runGetDetail() {
		if(!isWantToRun("GET_DETAIL")){
			return;
		}
		Log.logMsg(runCode, String.format("Start creating inmate detail."));
		
		try {

			File outFld = new File(String.format("%s/%s", detailFld, runCode));
			if (outFld.isDirectory()) {
				outFld.delete();
			}
			if (!outFld.isDirectory()) {
				outFld.mkdirs();
			}

			for (char chr1: lastList) { 
				File lstFile = new File(String.format("%s/%s/%s_%s.txt", listFld, runCode, runCode, chr1));
				if (lstFile.exists()) {
					GetDetail.processListFile(runCode, sprop, runDate, lstFile);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.logMsg(runCode, e.getMessage());
		}
		
		Log.logMsg(runCode, String.format("Finish creating inmate detail."));

	}
	
	private static void runCSV() {
		if(!isWantToRun("GET_CSV")){
			return;
		}
		Log.logMsg(runCode, String.format("Start creating .csv file."));
		
		FilenameFilter flt1 = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("_parsed.txt");
			}
		};
		
		try {						
			File outFld = new File(String.format("%s/%s.csv", csvFld, runCode));
			FileWriter csv = new FileWriter(outFld);
			
			File detFld1 = new File(String.format("%s/%s", detailFld, runCode));
			File[] allFiles = detFld1.listFiles(flt1);
				
			int count = 0;
			int current =0;
			int percent = 10;
			int total = new File(String.format("%s/%s", detailFld, runCode)).listFiles().length;
			total = total / 2;
			System.out.printf("\nprocess -> [csv]: ");
			
			//csv header tab
			for (String fld1 : Util.fldLst) { csv.write(String.format("%s\t", fld1)); }
			csv.write("\n");

			//System.out.println(delFld1.length());
			
			for (File inmFile : allFiles) {	
				
				//Display progress state
				count++;
				current = (100 * count)/total;
				if(current >= percent && current <= 100) {
					System.out.printf("%d ", current);
					percent = percent + 10;
				}
				
				java.util.HashMap<String, String> values = new java.util.HashMap<String, String>();
				
				StringBuilder sbr = new StringBuilder();
				int in;
				char chr = '-';
				int lastInx = 0;
				boolean valFlg = false;
				
				FileInputStream input = new FileInputStream(inmFile);
				while ((in = input.read()) != -1) {
					chr = (char) in;
					if (valFlg) { 
						sbr.append(chr); 
						
						//Handle newline in "MARKS, SCARS, &amp; TATTOOS"
						if(sbr.toString().endsWith("\n")) {
							sbr.deleteCharAt(sbr.length()-1);
							sbr.deleteCharAt(sbr.length()-1);
							sbr.append(" | ");
						}
					}
					
					if (chr == '[') {
						valFlg = true;
					} else if (chr == ']') {
						valFlg = false;
						sbr.deleteCharAt(sbr.length() - 1);
						values.put(Util.fldLst[lastInx], sbr.toString());
						lastInx++;
						sbr.delete(0, sbr.length());
					}	

				}
				
				for (String fld1 : Util.fldLst) {
					csv.write(String.format("%s\t", values.get(fld1)));
				}
				csv.write("\n");				
				input.close();				
			}
			csv.flush();
			csv.close();
			System.out.printf("|Complete|\n");
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.logMsg(runCode, e.getMessage());
		}
		
		Log.logMsg(runCode, String.format("Finish creating .csv file."));

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final long start = System.currentTimeMillis();
		System.out.printf("\nStart\n");
		init();
		Log.logMsg(runCode, "  ***program started.***");
		runGetList();
		runGetDetail();
		runCSV();
		Log.logMsg(runCode, "  ***program finished.***\n\n\n");
		System.out.printf("\nFinished\n");
		
		final long end = System.currentTimeMillis();
		
		System.out.printf("Process time: %f sec\n", (end - start) / 1000.0);
	}
}
