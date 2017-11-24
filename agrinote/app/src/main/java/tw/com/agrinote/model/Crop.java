package tw.com.agrinote.model;

/**
 * Created by orc59 on 2017/11/3.
 */

public class Crop {
    private int id;
    private int farmId;
    private String item1;
    private String item2;
    private String item3;
    private String startDate;
    private String picPath;
    private String period;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getItem3() {
        return item3;
    }

    public void setItem3(String item3) {
        this.item3 = item3;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCropName() {
        return this.item1 + "-" + this.item2 + "-" + this.item3;
    }

    @Override
    public String toString() {
        return "Crop{" +
                "id=" + id +
                ", farmId=" + farmId +
                ", item1='" + item1 + '\'' +
                ", item2='" + item2 + '\'' +
                ", item3='" + item3 + '\'' +
                ", startDate='" + startDate + '\'' +
                ", picPath='" + picPath + '\'' +
                ", period=" + period +
                '}';
    }


}
