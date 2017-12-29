package com.arpitos.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {

	// Database credentials
	String Username = "root";
	String Password = "password";

	// JDBC driver name and database URL
	String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	String DB_URL = "jdbc:mysql://localhost/releasedb";

	Connection conn = null;
	Statement stmt = null;

	public SQL(String userName, String Password, String databaseName) {
		this.Username = userName;
		this.Password = Password;
		this.DB_URL = "jdbc:mysql://localhost/" + databaseName;
	}

	public void connect() throws Exception {
		// Register JDBC driver
		Class.forName("com.mysql.jdbc.Driver");

		// Open a connection
		System.out.println("Connecting to database " + DB_URL);
		conn = DriverManager.getConnection(DB_URL, Username, Password);
	}

	public void disconnect() {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sqlQuery) throws Exception {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sqlQuery);
		return rs;
	}

	public void executeUpdate(String sqlQuery) throws Exception {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sqlQuery);
	}

	public static void main(String[] args) throws Exception {

		SQL sql = new SQL("root", "password", "releasedb");
		sql.connect();
		String sqlQuery = "SELECT id, customer, release_num, release_keyword FROM bom";
		String sqlQuery2 = "INSERT INTO `releasedb`.`bom` (`customer`, `release_num`, `release_keyword`, `release_jira`, `note`) VALUES ('NCR', '1.5.49', 'NA', 'NA', 'NA')";
		ResultSet rs = sql.executeQuery(sqlQuery);
		sql.executeUpdate(sqlQuery2);

		// STEP 5: Extract data from result set
		// while (rs.next()) {
		// // Retrieve by column name
		// int id = rs.getInt("id");
		// String age = rs.getString("customer");
		// String first = rs.getString("release_num");
		// String last = rs.getString("release_keyword");
		//
		// // Display values
		// System.out.print("ID: " + id);
		// System.out.print(", customer: " + age);
		// System.out.print(", release_num: " + first);
		// System.out.println(", relese_keyword: " + last);
		// }
		// rs.close();
	}
}
