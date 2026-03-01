package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.UserDTO;
import com.example.WebCloneMessenger.DTO.FriendDTO;
import com.example.WebCloneMessenger.DTO.FriendDetailDTO;
import com.example.WebCloneMessenger.Model.Friend;
import com.example.WebCloneMessenger.Model.FriendId;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.mapper.FriendMapper;
import com.example.WebCloneMessenger.mapper.UserMapper;
import com.example.WebCloneMessenger.repos.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final UserMapper userMapper;

    /**
     * Add a new friend relationship
     */
    public FriendDTO addFriend(Integer userID1, Integer userID2) {
        // Ensure consistent ordering
        Integer minID = Math.min(userID1, userID2);
        Integer maxID = Math.max(userID1, userID2);

        FriendId friendId = new FriendId(minID, maxID);
        Friend friend = new Friend();
        friend.setId(friendId);
        Friend saved = friendRepository.save(friend);
        return friendMapper.toDTO(saved);
    }

    /**
     * Remove a friend relationship
     */
    public void removeFriend(Integer userID1, Integer userID2) {
        Integer minID = Math.min(userID1, userID2);
        Integer maxID = Math.max(userID1, userID2);
        FriendId friendId = new FriendId(minID, maxID);
        friendRepository.deleteById(friendId);
    }

    /**
     * Get all friends of a user
     */
    public List<FriendDTO> getFriends(Integer userId) {
        return friendRepository.findFriendsByUserId(userId)
                .stream()
                .map(friendMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all friends with detailed information
     */
    public List<FriendDetailDTO> getFriendsWithDetails(Integer userId) {
        return friendRepository.findFriendsByUserId(userId)
                .stream()
                .map(friend -> friendMapper.toDetailDTO(friend, userId))
                .collect(Collectors.toList());
    }

    /**
     * Check if two users are friends
     */
    public boolean areFriends(Integer userID1, Integer userID2) {
        return friendRepository.existsFriendship(userID1, userID2);
    }

    /**
     * Get a specific friend relationship
     */
    public Optional<FriendDTO> getFriendship(Integer userID1, Integer userID2) {
        Optional<Friend> friend = friendRepository.findFriend(userID1, userID2);
        return friend.map(friendMapper::toDTO);
    }

    /**
     * Count friends of a user
     */
    public long countFriends(Integer userId) {
        return friendRepository.findFriendsByUserId(userId).size();
    }

    /**
     * Get mutual friends between two users
     */
    public List<UserDTO> getMutualFriends(Integer userId1, Integer userId2) {
        List<User> mutualUsers = friendRepository.findMutualFriends(userId1, userId2);
        return mutualUsers.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }
}



