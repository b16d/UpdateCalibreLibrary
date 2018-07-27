package com.acl.updater.database;


import com.acl.updater.database.datamode.Comments;
import com.acl.updater.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetadataDatabaseUpdater implements DatabaseUpdater {

    private static final Logger LOGGER = LogManager.getLogger(MetadataDatabaseUpdater.class);

    private static final String ID = "id";
    private static final String BOOK = "book";
    private static final String TEXT = "text";
    private static final String UPDATE_COMMENTS_SET = "UPDATE comments SET ";
    private static final String WHERE = "WHERE ";
    private static final String SELECT_REQUEST = "select * from comments";

    //Connection to SQL database
    private Connection connection;

    //Path to the metadata
    private String filePath;

    public MetadataDatabaseUpdater(String filePath) {
        this.filePath = filePath;
    }

    public void updateData() {
        if (connect()) {

            List<Comments> commentsList = getComments();

            updateComments(commentsList);

            disconnect();
        } else {
            LOGGER.info("Do nothing can't be connected to the database");
        }

    }


    /**
     * Connection to the databse, return true if it's ok
     * @return true connection ok
     */
    private boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + filePath + "\\metadata.db");
            connection.setAutoCommit(false);

            LOGGER.info("Connect to the database");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("Can't connect to database ", e);
            return false;
        }
        return true;
    }

    /**
     * Close connection to the database
     */
    private void disconnect() {
        try {
            LOGGER.info("Close database connection");
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Can't disconnect to database, ", e);
        }
    }

    /**
     *  Return comments table
     * @return a list of comments
     */
    private List<Comments> getComments() {
        Statement stmt;
        List<Comments> commentsList = new ArrayList<>();
        try {
            stmt = connection.createStatement();

            LOGGER.info("Executing request: {}", SELECT_REQUEST);
            ResultSet rs = stmt.executeQuery(SELECT_REQUEST );
            LOGGER.info("Request result: {}", rs);

            while (rs.next()) {
                int id = rs.getInt(ID);
                int book = rs.getInt(BOOK);
                String comment  = rs.getString(TEXT);

                commentsList.add(new Comments(id, book, comment));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e ) {
            LOGGER.error("Error, ", e);
            return null;
        }
        LOGGER.info("Return comments size: {}", commentsList.size());
        return commentsList;
    }

    /**
     * Update comments table with a list a comment who must be updated
     * @param commentsList list a comments ta update
     */
    private void updateComments (final List<Comments> commentsList) {
        try {
            LOGGER.info("Number of elements that will be updated: {}", commentsList.size());
            Statement stmt = connection.createStatement();

            for(Comments comments : commentsList) {

                String text =  Utils.html2text(comments.getText().replace("\'", ""));
                String sql = UPDATE_COMMENTS_SET + TEXT + "=\'" + text + "\'"+
                        WHERE + ID + "=" + comments.getId();

                LOGGER.info("Going to play SQL request: {}", sql);
                stmt.executeUpdate(sql);
                connection.commit();
            }
            stmt.close();
        } catch (Exception e) {
            LOGGER.error("Impossible to close database connection, ", e);
        }
    }

    public static void main(String[] args) {

        String filePath = "C:\\Users\\alban\\IdeaProjects\\EpubChanger\\src\\test\\ressources\\";

        MetadataDatabaseUpdater dbConnection = new MetadataDatabaseUpdater(filePath);

        if (dbConnection.connect()) {
            List<Comments> commentsList = dbConnection.getComments();
            if (commentsList != null) {
                for (Comments comments : commentsList) {
                    System.out.println(comments);
                    comments.setText(Utils.html2text(comments.getText()));
                }

                dbConnection.updateComments(commentsList);
            }
        }
    }
}
