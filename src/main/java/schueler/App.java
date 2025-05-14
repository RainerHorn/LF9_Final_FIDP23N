package schueler;



import org.sqlite.SQLiteConfig;

public class App {
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
           
            
            /*Statement st = c.createStatement();
            Task p = new Task("testTask", sdf.parse("2024-12-10"));
            p.setId(30);
            System.out.println("Read Statement:"+p.getReadStatement());
            ResultSet rs = st.executeQuery(p.getReadStatement());
            p.setEntity(rs);
            System.out.println("dummy");*/

            Project p = new Project("sehr wichtig");
            p.parseJSON("{\"name\":\"MyProject\"}");
            System.out.println("ToJson:"+p.toJSON());

            
            /*Priority p = new Priority(5,"test123");
            st.execute("INSERT INTO priority VALUES (17,5,\"mittel wichtig\");");
            ResultSet rs = st.executeQuery("select * from priority order by id DESC");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getInt(2) + " " + rs.getString(3));
            } */

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
