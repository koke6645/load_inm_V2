package com;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Log {

	public static void logMsg(String runCode, String msg) {
		try {
			File flog = new File(String.format("log/log_%s.txt", runCode));
			PrintWriter out = new PrintWriter(new FileWriter(flog, true));
			out.println(new java.util.Date());
		    out.println(msg);
		    out.println();
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
