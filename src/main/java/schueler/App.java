package schueler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.SQLiteConfig;

public class App {
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection c = DriverManager.getConnection("jdbc:sqlite:todo_klein_org.db", config.toProperties());

            Priority p = new Priority(5,"mittel wichtig");
            
            Statement st = c.createStatement();
            st.execute(p.getCreateStatement());
            ResultSet rs = st.executeQuery("select * from priority order by id DESC");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3));
            }
            /*
            while (rs.next()) {
                p.setId(rs.getInt(1));
            }
            p.setValue(10);
            st.execute(p.getUpdateStatement());
            st.execute(p.getDeleteStatement());*/
        } catch (ClassNotFoundException e) {
            System.out.println("Treiber nicht gefunden");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
