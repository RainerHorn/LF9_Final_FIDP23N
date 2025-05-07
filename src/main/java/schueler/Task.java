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
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static Connection c = null;

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
        
        if ((this.date != null)) { // I would not think that we still need this, but if I remove it the code breaks
            if ((this.priority != null)) {
                if ((this.project != null)) {
                    return "INSERT INTO task (title,date,proId,priId) VALUES (\"" + this.getName() + "\",\""+sdf.format(this.getDate())+"\","+this.getProject().getId()+","+this.getPriority().getId()+");";
                } else {
                    return "INSERT INTO task (title,date,priId) VALUES (\"" + this.getName() + "\",\""+sdf.format(this.getDate())+"\","+this.getPriority().getId()+");";
                }
            } else {
                if ((this.project != null)) {
                    return "INSERT INTO task (title,date,proId) VALUES (\"" + this.getName() + "\",\""+sdf.format(this.getDate())+"\","+this.getProject().getId()+");";
                } else {
                    return "INSERT INTO task (title,date) VALUES (\"" + this.getName() + "\",\""+sdf.format(this.getDate())+"\");";
                }
            }
        } else {
            if ((this.priority != null)) {
                if ((this.project != null)) {
                    return "INSERT INTO task (title,proId,priId) VALUES (\"" + this.getName() + "\","+this.getProject().getId()+","+this.getPriority().getId()+");";
                } else {
                    return "INSERT INTO task (title,priId) VALUES (\"" + this.getName() + "\","+this.getPriority().getId()+");";
                }
            } else {
                return "INSERT INTO task (title,proId) VALUES (\"" + this.getName() + "\","+this.getProject().getId()+");";
            }
        }
    }

    @Override
    public String getUpdateStatement() {
        String tmpResponse = "UPDATE task SET title = \"" + this.getName() + "\", date = " + sdf.format(this.getDate()) + ", proId = "+this.getProject().getId()+", priId = "+this.getPriority()+getId()+" WHERE id = "+ this.getId() + ";";
        return tmpResponse.replaceAll("null[0-9]+","NULL");
    }

    @Override
    public String getDeleteStatement() {
        return "DELETE * FROM task WHERE title = \"" + this.getName() + "\" AND id = " + this.getId() + ";";
    }

    @Override
    public String getReadStatement() {
        return "SELECT * FROM task WHERE id = " + this.getId() + ";";
    }

    @Override
    public String getReadAllStatement() {
        return "SELECT * FROM task;";
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
            Connection c = this.getDBConnection();

            Statement st = c.createStatement();
            ResultSet rsProj = st.executeQuery("SELECT * FROM project WHERE projId = "+projId);
            Project pro = new Project(rsProj.getString("name"));
            setProject(pro);

            Statement st2 = c.createStatement();
            int prioId = rs.getInt("priId");
            ResultSet rsPrio = st2.executeQuery("SELECT * FROM priority WHERE id = "+prioId);
            Priority prio = new Priority(rsPrio.getInt("value"),rsPrio.getString("description"));
            setPriority(prio);

            rsProj.close();
            rsPrio.close();
            st.close();
            st2.close();
            c.close();
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
        try {
            obj.put("proId",this.getProject().getId());
        } catch (Exception e) {obj.put("proId",JSONObject.NULL);}
        try {
            obj.put("priId",this.getPriority().getId());
        } catch (Exception e) {obj.put("priId",JSONObject.NULL);}
        return obj.toString();
    }

    @Override
    public void parseJSON(String jsonString) {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            Connection c = this.getDBConnection();


            System.out.println(jsonString);
            JSONObject obj = new JSONObject(jsonString);

            this.setName(obj.getString( "title"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.setDate(sdf.parse(obj.getString("date")));
        
            try {
                Integer projId = obj.getInt("proId");
                Statement st = c.createStatement();
                ResultSet rsProj = st.executeQuery("SELECT * FROM project WHERE projId = "+projId);
                Project proj = new Project(rsProj.getString("name"));
                proj.setId(projId);
                this.setProject(proj);
                rsProj.close();
                st.close();
            } catch(Exception e) {}
            
            try {
                Integer prioId = obj.getInt("priId");
                Statement st2 = c.createStatement();
                ResultSet rsPrio = st2.executeQuery("SELECT * FROM priority WHERE id = "+prioId);
                Priority prio = new Priority(rsPrio.getInt("value"),rsPrio.getString("description"));
                prio.setId(prioId);
                this.setPriority(prio);
                rsPrio.close();
                st2.close();
            } catch(Exception e) {}
            
            c.close();
        }
        catch (SQLException e) {
            System.out.println("DATABASE ERROR\n");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection getDBConnection() {
        try {
            if (c==null) {
                Class.forName("org.sqlite.JDBC");
                SQLiteConfig config = new SQLiteConfig();
                config.enforceForeignKeys(true);
                c = DriverManager.getConnection("jdbc:sqlite:todo.db", config.toProperties());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

}
