package com.cland.accessstats;

import com.cland.accessstats.db.mysql.MySQLAccess;

public class StatsService {	
		  public static void main(String[] args) throws Exception {
		    MySQLAccess dao = new MySQLAccess();
		    dao.testAccessStats();
		  }
} //end class
