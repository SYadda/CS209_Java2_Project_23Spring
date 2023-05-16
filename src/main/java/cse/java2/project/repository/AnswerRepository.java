package cse.java2.project.repository;


import cse.java2.project.model.Answer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

     Answer findByAnswerid(Long id);
     List<Answer> findAllByQuestionid(Long id);
}
