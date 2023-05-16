package cse.java2.project.repository;

import cse.java2.project.model.Owner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


public interface OwnerRepository extends JpaRepository<Owner, Long> {


}
