package db.mysql;

import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import external.TicketMasterClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MySQLConnection implements DBConnection {
	private Connection conn;
	   
	   public MySQLConnection() {
	  	 try {
	  		 Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
	  		 conn = DriverManager.getConnection(MySQLDBUtil.URL);
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 }
	   }


	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (conn != null) {
	  		 try {
	  			 conn.close();
	  		 } catch (Exception e) {
	  			 e.printStackTrace();
	  		 }
	  	 }


	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// TODO Auto-generated method stub
		TicketMasterClient ticketMasterClient = new TicketMasterClient();
	        List<Item> items = ticketMasterClient.search(lat, lon, term);
	
	        for(Item item : items) {
		 saveItem(item);
	        }
	
	        return items;

	}

	@Override
	public void saveItem(Item item) {
		// TODO Auto-generated method stub
		 if (conn == null) {
	  		   System.err.println("DB connection failed");
	  		   return;
	  	         }
	  	
	  	 try {
	  		 String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
	  		 PreparedStatement ps = conn.prepareStatement(sql);
	  		 ps.setString(1, item.getItemId());
	  		 ps.setString(2, item.getName());
	  		 ps.setDouble(3, item.getRating());
	  		 ps.setString(4, item.getAddress());
	  		 ps.setString(5, item.getImageUrl());
	  		 ps.setString(6, item.getUrl());
	  		 ps.setDouble(7, item.getDistance());
	  		 ps.execute();
	  		
	  		 sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
	  		 ps = conn.prepareStatement(sql);
	  		 ps.setString(1, item.getItemId());
	  		 for(String category : item.getCategories()) {
	  			 ps.setString(2, category);
	  			 ps.execute();
	  		 }
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 }


	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return "";
		}		
		String name = "";
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return name;

	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return false;
		}
		try {
			String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;

	}
	
	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		try {
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;	
	}


}
