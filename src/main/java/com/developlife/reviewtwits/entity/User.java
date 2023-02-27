package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ghdic
 * @since 2023/02/019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "User")
public class User implements UserDetails {
    @Id @GeneratedValue
    private long userId;
    private String nickname;
    private String accountId;
    private String accountPw;

    @ElementCollection(fetch = FetchType.EAGER) // pk-fk갖고 별도테이블 생성
    @Enumerated(EnumType.STRING)
    // @CollectionTable(name="user_role", joinColumns = @JoinColumn(name="user_role_id", referencedColumnName = "user_role_id"))
    @Builder.Default // 인스턴스 만들때 특정 필드값으로 초기화 할경우
    private Set<UserRole> roles = new HashSet<>();
    LocalDateTime createdDate;
    LocalDateTime lastModifiedDate;

    // TODO 파일업로드 구현 완료시 구현
    // @Transient
    // FileStorage profile_image

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }


    @Override
    public String getUsername() {
        return this.accountId;
    }
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
