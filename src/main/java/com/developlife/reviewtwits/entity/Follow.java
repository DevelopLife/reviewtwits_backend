package com.developlife.reviewtwits.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

/**
 * @author WhalesBob
 * @since 2023-03-20
 */

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id @GeneratedValue
    private long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private User targetUser;

    @Builder.Default
    @ColumnDefault(value = "false")
    private boolean followBackFlag = false;
}