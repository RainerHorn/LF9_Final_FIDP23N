package schueler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

public class Priority extends Entity {

    private int value;

    public Priority(){ }

    public Priority(int value, String name) {
        super(name);
        this.setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value < 0)
            value = 0;
        this.value = value;
    }

    @Override
    public String getCreateStatement() {
        return "INSERT INTO priority (value,description) VALUES (" + this.getValue() + ",\"" + this.getName() + "\");";
    }

    @Override
    public String getUpdateStatement() {
        return "UPDATE Priority SET value = " + this.getValue() + ", description = \"" + this.getName()
                + "\" WHERE id = '" + this.getId() + "';";
    }

    @Override
    public String getDeleteStatement() {
        return "DELETE FROM Priority WHERE id = '" + this.getId() + "';";
    }

    @Override
    public String getReadStatement() {
        return "SELECT * FROM Priority WHERE id = '" + this.getId() + "';";
    }

    @Override
    public String getReadAllStatement() {
        return "SELECT * FROM Priority;";
    }

    @Override
    public void setEntity(ResultSet rs) {
        try {
            setId(rs.getInt("id"));
            System.out.println(rs.getInt("value"));
            setValue(rs.getInt("value"));
            setName(rs.getString("description"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("value", this.getValue());
        obj.put("description", this.getName());
        return obj.toString();
    }

    @Override
    public void parseJSON(String jsonString) {
        JSONObject obj = new JSONObject(jsonString);
        this.setName(obj.getString("description"));
        this.setValue(obj.getInt("value"));
        //this.setValue(obj.getInt("id"));
    }
}
