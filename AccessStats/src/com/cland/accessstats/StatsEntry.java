package com.cland.accessstats;

public class StatsEntry {
	String entry_datetime;
	String exit_datetime;
	String access_club;
	String home_club;
	String gender;
	int memberId;
	public StatsEntry(String entry_datetime, String exit_datetime,
			String access_club, String home_club, String gender, int idno) {
		super();
		this.entry_datetime = entry_datetime;
		this.exit_datetime = exit_datetime;
		this.access_club = access_club;
		this.home_club = home_club;
		this.gender = gender;
		this.memberId = idno;
	}
	public static String headers(){
		return "entry_datetime,exit_datetime,access_club,home_club,gender,memberid\n";
	}
	public String toCsvString() {
		//entry_datetime,exit_datetime,access_club,home_club,gender,memberid
		return entry_datetime + "," 
		+ exit_datetime + "," 
		+ access_club + "," 
		+ home_club + ","
		+ gender + "," 
		+ memberId +"\n";
	}
	
	public String getEntry_datetime() {
		return entry_datetime;
	}
	public void setEntry_datetime(String entry_datetime) {
		this.entry_datetime = entry_datetime;
	}
	public String getExit_datetime() {
		return exit_datetime;
	}
	public void setExit_datetime(String exit_datetime) {
		this.exit_datetime = exit_datetime;
	}
	public String getAccess_club() {
		return access_club;
	}
	public void setAccess_club(String access_club) {
		this.access_club = access_club;
	}
	public String getHome_club() {
		return home_club;
	}
	public void setHome_club(String home_club) {
		this.home_club = home_club;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	@Override
	public String toString() {
		return "StatsEntry [entry_datetime=" + entry_datetime
				+ ", exit_datetime=" + exit_datetime + ", access_club="
				+ access_club + ", home_club=" + home_club + ", gender="
				+ gender + ", memberId=" + memberId + "]";
	}
	
	
}
