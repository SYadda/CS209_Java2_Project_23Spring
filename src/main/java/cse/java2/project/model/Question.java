package cse.java2.project.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class Question {

    @Id
    @GeneratedValue
    public Long question_id;
    @ElementCollection
    List<String> tags;

    @Transient
    public owner owner;

    Long account_id;
    Boolean is_answered;

    Long down_vote_count;

    Long up_vote_count;

    Long answer_count;

    Long last_activity_date;

    Long creation_date;

    Long accepted_answer_id;

    Boolean haveAcceptedAns = false;

    Long view_count;




    @Column(length = 40000)
    String body_markdown;

    @Column(length = 40000)
    String body;

    String title;

    @Transient
    public List<answer> answers = new ArrayList<>();

    public void setOwnerAccountId() {
        this.account_id = owner.account_id;
    }

    public void setHaveAcceptedAns(){
        if (accepted_answer_id != null){
            haveAcceptedAns = true;
        }
    }

    /**
     * owner和answer只是作为临时存储用的,算是对数据库嵌套失败的妥协，反正基本上也就涉及到他俩独有的属性的时候需要给question
     * 传一下，其他时间基本用不上
     */
    public class owner {

        Long account_id;
        Long user_id;
        String display_name;

        public Owner getOwner() {
            return new Owner(account_id, user_id, display_name);
        }

    }

    /**
     * 与owner类似
     */
    public class answer {

        owner owner;
        Long down_vote_count;
        Long up_vote_count;
        Boolean is_accepted;
        Long creation_date;

        Long answer_id;
        Long question_id;

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
            return answer_id;
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

        String body_markdown;
        String body;
        String title;

        public Answer getAnswer() {
            return new Answer(owner.account_id, down_vote_count, up_vote_count, is_accepted,
                creation_date, answer_id, question_id, body_markdown, body, title);
        }

        public Owner getOwner() {
            return new Owner(owner.account_id, owner.user_id, owner.display_name);
        }
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public Long getAccepted_answer_id() {
        return accepted_answer_id;
    }

    public Boolean getHaveAcceptedAns() {
        return haveAcceptedAns;
    }

    public Long getView_count() {
        return view_count;
    }

    public List<String> getTags() {
        return tags;
    }

    public Question.owner getOwner() {
        return owner;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public Boolean getIs_answered() {
        return is_answered;
    }

    public Long getDown_vote_count() {
        return down_vote_count;
    }

    public Long getUp_vote_count() {
        return up_vote_count;
    }

    public Long getAnswer_count() {
        return answer_count;
    }

    public Long getLast_activity_date() {
        return last_activity_date;
    }

    public Long getCreation_date() {
        return creation_date;
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

    public List<answer> getAnswers() {
        return answers;
    }
}
