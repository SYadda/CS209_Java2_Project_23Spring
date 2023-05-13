package cse.java2.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Answer {
    @GeneratedValue
    Long account_id;
    Long down_vote_count;
    Long up_vote_count;
    Boolean is_accepted;
    Long creation_date;
    @Id
    Long answerid;

    Long question_id;
    @Column(length = 40000)
    String body_markdown;
    @Column(length = 40000)
    String body;
    String title;

    public Long getAccount_id() {
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
        return question_id;
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

    public Answer(Long account_id, Long down_vote_count, Long up_vote_count, Boolean is_accepted,
        Long creation_date, Long answer_id, Long question_id, String body_markdown, String body,
        String title) {
        this.account_id = account_id;
        this.down_vote_count = down_vote_count;
        this.up_vote_count = up_vote_count;
        this.is_accepted = is_accepted;
        this.creation_date = creation_date;
        this.answerid = answer_id;
        this.question_id = question_id;
        this.body_markdown = body_markdown;
        this.body = body;
        this.title = title;
    }

    public Answer() {

    }
}
