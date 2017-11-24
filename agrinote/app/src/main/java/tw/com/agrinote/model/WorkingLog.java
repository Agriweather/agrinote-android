package tw.com.agrinote.model;

/**
 * Created by orc59 on 2017/11/14.
 */

public class WorkingLog {

    private int id;
    private int cropId;
    private String picPath;
    private String recordDate;
    private String working;
    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCropId() {
        return cropId;
    }

    public void setCropId(int cropId) {
        this.cropId = cropId;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        this.working = working;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "WorkingLog{" +
                "id=" + id +
                ", cropId=" + cropId +
                ", picPath='" + picPath + '\'' +
                ", recordDate='" + recordDate + '\'' +
                ", working='" + working + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
