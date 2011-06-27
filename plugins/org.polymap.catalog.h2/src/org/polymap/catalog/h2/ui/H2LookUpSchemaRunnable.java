package org.polymap.catalog.h2.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.catalog.service.database.LookUpSchemaRunnable;
import net.refractions.udig.catalog.service.database.TableDescriptor;
import net.refractions.udig.core.Pair;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A runnable that looks up all the schemas in the provided database using the
 * provided username and password
 * 
 * @since 3.1
 */
public class H2LookUpSchemaRunnable 
        implements LookUpSchemaRunnable {

    private final String               host;

    private final int                  port;

    private final String               username;

    private final String               password;

    private final String               database;

    private final Set<TableDescriptor> tables = new HashSet<TableDescriptor>();

    private volatile String            error;

    private volatile boolean           ran;


    public H2LookUpSchemaRunnable( String host, int port, String username, String password,
            String database ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public void run( IProgressMonitor monitor ) {
        //MySQLConnectionFactory conFac = new MySQLConnectionFactory(host, port, database);
        //conFac.setLogin(username, password);
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName(host);
        ds.setPort(port);
        ds.setDatabaseName(database);
        ds.setUser(username);
        ds.setPassword(password);
        DriverManager.setLoginTimeout(3);

        try {
            loadTableDescriptrs(ds);

        } catch (SQLException e) {
            error = "Unrecognized connection failure.  Check parameters and database.";
        }
        ran = true;
    }

    private void loadTableDescriptrs( MysqlDataSource ds ) throws SQLException {
    	MySqlDialect dialect = new MySqlDialect();
        Connection connection = ds.getConnection(username, password);
        try {

            Statement statement = connection.createStatement();
            if (statement
                    .execute("SHOW TABLES;")) { //$NON-NLS-1$
                ResultSet resultSet = statement.getResultSet();
                while( resultSet.next() ) {
                    
                    String table = resultSet.getString("Tables_in_"+database); //$NON-NLS-1$
                    Pair<String, Pair<String, String>> results =  lookupGeometryColumn(table,
                             connection);
                    if (results != null) {
                        String geometryColumn = results.getLeft();
                        String geometryType = results.getRight().getLeft();
                        System.out.println( "geometryType: " + geometryType );
                        String srid = results.getRight().getRight();
                        tables.add(new TableDescriptor(table, /*dialect.toGeomClass(*/geometryType, database,
                                geometryColumn, srid,false));
                    }

                }
            }
            statement.close();
        } catch (SQLException e) {
            error = "An error occurred when querying the database about the data it contains. Please talk to the administrator: "
                    + e.getMessage();
        } finally {
            connection.close();
        }
    }

    private Pair<String, Pair<String, String>> lookupGeometryColumn( String table,
            Connection connection ) throws SQLException {

        Statement statement = connection.createStatement();
        try {

            String sql = "SHOW FIELDS FROM " + table + " ;";
            if (statement.execute(sql)) {
                ResultSet results = statement.getResultSet();
                while (results.next()) {
                    String type = results.getString("Type");
                    // _p3: all geometry types added
                	if (type.equalsIgnoreCase("geometry") 
                	        || type.equalsIgnoreCase( "point" ) || type.equalsIgnoreCase( "multipoint" )
                	        || type.equalsIgnoreCase( "linestring" ) || type.equalsIgnoreCase( "polygon" )
                	        || type.equalsIgnoreCase( "multilinestring" ) || type.equalsIgnoreCase( "multipolygon" )) {
                	    Pair<String, String> typeSrid = new Pair<String, String>(
                	            results.getString("Type"), "4362"); // assumed srid
                	    String geom = results.getString("Field");
                	    Pair<String, Pair<String, String>> all = new Pair<String, Pair<String, String>>(
                	            geom, typeSrid);
                	    return all;
                	}
                }
            }

            return null;
        } finally {
            statement.close();
        }
    }
    /**
     * Returns null if the run method was able to connect to the database otherwise will return a
     * message indicating what went wrong.
     * 
     * @return null if the run method was able to connect to the database otherwise will return a
     *         message indicating what went wrong.
     * @throws IllegalStateException if called before run.
     */
    public String getError() throws IllegalStateException {
        if (!ran) {
            throw new IllegalStateException(
                    "run must complete running before this method is called.");
        }
        return error;
    }

    /**
     * Returns the names of the databases in the database that this object connected to when the run
     * method was executed.
     * 
     * @return the names of the databases in the database that this object connected to when the run
     *         method was executed.
     */
    public Set<TableDescriptor> getSchemas() {
        return tables;
    }

}
