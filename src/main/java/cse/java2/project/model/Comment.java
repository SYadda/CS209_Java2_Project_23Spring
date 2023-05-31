package cse.java2.project.model;


import com.google.gson.annotations.SerializedName;
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

  @SerializedName("post_id")
  Long postid;

  String post_type;
  @Id
  Long comment_id;

  @Column(length = 40000)
  String body_markdown;

  @Column(length = 40000)
  String body;

  public Integer account_id;

  public Comment(Owner owner, Long post_id, Long comment_id, String body_markdown, String body,
      String post_type) {
    this.owner = owner;
    this.postid = post_id;
    this.comment_id = comment_id;
    this.body_markdown = body_markdown;
    this.body = body;
    this.account_id = owner.accountid;
    this.post_type = post_type;
  }

  public Comment() {

  }

  public Owner getOwner() {
    return owner;
  }

  public Long getPostid() {
    return postid;
  }

  public String getPost_type() {
    return post_type;
  }

  public Long getComment_id() {
    return comment_id;
  }

  public String getBody_markdown() {
    return body_markdown;
  }

  public String getBody() {
    return body;
  }

  public Integer getAccount_id() {
    return account_id;
  }
}
