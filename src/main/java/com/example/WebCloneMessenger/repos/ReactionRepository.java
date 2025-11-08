package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReactionRepository extends JpaRepository<Reaction, Integer> {

    Reaction findFirstByIduserId(Integer id);

    Reaction findFirstByMessageId(Integer id);

}
