package com.cland.accessstats;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.cland.accessstats.db.mysql.MySQLAccess;

public class StatsGenerator {
	
	private static final int ENTRY_LIMIT_LATE_EVENING = 1;	//late_evening : 21-22pm
	private static final int ENTRY_LIMIT_EVENING = 8;		//evening : 17 - 20
	private static final int ENTRY_LIMIT_AFTERNOON = 5;		//afternoon : 12-13:00 PM
	private static final int ENTRY_LIMIT_MID_AFTERNOON = 4; //mid_afternoon : 14:00 - 16:30 PM
	private static final int ENTRY_LIMIT_MID_MORNING = 3;	//mid-_morning : 10-11 AM
	private static final int ENTRY_LIMIT_EARLY_MORNING = 4; //early_morning : 7-10 AM
	private static final int NUM_OF_CLUBS = 3;	
	private static final int ENTRY_SWIPE_PERIOD = 3;
	private static String DATE_FORMAT = "yyyy-M-dd HH:mm:ss"; //"dd/MM/yyyy HH:mm:ss";
	static int max_members = 500;
	
	static Map<Object,Member> mp = new HashMap<Object, Member>();
	static List<StatsEntry> entrystats = new ArrayList<StatsEntry>();
	private static Calendar closetime = Calendar.getInstance();	
	private static Calendar opentime= Calendar.getInstance();

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		setCloseTime(Calendar.getInstance());
		setOpenTime(Calendar.getInstance());

		//** Setup the members with gender and homeclub
		for(int i=0;i<max_members;i++){
			mp.put(i,new Member(i, getGender(), getClub(null)));
		}

		System.out.println("Generating stats... "); //mp.get(1) + "\n" + mp.get(5));

		//** setup the access starts from 2years back to now.		
		
		Calendar calnow = Calendar.getInstance();
		
		Calendar calstart = Calendar.getInstance();
		calstart.add(Calendar.YEAR, -2);		
		
		while(calnow.compareTo(calstart) > 0){			
			//** work out and offset
			int num_offset = getMemberEntryCountLimit(calstart);
			num_offset *= Math.ceil(NUM_OF_CLUBS/1);
			//** Generate random number of entries
			int num_members = new Random().nextInt(num_offset);
			
			//System.out.print("Entry Count: " + num_members + ") [" + calstart.get(Calendar.HOUR_OF_DAY) + "] ");
			//printDate(calstart.getTime());
			for(int i=0;i<num_members;i++){
				calstart.add(Calendar.MILLISECOND, ENTRY_SWIPE_PERIOD);
				// get the member randomly
				int pos = new Random().nextInt(max_members);
				if (pos % 7 == 0 | pos % 11 == 0 | pos % 13 == 0 | pos % 15 == 0 | pos % 17 == 0 | pos % 19 == 0) Math.min(num_members, pos++);
				
				Member m = mp.get(pos);
				//create entry and compute exit time
				entrystats.add(new StatsEntry(DateStr(calstart),getExitTime(calstart),getClub(m),m.homeclub,m.gender,m.idno));
			}
			nextDateTime(calstart);
		}
		//print the stats
		try {
			
			boolean tosql = true;
			System.out.println("Stats count: " + entrystats.size());
			if(tosql){
				MySQLAccess dao = new MySQLAccess();
				System.out.println("Deleting old stats from MySQL...");
				dao.clearAccessStats();
				System.out.println("Inserting data into MySQL...");
				for(StatsEntry s:entrystats){
					dao.insertAccessStat(s);
					
				}
				dao.close();
			}else{
				FileWriter file = new FileWriter("c:\\stats.csv");
				file.write(StatsEntry.headers());
				for(StatsEntry s:entrystats){
					file.write(s.toCsvString());
				}
				file.flush();
				file.close();
			}
			
	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		StringBuilder stats = new StringBuilder();
//		stats.append(StatsEntry.headers());
//		for(StatsEntry s:entrystats){
//			stats.append(s.toCsvString());
//		}
//		writeToFile(stats.toString(), "c:\\stats.csv");
		System.out.println("Stats count: " + entrystats.size());

	} //end main

	private static String getExitTime(Calendar calstart) {
		setCloseTime(calstart);
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(calstart.getTime());
		tmp.add(Calendar.MILLISECOND, Math.min((new Random().nextInt(120)+5)*60*1000,(int)(closetime.getTimeInMillis() - tmp.getTimeInMillis())));
		return DateStr(tmp);
	}

