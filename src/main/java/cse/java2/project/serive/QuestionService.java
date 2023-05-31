package cse.java2.project.serive;

import cse.java2.project.model.Answer;
import cse.java2.project.model.Comment;
import cse.java2.project.model.Owner;
import cse.java2.project.model.Question;
import cse.java2.project.model.Questions;
import cse.java2.project.repository.AnswerRepository;
import cse.java2.project.repository.CommentRepository;
import cse.java2.project.repository.OwnerRepository;
import cse.java2.project.repository.QuestionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

  final QuestionRepository questionRepository;
  final AnswerRepository answerRepository;
  final OwnerRepository ownerRepository;

  final CommentRepository commentRepository;

  @Autowired
  public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository,
      OwnerRepository ownerRepository, CommentRepository commentRepository) {
    this.questionRepository = questionRepository;
    this.answerRepository = answerRepository;
    this.ownerRepository = ownerRepository;
    this.commentRepository = commentRepository;
  }

  public List<Question> findAllQuestion() {
    return questionRepository.findAll();
  }

  public List<Owner> findAllOwner() {
    return ownerRepository.findAll();
  }

  public List<Answer> findAllAnswer() {
    return answerRepository.findAll();
  }

  public List<Comment> findAllComment() {
    return commentRepository.findAll();
  }

  public Answer findByAnswer_id(Long answer_id) {
    return answerRepository.findByAnswerid(answer_id);
  }


  /**
   * 把数据插入数据库中
   */
  public void addAll() {
    List<Question> questions = Questions.useAPI();
    List<Owner> owners = new ArrayList<>();
    questions.forEach(t -> {
      t.setOwnerAccountId();
      t.setHaveAcceptedAns();
      owners.add(t.owner.getOwner());
      t.answers.forEach(e -> {
        answerRepository.save(e.getAnswer());

        owners.add(e.getOwner());
        if (e.comments != null) {
          owners.addAll(e.comments.stream().map(s -> {
            // System.out.println(s.owner.getOwner().getAccount_id());
            return s.owner.getOwner();
          }).toList());
        }
        if (e.comments != null) {
          commentRepository.saveAll(e.comments.stream().map(s -> s.getComment()).collect(
              Collectors.toList()));
        }
      });
      commentRepository.saveAll(
          t.comments.stream().map(s -> s.getComment())
              .filter(comment -> comment.getAccount_id() != null).collect(
                  Collectors.toList()));
    });

    ownerRepository.saveAll(
        owners.stream().filter(t -> t.getUser_id() != null && t.getAccount_id() != null)
            .filter(distinctByKey(Owner::getUser_id)).collect(
                Collectors.toList()));
    questionRepository.saveAll(questions.stream().filter(t -> t.getAccount_id() != null).collect(
        Collectors.toList()));
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }
}
