package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.BlockList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BlockListRepository extends JpaRepository<BlockList, Integer> {

    BlockList findFirstByBlockerId(Integer id);

    BlockList findFirstByBlockedId(Integer id);

    boolean existsByBlocker_IdAndBlocked_Id(Integer blockerId, Integer blockedId);
}
