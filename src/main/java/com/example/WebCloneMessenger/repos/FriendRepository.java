package com.example.WebCloneMessenger.repos;

import com.example.WebCloneMessenger.Model.Friend;
import com.example.WebCloneMessenger.Model.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId> {

    @Query("SELECT f FROM Friend f WHERE f.id.userID1 = :userId OR f.id.userID2 = :userId ORDER BY f.createdDate DESC")
    List<Friend> findFriendsByUserId(@Param("userId") Integer userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f " +
           "WHERE (f.id.userID1 = :userId1 AND f.id.userID2 = :userId2) " +
           "OR (f.id.userID1 = :userId2 AND f.id.userID2 = :userId1)")
    boolean existsFriendship(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    @Query("SELECT f FROM Friend f WHERE " +
           "(f.id.userID1 = :userId1 AND f.id.userID2 = :userId2) " +
           "OR (f.id.userID1 = :userId2 AND f.id.userID2 = :userId1)")
    Optional<Friend> findFriend(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    @Query("SELECT u FROM User u WHERE u.id IN (" +
           "  SELECT CASE WHEN f1.id.userID1 = :userId1 THEN f1.id.userID2 ELSE f1.id.userID1 END " +
           "  FROM Friend f1 WHERE f1.id.userID1 = :userId1 OR f1.id.userID2 = :userId1" +
           ") AND u.id IN (" +
           "  SELECT CASE WHEN f2.id.userID1 = :userId2 THEN f2.id.userID2 ELSE f2.id.userID1 END " +
           "  FROM Friend f2 WHERE f2.id.userID1 = :userId2 OR f2.id.userID2 = :userId2" +
           ")")
    List<com.example.WebCloneMessenger.Model.User> findMutualFriends(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
}

