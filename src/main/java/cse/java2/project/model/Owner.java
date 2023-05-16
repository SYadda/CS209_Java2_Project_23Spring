package cse.java2.project.model;

import com.google.gson.annotations.SerializedName;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.GenericGenerator;

@Entity
@DynamicInsert
public class Owner {


    @Id
    @GeneratedValue
    Integer id;



    @SerializedName("account_id")
    Integer accountid;
    @Column(unique = true)
    Long user_id;
    String display_name;

    public Owner(Integer account_id, Long user_id, String display_name) {
        this.accountid = account_id;
        this.user_id = user_id;
        this.display_name = display_name;
    }

    public Owner() {

    }

    public Integer getId() {
        return id;
    }

    public Integer getAccount_id() {
        return accountid;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    @Override
    public String toString() {
        return "Owner{" +
            "id=" + id +
            ", account_id=" + accountid +
            ", user_id=" + user_id +
            ", display_name='" + display_name + '\'' +
            '}';
    }
}
