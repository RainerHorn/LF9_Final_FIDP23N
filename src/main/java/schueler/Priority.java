package schueler;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Priority extends Entity{
    
    private int value;

    public Priority(int value,String name) {
        super(name);
        this.setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value<0) value=0;
        this.value = value;
    }

    @Override
    public String getCreateStatement() {
        return "INSERT INTO priority VALUES (" + this.getId() + "," + this.getValue() + ",\"" + this.getName() + "\");";
    }

    @Override
    public String getUpdateStatement() {
        return "UPDATE Priority SET value = " + this.getValue() + ", description = \"" + this.getName() + "\" WHERE id = '" + this.getId() + "';";
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
            setValue(rs.getInt("value"));
            setName(rs.getString("description"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
