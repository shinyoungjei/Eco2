package com.web.eco2.domain.dto.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {
    private String content;
    private Long userId;
    private Long postId;
    private Long commentId;

    public CommentDto() {

    }
}
