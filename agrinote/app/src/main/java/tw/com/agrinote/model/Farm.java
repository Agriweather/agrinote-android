package tw.com.agrinote.model;

/**
 * Created by orc59 on 2017/11/3.
 */

public class Farm {
    private int id;
    private String name;
    private String landId;
    private String startDate;
    private double latitude;
    private double longtiude;
    private String address;
    private int userId;
    private String picPath;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLandId() {
        return landId;
    }

    public void setLandId(String landId) {
        this.landId = landId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtiude() {
        return longtiude;
    }

    public void setLongtiude(double longtiude) {
        this.longtiude = longtiude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    @Override
    public String toString() {
        return "Farm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", landId='" + landId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", latitude=" + latitude +
                ", longtiude=" + longtiude +
                ", address='" + address + '\'' +
                ", userId=" + userId +
                ", picPath='" + picPath + '\'' +
                '}';
    }
}
