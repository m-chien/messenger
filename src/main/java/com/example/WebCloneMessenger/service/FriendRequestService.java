package com.example.WebCloneMessenger.service;

import com.example.WebCloneMessenger.DTO.FriendRequestDTO;
import com.example.WebCloneMessenger.Model.FriendRequest;
import com.example.WebCloneMessenger.Model.User;
import com.example.WebCloneMessenger.events.BeforeDeleteUser;
import com.example.WebCloneMessenger.mapper.FriendRequestMapper;
import com.example.WebCloneMessenger.repos.FriendRequestRepository;
import com.example.WebCloneMessenger.repos.UserRepository;
import com.example.WebCloneMessenger.Exception.NotFoundException;
import com.example.WebCloneMessenger.Exception.ReferencedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendRequestMapper friendRequestMapper;

    public List<FriendRequestDTO> findAll() {
        final List<FriendRequest> friendRequests = friendRequestRepository.findAll(Sort.by("id"));
        return friendRequests.stream()
                .map(friendRequest -> mapToDTO(friendRequest, new FriendRequestDTO()))
                .toList();
    }

    public FriendRequestDTO get(final Integer id) {
        return friendRequestRepository.findById(id)
                .map(friendRequest -> mapToDTO(friendRequest, new FriendRequestDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final FriendRequestDTO friendRequestDTO) {
        final FriendRequest friendRequest = new FriendRequest();
        mapToEntity(friendRequestDTO, friendRequest);
        return friendRequestRepository.save(friendRequest).getId();
    }

    public void update(final Integer id, final FriendRequestDTO friendRequestDTO) {
        final FriendRequest friendRequest = friendRequestRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(friendRequestDTO, friendRequest);
        friendRequestRepository.save(friendRequest);
    }

    public void delete(final Integer id) {
        final FriendRequest friendRequest = friendRequestRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        friendRequestRepository.delete(friendRequest);
    }
    public List<FriendRequestDTO> GetAllByAcceptedFriendRequests(final Integer receiverId) {
        return friendRequestRepository.findAcceptedFriendRequests(receiverId, "accepted")
                .stream()
                .map(friendRequestMapper::friendRequestToFriendRequestDTO)
                .toList();
    }


    private FriendRequestDTO mapToDTO(final FriendRequest friendRequest,
            final FriendRequestDTO friendRequestDTO) {
        friendRequestDTO.setId(friendRequest.getId());
        friendRequestDTO.setDateSend(friendRequest.getDateSend());
        friendRequestDTO.setStatus(friendRequest.getStatus());
        friendRequestDTO.setReceiver(friendRequest.getReceiver() == null ? null : friendRequest.getReceiver().getId());
        friendRequestDTO.setSender(friendRequest.getSender() == null ? null : friendRequest.getSender().getId());
        return friendRequestDTO;
    }

    private FriendRequest mapToEntity(final FriendRequestDTO friendRequestDTO,
            final FriendRequest friendRequest) {
        friendRequest.setDateSend(friendRequestDTO.getDateSend());
        friendRequest.setStatus(friendRequestDTO.getStatus());
        final User receiver = friendRequestDTO.getReceiver() == null ? null : userRepository.findById(friendRequestDTO.getReceiver())
                .orElseThrow(() -> new NotFoundException("receiver not found"));
        friendRequest.setReceiver(receiver);
        final User sender = friendRequestDTO.getSender() == null ? null : userRepository.findById(friendRequestDTO.getSender())
                .orElseThrow(() -> new NotFoundException("sender not found"));
        friendRequest.setSender(sender);
        return friendRequest;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final FriendRequest receiverFriendRequest = friendRequestRepository.findFirstByReceiverId(event.getId());
        if (receiverFriendRequest != null) {
            referencedException.setKey("user.friendRequest.receiver.referenced");
            referencedException.addParam(receiverFriendRequest.getId());
            throw referencedException;
        }
        final FriendRequest senderFriendRequest = friendRequestRepository.findFirstBySenderId(event.getId());
        if (senderFriendRequest != null) {
            referencedException.setKey("user.friendRequest.sender.referenced");
            referencedException.addParam(senderFriendRequest.getId());
            throw referencedException;
        }
    }

}
