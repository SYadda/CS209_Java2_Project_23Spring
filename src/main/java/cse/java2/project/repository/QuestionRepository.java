package cse.java2.project.repository;

import cse.java2.project.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuestionRepository extends JpaRepository<Question, Long> {


}
