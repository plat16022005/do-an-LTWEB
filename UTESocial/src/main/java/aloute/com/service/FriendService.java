package aloute.com.service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aloute.com.entity.Friend;
import aloute.com.entity.User;
import aloute.com.repository.FriendRepository;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    public boolean checkFriend(Integer currentUserId, Integer otherUserId) {
        return friendRepository.isFriend(currentUserId, otherUserId);
    }
    public boolean checkBeRequest(Integer currentUserId, Integer otherUserId) {
        return friendRepository.isBeingRequested(otherUserId, currentUserId);
    }
    public boolean checkPending(Integer currentUserId, Integer otherUserId) {
        return friendRepository.isPending(currentUserId, otherUserId);
    }
    public List<User> getFriendList(Integer userId) {
        List<Friend> friends = friendRepository.findAcceptedFriends(userId);
        List<User> result = new ArrayList<>();

        for (Friend f : friends) {
            if (f.getUser1().getUserId().equals(userId)) {
                result.add(f.getUser2());
            } else {
                result.add(f.getUser1());
            }
        }

        return result;
    }
    public List<User> getListBeingRequested(Integer userId) {
        List<Friend> friends = friendRepository.findWaitingFriends(userId);
        List<User> result = new ArrayList<>();

        for (Friend f : friends) {
            // Mình là user2, nên người gửi lời mời chính là user1
            result.add(f.getUser1());
        }

        return result;
    }
    public List<User> getListRequested(Integer userId) {
        List<Friend> friends = friendRepository.findRequestedFriends(userId);
        List<User> result = new ArrayList<>();

        for (Friend f : friends) {
            // Mình là user1, nên người nhận lời mời là user2
            result.add(f.getUser2());
        }

        return result;
    }
    public void unfriend(Integer userId1, Integer userId2)
    {
    	friendRepository.deleteFriendship(userId1, userId2);
    }
    public void send(User user1, User user2)
    {
    	Friend friend = new Friend();
    	friend.setUser1(user1);
    	friend.setUser2(user2);
    	friend.setStatus("Pending");
    	friend.setRequestDate(LocalDateTime.now());
    	friendRepository.save(friend);
    }
    public void accept(User user1, User user2)
    {
    	Friend friend = friendRepository.findByUser1AndUser2(user2.getUserId(), user1.getUserId());
    	friend.setStatus("Accepted");
    	friendRepository.save(friend);
    }
    public void deny(User user1, User user2)
    {
    	Friend friend = friendRepository.findByUser1AndUser2(user2.getUserId(), user1.getUserId());
    	friendRepository.deleteFriendship(user1.getUserId(), user2.getUserId());
    }
}

