package in.aadara.hisaabkitaab.localDB;

/**
 * Created by umashankarpathak on 20/01/18.
 */

public class Update {
    private double amount;

    private String date;

    private String type;

    private String remark;

    public String getDate() {
        return date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getAmount() {
        return amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
