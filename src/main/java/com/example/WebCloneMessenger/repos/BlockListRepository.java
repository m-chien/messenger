package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.BlockList;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BlockListRepository extends JpaRepository<BlockList, Integer> {

    BlockList findFirstByBlockerId(Integer id);

    BlockList findFirstByBlockedId(Integer id);

}
