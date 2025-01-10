package schueler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Project
 */
public class Project extends Entity {

    public Project(String n) {
        super(n);
    }

    @Override
    public String getCreateStatement() {
        return "INSERT INTO project VALUES ("+this.getId()+","+this.getName()+");";
    }

    @Override
    public String getUpdateStatement() {
        return "UPDATE project SET name = \""+this.getName()+"\" WHERE projId = "+this.getId()+";";
    }

    @Override
    public String getDeleteStatement() {
        return "DELETE FROM project WHERE name = \""+this.getName()+"\" AND projId = "+this.getId()+";";
    }

    @Override
    public String getReadStatement() {
        return "SELECT * FROM project WHERE projId = "+this.getId()+";";
    }

    @Override
    public String getReadAllStatement() {
        // TODO Auto-generated method stub
        return "SELECT *  FROM project;";
    }
}