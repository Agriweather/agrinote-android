package tw.com.agrinote.model;

/**
 * Created by orc59 on 2017/11/22.
 */

public class WorkingItem extends ItemBase {

    @Override
    public String toString() {
        return "WorkingItem{" +
                "id='" + getId() + '\'' +
                ", parentId='" + getParentId() + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}
