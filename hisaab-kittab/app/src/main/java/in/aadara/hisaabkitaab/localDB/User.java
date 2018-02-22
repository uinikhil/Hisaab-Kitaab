package in.aadara.hisaabkitaab.localDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by umashankarpathak on 15/01/18.
 */

@Entity
public class User {

    private List<Update> update;

    private String uid;

    private String remark;

    private String name;

    private String address;

    private String amount;

    private String mobile;

    private String date;

    private boolean shared;

    private String shared_by_name;

    private String shared_by_email;

    private String shared_to_uid;

    public void setShared_to_uid(String shared_by_uid) {
        this.shared_to_uid = shared_by_uid;
    }

    public String getShared_to_uid() {
        return shared_to_uid;
    }

    public String getShared_by_email() {
        return shared_by_email;
    }

    public void setShared_by_email(String shared_by_email) {
        this.shared_by_email = shared_by_email;
    }

    public String getShared_by_name() {
        return shared_by_name;
    }

    public void setShared_by_name(String shared_by_name) {
        this.shared_by_name = shared_by_name;
    }

    public boolean getShared(){
        return shared;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getAddress() {
        return address;
    }

    public String getAmount() {
        return amount;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }


    public List<Update> getUpdate() {
        return update;
    }

    public void setUpdate(List<Update> update) {
        this.update = update;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User chat = (User) o;

        return uid.equals(chat.uid)
                && (mobile == null ? chat.mobile == null : mobile.equals(chat.mobile));
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("mobile", getMobile());
        result.put("name", getName());
        result.put("address", getAddress());
        result.put("amount", getAmount());
        result.put("date", getDate());

        return result;
    }
}
