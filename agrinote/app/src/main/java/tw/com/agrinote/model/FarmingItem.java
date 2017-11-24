package tw.com.agrinote.model;

/**
 * Created by orc59 on 2017/11/22.
 */

public class FarmingItem extends ItemBase {

    @Override
    public String toString() {
        return "FarmingItem{" +
                "id='" + getId() + '\'' +
                ", parentId='" + getParentId() + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}
