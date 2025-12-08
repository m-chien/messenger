package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

    FriendRequest findFirstByReceiverId(Integer id);

    FriendRequest findFirstBySenderId(Integer id);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.status = :status " +
            "AND (fr.receiver.id = :userId OR fr.sender.id = :userId)")
    List<FriendRequest> findAcceptedFriendRequests(@Param("userId") Integer userId, @Param("status") String status);

}
