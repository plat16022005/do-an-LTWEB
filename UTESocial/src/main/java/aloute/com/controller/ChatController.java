package aloute.com.controller;

import aloute.com.dto.MessageDTO;
import aloute.com.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO message) {
        Integer senderId = message.getSenderId();
        Integer receiverId = message.getReceiverId();

        // ✅ Gửi tin nhắn realtime cho người nhận
        messagingTemplate.convertAndSend("/topic/messages/" + receiverId, message);

        // ✅ Gửi luôn cho người gửi để đồng bộ nhiều tab
        messagingTemplate.convertAndSend("/topic/messages/" + senderId, message);
    }
}
