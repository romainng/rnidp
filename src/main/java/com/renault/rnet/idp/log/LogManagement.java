package com.renault.rnet.idp.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

public class LogManagement {

	private org.slf4j.Logger log = LoggerFactory.getLogger(LogManagement.class);
	private File logFile;

	private String path = "";
	public LogManagement(String path) {
		this.path = path;
		this.logFile = new File(path);
		
		
	}

	public String getLogStringFormat() {
		BufferedReader br = null;
		FileReader fr = null;
		StringBuilder strb = new StringBuilder();

			try {
				fr = new FileReader(this.logFile);
				br = new BufferedReader(fr);

				String sCurrentLine;

				// br = new BufferedReader(new FileReader(this.logFile));

				while ((sCurrentLine = br.readLine()) != null) {
					strb.append(sCurrentLine);
					strb.append(System.lineSeparator());

				}
			} catch (IOException e) {
				log.error("Can't find application log file at "+ this.path);
			}
		

		return strb.toString();
	}

	public ArrayList<String> getLogListFormat() {
		BufferedReader br = null;
		FileReader fr = null;
		ArrayList<String> logList = new ArrayList<String>();
		try {
			fr = new FileReader(this.logFile);
			br = new BufferedReader(fr);

			String sCurrentLine;

			// br = new BufferedReader(new FileReader(this.logFile));

			while ((sCurrentLine = br.readLine()) != null) {
				logList.add(sCurrentLine);

			}
		} catch (IOException e) {
			log.error("Can't find application log file at "+ this.path);
		}

		return logList;
	}

	public String getFilePath(){
		return this.path;
	}
}
