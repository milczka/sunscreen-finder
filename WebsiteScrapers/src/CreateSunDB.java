import java.sql.*;

public class CreateSunDB{
    public static void main(String [] args){
        Connection connection = null;
        Statement statement = null;
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ss.db");

            statement = connection.createStatement();
            String sql = "CREATE TABLE SS_INFO(" +
                    "LINK           TEXT," +
                    "NAME           TEXT," +
                    "BRAND          TEXT," +
                    "INGREDIENTS    TEXT," +
                    "SPF            INT," +
                    "CHEMICAL       INT," +
                    "PHYSICAL       INT," +
                    "STABLE         INT)";
            statement.executeUpdate(sql);
            statement.close();
            connection.close();

        } catch(Exception e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
