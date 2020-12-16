package pers.giyn.LiveChat.server.database;

import pers.giyn.LiveChat.server.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author 许继元
 */
public class MySqlLoader {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "password";

    public static final String STANDARD_TABLE =
            "CREATE TABLE IF NOT EXISTS `userInfo`(" +
                    "`id` VARCHAR(20) NOT NULL, " +
                    "`password` VARCHAR(100), " +
                    "`nickName` VARCHAR(20), " +
                    "PRIMARY KEY ( `id` )" +
                    ");";

    public static final String STANDARD_FRIEND_TABLE =
            "CREATE TABLE IF NOT EXISTS `friendList`(" +
                    "`srcid` VARCHAR(20) NOT NULL, " +
                    "`dstid` VARCHAR(20) NOT NULL " +
                    ");";

    public static final String STANDARD_INSERT_FRIEND_STRING = "insert into friendList values(\"%s\",\"%s\");";
    public static final String STANDARD_SEARCH_FRIEND_STRING = "SELECT distinct f1.dstId FROM friendlist as f1 INNER JOIN friendlist as f2 ON f1.srcId = f2.dstId AND f2.srcId = f1.dstId WHERE f1.srcId = \"%s\";";
    public static final String STANDARD_SEARCH_APPLY_FRIEND_STRING = "SELECT distinct  * from (select friendlist.srcid, friendlist.dstid FROM friendlist LEFT JOIN " +
            "(SELECT f1.srcId, f1.dstId FROM friendlist as f1 INNER JOIN friendlist as f2 ON f1.srcId = f2.dstId AND f2.srcId = f1.dstId) as t1 " +
            "ON friendlist.dstid = t1.dstid AND friendlist.srcid = t1.srcid where t1.srcid IS NULL) as t2 where t2.dstid = \"%s\";";
    public static final String STANDARD_DELETE_FRIEND_STRING = "DELETE FROM friendlist WHERE dstId = \"%s\" and srcId = \"%s\";";

    public Connection connection = null;
    public Statement statement = null;

    public MySqlLoader() throws ClassNotFoundException, SQLException {
        this.init();
    }

    public void init() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
        this.statement = this.connection.createStatement();
        this.selfCheck();
    }

    public void selfCheck() {
        boolean isExist;
        isExist = true;

        try {
            this.statement.executeQuery("use LiveChat;");
        } catch (SQLException se) {
            if (se.getErrorCode() == 1049) {
                isExist = false;
            }
        }

        if (!isExist) {
            try {
                this.statement.executeQuery("create database LiveChat;");
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        try {
            this.statement.executeQuery("use LiveChat;");
            this.statement.execute(STANDARD_TABLE);
            this.statement.execute(STANDARD_FRIEND_TABLE);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void loadUsers(Map<String, User> dst) {
        try {
            ResultSet rs = this.statement.executeQuery("SELECT * FROM userinfo;");
            while (rs.next()) {
                dst.put(rs.getString("id"), new User(rs.getString("id"), rs.getString("password"), rs.getString("nickName")));
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void insertUsers() {
    }

    public void connectFriend(String srcId, String dstId) {
        try {
            this.statement.execute(String.format(STANDARD_INSERT_FRIEND_STRING, srcId, dstId));
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void deleteFriend(String srcId, String dstId) {
        try {
            this.statement.execute(String.format(STANDARD_DELETE_FRIEND_STRING, srcId, dstId));
            this.statement.execute(String.format(STANDARD_DELETE_FRIEND_STRING, dstId, srcId));
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public ArrayList<String> searchFriend(String srcId) {
        ArrayList<String> res = new ArrayList<>();
        try {
            ResultSet rs = this.statement.executeQuery(String.format(STANDARD_SEARCH_FRIEND_STRING, srcId));
            while (rs.next()) {
                res.add(rs.getString("dstid"));
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return res;
    }

    public ArrayList<String> searchApplyFriend(String srcId) {
        ArrayList<String> res = new ArrayList<>();
        try {
            ResultSet rs = this.statement.executeQuery(String.format(STANDARD_SEARCH_APPLY_FRIEND_STRING, srcId));
            while (rs.next()) {
                res.add(rs.getString("srcid"));
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return res;
    }

    public void close() throws SQLException {
        this.statement.close();
        this.connection.close();
    }
}
