package com.developlife.reviewtwits.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "User")
public class User {
    @Id @GeneratedValue
    long userId;
    String username;
    String accountId;
    String accountPw;
    // role
    LocalDateTime createdDate;
    LocalDateTime lastModifiedDate;
    // TODO 파일업로드 구현 완료시 구현
    // @Transient
    // FileStorage profile_image
}
