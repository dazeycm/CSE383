/*
	Craig Dazey
	CSE383-f15
	
	Assignment 1
	
	Class used to log server output
	**TAKEN FROM LOG CLASS USED IN LAB**
*/

package dazeycm;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Log {
	String logName;
	FileOutputStream fos;
	PrintWriter pw;

	// open log
	public Log(String name) {
		logName = name;
		fos=null;
		pw=null;
		try {
			fos = new FileOutputStream(name);
			pw = new PrintWriter(fos);
		} catch (FileNotFoundException err) {
			System.err.println("Error creating log");
		}
	}

	// write log message
	public void log(String msg) {
		if (fos!=null) {
			pw.println(dateTime() + " "+  msg);
			pw.flush();
		}
	}

	//get current date time
	private String dateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
