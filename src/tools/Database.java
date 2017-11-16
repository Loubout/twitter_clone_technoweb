package tools;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/** Manipulation de la base SQL */

public class Database {
	private DataSource dataSource;
	public Database(String jndiname) throws SQLException{
		try {
			dataSource = (DataSource) new InitialContext().lookup("java:comp/env/" +
					jndiname);
		} catch (NamingException e) {
			throw new SQLException();

		}
	}


	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
