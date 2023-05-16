package cse.java2.project.repository;

import cse.java2.project.model.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository  extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostid(Long id);

}
