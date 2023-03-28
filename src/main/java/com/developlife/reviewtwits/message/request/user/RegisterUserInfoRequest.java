package com.developlife.reviewtwits.message.request.user;

import com.developlife.reviewtwits.message.annotation.file.ImageFile;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author ghdic
 * @since 2023/02/25
 */
@Builder
public record RegisterUserInfoRequest(
    @Size(min = 2, max = 20, message = "닉네임은 2자리 이상, 20자리 이하로 입력해주세요")
    String nickname,
    @Size(max = 255, message = "자기소개는 255자리 이하로 입력해주세요")
    String introduceText,
    @ImageFile
    MultipartFile profileImage
) {
}
