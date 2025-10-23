package aloute.com.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aloute.com.entity.Message;
import aloute.com.repository.MessageRepository;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public String getLatestMessagePreview(Integer userId1, Integer userId2) {
        List<Message> result = messageRepository.findLatestMessageBetween(
                userId1, userId2, PageRequest.of(0, 1)
        );

        if (result.isEmpty()) {
            return "Nhắn tin với bạn này...";
        }

        Message latest = result.get(0);
        String content = latest.getContent();
        if (content == null || content.trim().isEmpty()) {
            content = "[Tệp đính kèm]";
        }

        if (content.length() > 30) {
            content = content.substring(0, 30) + "...";
        }

        if (latest.getSender().getUserId().equals(userId1)) {
            return "Bạn: " + content;
        } else {
            return latest.getSender().getFullName() + ": " + content;
        }
    }
    @Transactional(readOnly = true)
    public List<Message> getAllMessagesBetween(Integer userId1, Integer userId2) {
        return messageRepository.findAllMessagesWithAttachments(userId1, userId2);
    }  
}
