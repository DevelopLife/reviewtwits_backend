package com.developlife.reviewtwits.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author ghdic
 * @since 2023/02/27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "RefreshToken")
public class RefreshToken {
    @Id @GeneratedValue
    private long id;
    private String token;
    private String accountId;
}
