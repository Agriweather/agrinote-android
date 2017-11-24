package tw.com.agrinote.model;

/**
 * Created by orc59 on 2017/11/16.
 */

public class CropItem extends ItemBase {

    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "CropItem{" +
                "id='" + getId() + '\'' +
                ", parentId='" + getParentId() + '\'' +
                ", type='" + type + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}
