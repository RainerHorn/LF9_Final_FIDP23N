package schueler;

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
        return "INSERT INTO priority VALUES ("+this.value +","+this.getName()+");";
    }

    @Override
    public String getUpdateStatement() {
        return "UPDATE Priority SET value = " + this.value + " WHERE name = '" + this.getName() + "';";
    }

    @Override
    public String getDeleteStatement() {
        return "DELETE FROM Priority WHERE name = '" + this.getName() + "';";
    }

    @Override
    public String getReadStatement() {
        return "SELECT * FROM Priority WHERE name = '" + this.getName() + "';";
    }

    @Override
    public String getReadAllStatement() {
        return "SELECT * FROM Priority;";
    }
}
