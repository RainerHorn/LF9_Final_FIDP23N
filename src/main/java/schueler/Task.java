package schueler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.sqlite.SQLiteConfig;

public class Task extends Entity {

    private Priority priority;
    private Project project;
    private Date date;

    public Task() {}

    public Task(String title, Date d) {
        super(title);
        date = d;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return super.getName();
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String getCreateStatement() {
        return "INSERT INTO task (title) VALUES (\"" + this.getName() + "\");";
    }

    @Override
    public String getUpdateStatement() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "UPDATE task SET name = \"" + this.getName() + "\", date = " + sdf.format(this.getDate()) + " WHERE id = "
                + this.getId() + ";";
    }

    @Override
    public String getDeleteStatement() {
        return "DELETE * FROM task WHERE name = \"" + this.getName() + "\" AND id = " + this.getId() + ";";
    }

    @Override
    public String getReadStatement() {
        return "SELECT * FROM task WHERE id = " + this.getId() + ";";
    }

    @Override
    public String getReadAllStatement() {
        return "SELECT *  FROM task;";
    }

    @Override
    public void setEntity(ResultSet rs) {
        try {
            setId(rs.getInt("id"));
            setName(rs.getString(2));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            setDate(sdf.parse(rs.getString("date")));

            //Project
            int projId = rs.getInt("proId");
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection c = DriverManager.getConnection("jdbc:sqlite:todo.db", config.toProperties());

            Statement st = c.createStatement();
            ResultSet rsProj = st.executeQuery("SELECT * FROM project WHERE projId = "+projId);
            Project pro = new Project(rsProj.getString("name"));
            setProject(pro);

            Statement st2 = c.createStatement();
            int prioId = rs.getInt("priId");
            ResultSet rsPrio = st2.executeQuery("SELECT * FROM priority WHERE id = "+prioId);
            Priority prio = new Priority(rsPrio.getInt("value"),rsPrio.getString("description"));
            setPriority(prio);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("title",this.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        obj.put("date",sdf.format(this.getDate()));
        obj.put("proId",this.getProject().getId());
        obj.put("priId",this.getPriority().getId());
        return obj.toString();
    }

    @Override
    public void parseJSON(String jsonString) {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection c = DriverManager.getConnection("jdbc:sqlite:todo.db", config.toProperties());
            
            JSONObject obj = new JSONObject(jsonString);

            this.setName(obj.getString( "title"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.setDate(sdf.parse(obj.getString("date")));
        
            Integer projId = obj.getInt("proId");
            Statement st = c.createStatement();
            ResultSet rsProj = st.executeQuery("SELECT * FROM project WHERE projId = "+projId);
            Project proj = new Project(rsProj.getString("name"));
            this.setProject(proj);
            
            Integer prioId = obj.getInt("priId");
            Statement st2 = c.createStatement();
            ResultSet rsPrio = st2.executeQuery("SELECT * FROM priority WHERE id = "+prioId);
            Priority prio = new Priority(rsPrio.getInt("value"),rsPrio.getString("description"));
            this.setPriority(prio);
        }
        catch (SQLException e) {
            System.out.println("DATABASE ERROR\n");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
