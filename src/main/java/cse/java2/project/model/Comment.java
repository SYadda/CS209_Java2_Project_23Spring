package cse.java2.project.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
public class Comment {

    @Transient
    Owner owner;
    Long post_id;

    String post_type;
    @Id
    Long comment_id;

    @Column(length = 40000)
    String body_markdown;

    @Column(length = 40000)
    String body;

    Integer account_id;

    public Comment(Owner owner, Long post_id, Long comment_id, String body_markdown, String body, String post_type) {
        this.owner = owner;
        this.post_id = post_id;
        this.comment_id = comment_id;
        this.body_markdown = body_markdown;
        this.body = body;
        this.account_id = owner.account_id;
        this.post_type = post_type;
    }

    public Comment() {

    }
}
