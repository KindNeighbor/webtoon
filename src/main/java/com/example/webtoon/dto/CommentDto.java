package com.example.webtoon.dto;

import com.example.webtoon.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long commentId;
    private String userComment;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
            .commentId(comment.getCommentId())
            .userComment(comment.getComment())
            .build();
    }
}
