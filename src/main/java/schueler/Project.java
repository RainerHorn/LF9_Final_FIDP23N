package schueler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

/**
 * Project
 */
public class Project extends Entity {

    public Project() {}

    public Project(String n) {
        super(n);
    }

    @Override
    public String getCreateStatement() {
        return "INSERT INTO project (name) VALUES (" + this.getId() + "," + this.getName() + ");";
    }

    @Override
    public String getUpdateStatement() {
        return "UPDATE project SET name = \""+this.getName()+"\" WHERE projId = "+this.getId()+";";
    }

    @Override
    public String getDeleteStatement() {
        return "DELETE FROM project WHERE name = projId = "+this.getId()+";";
    }

    @Override
    public String getReadStatement() {
        return "SELECT * FROM project WHERE projId = " + this.getId() + ";";
    }

    @Override
    public String getReadAllStatement() {
        // TODO Auto-generated method stub
        return "SELECT *  FROM project;";
    }

    @Override
    public void setEntity(ResultSet rs) {
        try {
            setId(rs.getInt("projId"));
            setName(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name",this.getName());
        return obj.toString();
    }

    @Override
    public void parseJSON(String jsonString) {
        JSONObject obj = new JSONObject(jsonString);
        this.setName(obj.getString("name"));
    }
}