package cse.java2.project.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Owner {
    @Id
    @GeneratedValue
    Long account_id;
    Long user_id;
    String display_name;

    public Owner(Long account_id, Long user_id, String display_name) {
        this.account_id = account_id;
        this.user_id = user_id;
        this.display_name = display_name;
    }

    public Owner() {

    }
}
