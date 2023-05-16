package cse.java2.project.model;

import com.google.gson.annotations.SerializedName;
import cse.java2.project.model.Question.answer;
import cse.java2.project.model.Question.answer.comments;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.DynamicInsert;

@Entity
@DynamicInsert
public class Answer {
    @GeneratedValue
    @SerializedName("account_id")
    Integer account_id;
    Long down_vote_count;
    Long up_vote_count;
    Boolean is_accepted;
    Long creation_date;
    @Id
    Long answerid;

    @SerializedName("question_id")
    Long questionid;
    @Column(length = 40000)
    String body_markdown;
    @Column(length = 40000)
    String body;
    String title;
    @Transient
    List<Comment> comments = new ArrayList<>();

    public Integer getAccount_id() {
        return account_id;
    }

    public Long getDown_vote_count() {
        return down_vote_count;
    }

    public Long getUp_vote_count() {
        return up_vote_count;
    }

    public Boolean getIs_accepted() {
        return is_accepted;
    }

    public Long getCreation_date() {
        return creation_date;
    }

    public Long getAnswer_id() {
        return answerid;
    }

    public Long getQuestion_id() {
        return questionid;
    }

    public String getBody_markdown() {
        return body_markdown;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public Answer(Integer account_id, Long down_vote_count, Long up_vote_count, Boolean is_accepted,
        Long creation_date, Long answer_id, Long question_id, String body_markdown, String body,
        String title, List<comments> comments) {
        this.account_id = account_id;
        this.down_vote_count = down_vote_count;
        this.up_vote_count = up_vote_count;
        this.is_accepted = is_accepted;
        this.creation_date = creation_date;
        this.answerid = answer_id;
        this.questionid = question_id;
        this.body_markdown = body_markdown;
        this.body = body;
        this.title = title;
        if (comments != null){
        comments.forEach(t -> this.comments.add(t.getComment()));}

    }

    public Answer() {

    }

    @Override
    public String toString() {
        return "Answer{" +
            "account_id=" + account_id +
            ", down_vote_count=" + down_vote_count +
            ", up_vote_count=" + up_vote_count +
            ", is_accepted=" + is_accepted +
            ", creation_date=" + creation_date +
            ", answerid=" + answerid +
            ", question_id=" + questionid +
            ", title='" + title + '\'' +
            ", comments=" + comments +
            '}';
    }
}
