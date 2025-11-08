package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

    FriendRequest findFirstByReceiverId(Integer id);

    FriendRequest findFirstBySenderId(Integer id);

}
