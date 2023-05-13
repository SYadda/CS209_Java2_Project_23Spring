package cse.java2.project.serive;

import cse.java2.project.model.Answer;
import cse.java2.project.model.Owner;
import cse.java2.project.model.Question;
import cse.java2.project.model.Questions;
import cse.java2.project.repository.AnswerRepository;
import cse.java2.project.repository.OwnerRepository;
import cse.java2.project.repository.QuestionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    final QuestionRepository questionRepository;
    final AnswerRepository answerRepository;
    final OwnerRepository ownerRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository,
        OwnerRepository ownerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.ownerRepository = ownerRepository;
    }

    public List<Question> findAllQuestion(){
        return questionRepository.findAll();
    }
    public List<Owner> findAllOwner(){
        return ownerRepository.findAll();
    }
    public List<Answer> findAllAnswer(){
        return answerRepository.findAll();
    }

    public Answer findByAnswer_id(Long answer_id){
        return answerRepository.findByAnswerid(answer_id);
    }


    /**
     * 把数据插入数据库中
     */
    public void addAll() {
        List<Question> questions = Questions.useAPI();
        questions.forEach(t -> {
            t.setOwnerAccountId();
            t.setHaveAcceptedAns();
            t.answers.forEach(e -> {
                answerRepository.save(e.getAnswer());
                ownerRepository.save(e.getOwner());
            });
            ownerRepository.save(t.owner.getOwner());
        });
        questionRepository.saveAll(questions);
    }
}