	private static void nextDateTime(Calendar cal) {
		/*
		 * - if current time is >= 10pm jump to next day 7am
		 * - the increment time is in minutes and is a random num between 1-15
		 * 
		 */
		Calendar tmpCal = Calendar.getInstance();
		tmpCal.setTime(cal.getTime());
	
		int time_increment = new Random().nextInt(15);
//		System.out.println("time-increment: " + time_increment);
//		printDate(cal.getTime());		
		cal.add(Calendar.MINUTE, time_increment);
		//tmpcal - closetime : set the date component of closetime to current date in question
		setCloseTime(cal);
//		printDate(closetime.getTime());
		if(cal.compareTo(closetime) >= 0 ){
			//this is after closing time		
			setOpenTime(cal);
			opentime.add(Calendar.DATE, 1);			
			cal.add(Calendar.MILLISECOND, (int) (opentime.getTimeInMillis() - cal.getTimeInMillis()));
			cal.add(Calendar.SECOND, (new Random().nextInt(60*60)));
			//System.out.println("Jump to open time: ");
		}

		
	} //end

	private static int getMemberEntryCountLimit(Calendar calstart) {
		/*
		 *  breaking up the days into
		 *  - early_morning : 7-10
		 *  - mid-_morning : 10-12
		 *  - afternoon : 12-14:00
		 *  - mid_afternoon : 14:00 - 16:30
		 *  - evening : 16:30 - 9
		 *  - late_evening : 9-10
		 */
		
		switch(calstart.get(Calendar.HOUR_OF_DAY)){
		case 7:case 8:case 9:
			return ENTRY_LIMIT_EARLY_MORNING;			
		case 10:case 11:
			return ENTRY_LIMIT_MID_MORNING;
		case 12:case 13:
			return ENTRY_LIMIT_AFTERNOON;
		case 14:case 16:
			return ENTRY_LIMIT_MID_AFTERNOON;
		case 17:case 18:case 19:case 20:
			return ENTRY_LIMIT_EVENING;
		case 21:case 22:
			return ENTRY_LIMIT_LATE_EVENING;
		}
		return 1;
	}

	private static void setOpenTime(Calendar instance) {
		opentime.set(Calendar.HOUR, 7);
		opentime.set(Calendar.MINUTE, 0);
		opentime.set(Calendar.SECOND, 0);
		opentime.set(Calendar.MILLISECOND, 0);
		opentime.set(Calendar.AM_PM, Calendar.AM);
		opentime.set(Calendar.YEAR, instance.get(Calendar.YEAR));
		opentime.set(Calendar.MONTH, instance.get(Calendar.MONTH));
		opentime.set(Calendar.DATE, instance.get(Calendar.DATE));
	}

	private static void setCloseTime(Calendar instance) {
		closetime.set(Calendar.HOUR, 10);
		closetime.set(Calendar.MINUTE, 0);
		closetime.set(Calendar.SECOND, 0);
		closetime.set(Calendar.MILLISECOND, 0);
		closetime.set(Calendar.AM_PM, Calendar.PM);
		closetime.set(Calendar.YEAR, instance.get(Calendar.YEAR));
		closetime.set(Calendar.MONTH, instance.get(Calendar.MONTH));
		closetime.set(Calendar.DATE, instance.get(Calendar.DATE));		
	}

	private static String getGender() {		
		return new Random().nextBoolean() ? "F" : "M";
	}

	private static String getClub(Member m) {
		if(m != null){
			if(new Random().nextInt(10) <= 7) return m.homeclub;
		}
		return String.valueOf((new Random().nextInt(NUM_OF_CLUBS) + 1) );
	}

	

//	private static void printDate(Date time) {
//		System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(time));
//	}

	private static String DateStr(Calendar cal) {
		return new SimpleDateFormat(DATE_FORMAT).format(cal.getTime());
	}

	static String readWithStringBuffer(Reader fr)
			 throws IOException {
	
			 BufferedReader br = new BufferedReader(fr);
			 String line;
			 StringBuffer result = new StringBuffer();
			 while ((line = br.readLine()) != null) {
			 result.append(line);
			 }
	
			 return result.toString();
	} //end method

	static String readWithStringBuilder(Reader fr)
			 throws IOException {
	
			 BufferedReader br = new BufferedReader(fr);
			 String line;
			 StringBuilder result = new StringBuilder();
			 while ((line = br.readLine()) != null) {
			 result.append(line);
			 }
	
			 return result.toString();
	 } //end method

	static void writeToFile(String data, String filename ){
		try {
			 
			FileWriter file = new FileWriter(filename);
			file.write(data);
			file.flush();
			file.close();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
