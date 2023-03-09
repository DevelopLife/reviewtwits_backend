package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.EmailType;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author ghdic
 * @since 2023/03/03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "EmailVerify")
public class EmailVerify extends BaseEntity {
    @Id
    private String email;
    @Column(length = 36, unique = true)
    private String verifyCode;
    @Enumerated(EnumType.STRING)
    private EmailType type;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime verifyDate;
    @ColumnDefault("false")
    private boolean alreadyUsed;
}
