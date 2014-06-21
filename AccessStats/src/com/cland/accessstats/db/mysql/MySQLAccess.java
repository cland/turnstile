package com.cland.accessstats.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import com.cland.accessstats.StatsEntry;

public class MySQLAccess {
	private static final int TIME_SPENT_IN_VENUE = 60;
	private static final int FUTURE_TIME_OFFSET = 20;
	private static final int TIME_PERIOD_FOR_AVERAGE = 120;
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public void readDataBase() throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost/access_stats?user=root&password=Cland001");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			//      resultSet = statement.executeQuery("SELECT gender,count(*) as total from stats group by gender");
			//      writeResultSet(resultSet);
			/*
      // PreparedStatements can use variables and are more efficient
      preparedStatement = connect.prepareStatement("insert into  FEEDBACK.COMMENTS values (default, ?, ?, ?, ? , ?, ?)");
      // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
      // Parameters start with 1
      preparedStatement.setString(1, "Test");
      preparedStatement.setString(2, "TestEmail");
      preparedStatement.setString(3, "TestWebpage");
      preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
      preparedStatement.setString(5, "TestSummary");
      preparedStatement.setString(6, "TestComment");
      preparedStatement.executeUpdate();
			 */
			preparedStatement = connect.prepareStatement("SELECT gender as col1,count(*) as total from stats2 group by gender");
			resultSet = preparedStatement.executeQuery();
			writeResultSet(resultSet);
			/*
      // Remove again the insert comment
      preparedStatement = connect.prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
      preparedStatement.setString(1, "Test");
      preparedStatement.executeUpdate();
			 */      
			resultSet = statement.executeQuery("select * from stats2");
			writeMetaData(resultSet);

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	public boolean isConnected(){	  
		try {
			if(connect != null){
				if(connect.isValid(0)){
					return true;
				}
			} //end if
			//Otherwise start a new connections
			// This will load the MySQL driver, each DB has its own driver  
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost/access_stats?user=root&password=Cland001");

			//done successfully otherwise an error would have been thrown
			return true;
		} catch (ClassNotFoundException e) {		
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		return false;
	} //end method isConnected
	public boolean insertAccessStat(StatsEntry stats) throws SQLException{
		boolean resultSet = false;	 
		if (isConnected()){			
			String sql = "INSERT INTO stats2 (entry_datetime, exit_datetime, access_club, home_club, gender, memberid) VALUES (?,?,?,?,?,? )";			
			preparedStatement = connect.prepareStatement(sql);
			preparedStatement.setString(1, stats.getEntry_datetime());
			preparedStatement.setString(2, stats.getExit_datetime());
			preparedStatement.setString(3, stats.getAccess_club());
			preparedStatement.setString(4, stats.getHome_club());
			preparedStatement.setString(5, stats.getGender());
			preparedStatement.setInt(6, stats.getMemberId());
			resultSet = preparedStatement.execute();		
		}
		return resultSet;
	} //end method

	public boolean clearAccessStats() throws SQLException{
		boolean resultSet = false;	 
		if (isConnected()){			
			preparedStatement = connect.prepareStatement("delete from stats2");
			resultSet = preparedStatement.execute();
		}
		return resultSet;	
	}
	
	public void testAccessStats(){
		if (isConnected()){
			try {
				//Number of people in the club at this point in time.
				resultSet = getNumberOfMembersByClub("2011-08-22 09:40:43", TIME_SPENT_IN_VENUE);
				System.out.println("Number of members by club: ");
				writeResultSet(resultSet);
				
				//Number of people in the club at this point in time.
				resultSet = getNumberOfMembersInClubByGender("2011-08-22 09:40:43", TIME_SPENT_IN_VENUE);
				System.out.println("Number of members in club: ");
				writeResultSet(resultSet);
	
				//Number of people who might leave in he next y min
				resultSet = getNumberLeavingInFuture("2011-08-22 09:40:43", TIME_SPENT_IN_VENUE, FUTURE_TIME_OFFSET);
				System.out.println("\nNumber of members leaving soon: ");
				writeResultSet(resultSet);
	
				//Ave estimate number entering per min in the last hour
				resultSet = getEstimateNumberOfEntered("2011-08-22 09:40:43", TIME_SPENT_IN_VENUE, TIME_PERIOD_FOR_AVERAGE);
				System.out.println("\nEstimate Number entering per min: ");
				writeResultSet(resultSet);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				close();
			}
	
		}
	} //end method testAccessStats

	private ResultSet getNumberOfMembersInClubByGender(String timeNow, int period) throws SQLException{
		ResultSet resultSet = null;

		preparedStatement = connect.prepareStatement(
				"select  gender,access_club as club, count(*) as total from stats2 where entry_datetime <= ? and ? <= DATE_ADD(entry_datetime, INTERVAL ? MINUTE) group by gender,club;"
				);

		preparedStatement.setString(1, timeNow);
		preparedStatement.setString(2, timeNow);
		preparedStatement.setInt(3, period);

		resultSet = preparedStatement.executeQuery();
		return resultSet;
	}//end method
	private ResultSet getNumberOfMembersByClub(String timeNow, int period) throws SQLException{
		ResultSet resultSet = null;

		preparedStatement = connect.prepareStatement(
				"select access_club as club,gender, count(*) as total from stats2 where entry_datetime <= ? and ? <= DATE_ADD(entry_datetime, INTERVAL ? MINUTE) group by club,gender;"
				);

		preparedStatement.setString(1, timeNow);
		preparedStatement.setString(2, timeNow);
		preparedStatement.setInt(3, period);

		resultSet = preparedStatement.executeQuery();
		return resultSet;
	}//end method
	private ResultSet getNumberLeavingInFuture(String timeNow, int period, int offset) throws SQLException{
		/*
		 * -- How many members would have left in the next y minutes from now.
		 * -- (ta + P) >= tn and (ta + P) <= tn + y
		 * -- let y=20min, P=60min, tn='2013-04-22 05:50:00'
		 * example:
		 * 
		 * select gender, count(*) from stats 
		 * where 
		 * DATE_ADD(accessdate, INTERVAL 60 MINUTE) >= '2013-04-22 05:50:00' 
		 * and 
		 * DATE_ADD(accessdate, INTERVAL 60 MINUTE) <= DATE_ADD('2013-04-22 05:50:00', INTERVAL 20 MINUTE)
		 * group by gender;
		 */
		ResultSet resultSet = null;

		preparedStatement = connect.prepareStatement(
				"select access_club as club, gender, count(*) as total from stats2 where DATE_ADD(entry_datetime, INTERVAL ? MINUTE) >= ? and DATE_ADD(entry_datetime, INTERVAL ? MINUTE) <= DATE_ADD(?, INTERVAL ? MINUTE) group by club,gender;"
				);
		preparedStatement.setInt(1, period);
		preparedStatement.setString(2, timeNow);
		preparedStatement.setInt(3, period);
		preparedStatement.setString(4, timeNow);
		preparedStatement.setInt(5, offset);

		resultSet = preparedStatement.executeQuery();
		return resultSet;
	}//end method

	private ResultSet getEstimateNumberOfEntered(String timeNow, int period, int aveperiod) throws SQLException{
		/*
		 * -- plus estimate number of members who entered in the same period y minutes:
		 * -- The ave number members entering per minute in the last hour:
		 * -- So what is the ave number of people who entered per min in the last hour?
		 * select gender, round(count(*)/60) as total  from stats
		 * where
		 * DATE_SUB('2013-04-22 05:50:00', INTERVAL 60 MINUTE) <= accessdate and accessdate <= '2013-04-22 05:50:00' group by gender;
		 */

		ResultSet resultSet = null;	 

		preparedStatement = connect.prepareStatement(
				"select access_club as club,gender, round(count(*)/?) as total from stats2 where DATE_SUB(?, INTERVAL ? MINUTE) <= entry_datetime and entry_datetime <= ? group by club,gender;"
				);

		preparedStatement.setInt(1, aveperiod);
		preparedStatement.setString(2, timeNow);
		preparedStatement.setInt(3, period);
		preparedStatement.setString(4, timeNow);


		resultSet = preparedStatement.executeQuery();
		return resultSet;
	}//end method

	private void writeMetaData(ResultSet resultSet) throws SQLException {
		//   Now get some metadata from the database
		// Result set get the result of the SQL query

		System.out.println("The columns in the table are: ");

		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
			System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
		}
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set
		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			String gender = resultSet.getString("gender");
			String club = resultSet.getString("club");
			String total = resultSet.getString("total");
			System.out.println("club: " +club + " - Gender: "+ gender + "  Total: " + total);


		}
	} //end method

	// You need to close the resultSet
	public void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	} //end method Close

} //end class 
