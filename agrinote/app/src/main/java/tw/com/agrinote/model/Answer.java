package tw.com.agrinote.model;

/**
 * Created by orc59 on 2017/11/22.
 */

public class Answer {

    private String mainItem = "無";
    private String subItem = "無";
    private String note = "無";
    private String usageAmount = "無";
    private String unit = "無";
    private String dilution = "無";
    private String item = "無";
    private String itemCost = "無";
    private String income = "無";
    private String machine = "無";


    public String getMainItem() {
        return mainItem;
    }

    public String displayMainItem() {
        return "[ 工作項目 ] : " + mainItem;
    }

    public void setMainItem(String mainItem) {
        if (isNotNullorEmpty(mainItem)) {
            this.mainItem = mainItem;
        }
    }


    public String getSubItem() {
        return subItem;
    }

    public String displaySubItem() {
        return "[ 副工作項目 ] : " + subItem;
    }

    public void setSubItem(String subItem) {
        if (isNotNullorEmpty(subItem)) {
            this.subItem = subItem;
        }
    }

    public String getNote() {
        return note;
    }

    public String displayNote() {
        return "[ 工作備註 ] : " + note;
    }

    public void setNote(String note) {
        if (isNotNullorEmpty(note)) {
            this.note = note;
        }
    }

    public String getUsageAmount() {
        return usageAmount;
    }

    public String displayUsageAmount() {
        return "[ 使用量(次/量) ] : " + usageAmount;
    }


    public void setUsageAmount(String usageAmount) {
        if (isNotNullorEmpty(usageAmount)) {
            this.usageAmount = usageAmount;
        }
    }

    public String getUnit() {
        return unit;
    }

    public String displayUnit() {
        return "[ 使用單位 ] : " + unit;
    }

    public void setUnit(String unit) {
        if (isNotNullorEmpty(unit)) {
            this.unit = unit;
        }
    }

    public String getDilution() {
        return dilution;
    }

    public String displayDilution() {
        return "[ 稀釋倍數 ] : " + dilution;
    }

    public void setDilution(String dilution) {
        if (isNotNullorEmpty(dilution)) {
            this.dilution = dilution;
        }
    }

    public String getItem() {
        return item;
    }

    public String displayItem() {
        return "[ 使用資材 ] : " + item;
    }

    public void setItem(String item) {
        if (isNotNullorEmpty(item)) {
            this.item = item;
        }
    }

    public String getItemCost() {
        return itemCost;
    }

    public String displayItemCost() {
        return "[ 資材使用費用 ] : " + itemCost;
    }

    public void setItemCost(String itemCost) {
        if (isNotNullorEmpty(itemCost)) {
            this.itemCost = itemCost;
        }

    }

    public String getIncome() {
        return income;
    }

    public String displayIncome() {
        return "[ 賣出價格 ] : " + income;
    }

    public void setIncome(String income) {
        if (isNotNullorEmpty(income)) {
            this.income = income;
        }
    }

    public String getMachine() {
        return machine;
    }

    public String displayMachine() {
        return "[ 使用農具機 ] : " + machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    private boolean isNotNullorEmpty(String text) {
        return null != text && !"".equals(text.trim());
    }

    @Override
    public String toString() {
        return "Answer{" +
                "mainItem='" + mainItem + '\'' +
                ", subItem='" + subItem + '\'' +
                ", note='" + note + '\'' +
                ", usageAmount='" + usageAmount + '\'' +
                ", unit='" + unit + '\'' +
                ", dilution='" + dilution + '\'' +
                ", item='" + item + '\'' +
                ", itemCost='" + itemCost + '\'' +
                ", income='" + income + '\'' +
                ", machine='" + machine + '\'' +
                '}';
    }

    public String display() {
        return "\n\n[ 工作項目 ] : " + getMainItem() + " \n\n" +
                "[ 工作備註 ] : " + getNote() + "\n\n" +
                "[ 使用量(次/量) ] : " + getUsageAmount() + "\n\n" +
                "[ 使用單位 ] : " + getUnit() + "\n\n" +
                "[ 稀釋倍數 ] : " + getDilution() + "\n\n" +
                "[ 使用資材 ] : " + getItem() + "\n\n" +
                "[ 使用資材費用 ] : " + getItemCost() + "\n\n" +
                "[ 賣出價格 ] : " + getIncome() + "\n\n" +
                "[ 使用農機具 ] : " + getMachine() + "\n\n";
    }

    public String smallDisplay() {
        return "[ 工作項目 ] : " + getMainItem() + " \n" +
                "[ 工作備註 ] : " + getNote() + "\n" +
                "[ 使用量 ] : " + getUsageAmount() + "\n" +
                "[ 使用單位 ] : " + getUnit() + "\n" +
                "[ 稀釋倍數 ] : " + getDilution() + "\n" +
                "[ 使用資材 ] : " + getItem() + "\n" +
                "[ 使用資材費用 ] : " + getItemCost() + "\n" +
                "[ 賣出價格 ] : " + getIncome() + "\n" +
                "[ 使用農機具 ] : " + getMachine() + "\n";
    }
}
