package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.UserRole;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "User")
public class User extends BaseEntity implements UserDetails {
    @Id @GeneratedValue
    private long userId;
    @Setter
    @Column(unique = true, length = 20)
    private String nickname;
    private String accountId;
    @Setter
    private String accountPw;
    @Setter
    @Column(columnDefinition = "TIMESTAMP")
    private Date birthDate;
    @Column(unique = true, length = 20)
    private String phoneNumber;
    @Setter
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    private String provider;
    private String uuid;

    @Setter
    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Project> projectList = new ArrayList<>();


    @Setter
    @ElementCollection(fetch = FetchType.EAGER) // pk-fk갖고 별도테이블 생성
    @Enumerated(EnumType.STRING)
    @Builder.Default // 인스턴스 만들때 특정 필드값으로 초기화 할경우
    private Set<UserRole> roles = new HashSet<>();

    // TODO 파일업로드 구현 완료시 구현
     @Transient
     String profileImage;

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
