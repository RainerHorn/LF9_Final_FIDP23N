package schueler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task extends Entity {

    private Priority priority;
    private Project project;
    private Date date;

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
        return "INSERT INTO task VALUES (" + this.getId() + ",'" + this.getName() + "');";
    }

    @Override
    public String getUpdateStatement() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "UPDATE task SET name = '" + this.getName() + "', date = " + sdf.format(this.getDate());
    }

    @Override
    public String getDeleteStatement() {
        return "DELETE * FROM task WHERE name = '" + this.getName() + "' AND id = " + this.getId();
    }

    @Override
    public String getReadStatement() {
        return "SELECT * FROM task WHERE id = " + this.getId() + ";";
    }

    @Override
    public String getReadAllStatement() {
        return "SELECT *  FROM task;";
    }

}
