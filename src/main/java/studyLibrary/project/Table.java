package studyLibrary.project;

import java.util.ArrayList;

public class Table {

    private int tableNo;
    private String availability;
    private int reservedBy = 0;
    private int occupiedBy = 0; //ID olarak tutucaz, ve ilk başta 0 (kimse tarafından alınmamış)

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
    public int getReservedBy() {
        return reservedBy;
    }
    public void setReservedBy(int reservedBy) {
        this.reservedBy = reservedBy;
    }
    public int getOccupiedBy(){
        return occupiedBy;
    }
    public void setOccupiedBy(int occupiedBy){
        this.occupiedBy = occupiedBy;
    }

}
