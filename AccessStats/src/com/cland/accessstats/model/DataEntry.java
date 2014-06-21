package com.cland.accessstats.model;

public class DataEntry {
	/**
	 * 
	 */
	protected String name;
	protected String gpsLocation;
	protected String lastDateTime;
	protected String activityLevel;
	protected String activityFutureLevel;
	protected String activityFuturePeriod;
	protected String adultCapacityLevel;
	protected String adultCapacityLevelMale;
	protected String adultCapacityLevelFemale;
	protected String childCapacityLevel;
	protected String travelTime;
	protected String distance;
	protected String routeJson;
	protected int routeColor;
	
	public DataEntry(){
		this.name = "unknown";
		this.gpsLocation = "";
		this.activityLevel = "";
		this.activityFutureLevel = "";
		this.activityFuturePeriod = "";
		this.adultCapacityLevel = "";
		this.adultCapacityLevelMale = "";
		this.adultCapacityLevelFemale = "";
		this.childCapacityLevel = "";	
		this.lastDateTime = "";
		this.distance = "--km";
		this.travelTime = "--hrs";
	}

	public DataEntry(String name, String gpsLocation, String activityLevel,
			String activityFutureLevel, String activityFuturePeriod,
			String adultCapacityLevel, String adultCapacityLevelMale,
			String adultCapacityLevelFemale, String childCapacityLevel, String lastDateTime) {
		super();
		this.name = name;
		this.gpsLocation = gpsLocation;
		this.activityLevel = activityLevel;
		this.activityFutureLevel = activityFutureLevel;
		this.activityFuturePeriod = activityFuturePeriod;
		this.adultCapacityLevel = adultCapacityLevel;
		this.adultCapacityLevelMale = adultCapacityLevelMale;
		this.adultCapacityLevelFemale = adultCapacityLevelFemale;
		this.childCapacityLevel = childCapacityLevel;
		this.lastDateTime = lastDateTime;
		this.distance = "--km";
		this.travelTime = "--hrs";
	} //end constructor

	public double getLatitude(){		
		return Double.valueOf(getGpsLocation().substring(0,getGpsLocation().indexOf(",")).trim());
	}

	public double getLongitude(){	
		return Double.valueOf(getGpsLocation().substring(getGpsLocation().indexOf(",")+1).trim());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGpsLocation() {
		return gpsLocation;
	}

	public void setGpsLocation(String gpsLocation) {
		this.gpsLocation = gpsLocation;
	}

	public String getActivityLevel() {
		return activityLevel;
	}

	public void setActivityLevel(String activityLevel) {
		this.activityLevel = activityLevel;
	}

	public String getActivityFutureLevel() {
		return activityFutureLevel;
	}

	public void setActivityFutureLevel(String activityFutureLevel) {
		this.activityFutureLevel = activityFutureLevel;
	}

	public String getActivityFuturePeriod() {
		return activityFuturePeriod;
	}

	public void setActivityFuturePeriod(String activityFuturePeriod) {
		this.activityFuturePeriod = activityFuturePeriod;
	}

	public String getAdultCapacityLevel() {
		return adultCapacityLevel;
	}

	public void setAdultCapacityLevel(String adultCapacityLevel) {
		this.adultCapacityLevel = adultCapacityLevel;
	}

	public String getAdultCapacityLevelMale() {
		return adultCapacityLevelMale;
	}

	public void setAdultCapacityLevelMale(String adultCapacityLevelMale) {
		this.adultCapacityLevelMale = adultCapacityLevelMale;
	}

	public String getAdultCapacityLevelFemale() {
		return adultCapacityLevelFemale;
	}

	public void setAdultCapacityLevelFemale(String adultCapacityLevelFemale) {
		this.adultCapacityLevelFemale = adultCapacityLevelFemale;
	}

	public String getChildCapacityLevel() {
		return childCapacityLevel;
	}

	public void setChildCapacityLevel(String childCapacityLevel) {
		this.childCapacityLevel = childCapacityLevel;
	}

	public String getLastDateTime() {
		return lastDateTime;
	}

	public void setLastDateTime(String lastDateTime) {
		this.lastDateTime = lastDateTime;
	}

	public String getDistance() {
		return distance;
	}

	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getTravelTime() {		
		return travelTime;
	}

	public String getRouteJson() {
		return routeJson;
	}

	public void setRouteJson(String routeJson) {
		this.routeJson = routeJson;
	}

	public int getRouteColor() {
		return routeColor;
	}

	public void setRouteColor(int routeColor) {
		this.routeColor = routeColor;
	}
}//End class
