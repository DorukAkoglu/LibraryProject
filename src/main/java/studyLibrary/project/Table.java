package studyLibrary.project;

import java.util.ArrayList;

public class Table {

    private int tableNo;
    private String availability;

    public Table(){
        this.availability = "Available";
    }

    public int getTableNo() {
        return tableNo;
    }
    public String getAvailability() {
        return availability;
    }
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    public void setTableNo(int no){
        this.tableNo = no;
    }
}
