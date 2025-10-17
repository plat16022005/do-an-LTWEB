package aloute.com.mapper;

import aloute.com.dto.CommentDTO;
import aloute.com.entity.Comment;

import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getCommentId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setLikeCount(comment.getLikeCount());

        dto.setUsername(comment.getUser().getNameUser());
        dto.setFullName(comment.getUser().getFullName());
        dto.setAvatarUrl(comment.getUser().getAvatarUrl());

        dto.setReplies(comment.getReplies().stream()
                .filter(c -> !c.isDeleted())
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList()));

        return dto;
    }
}
