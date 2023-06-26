package com.developlife.reviewtwits.entity;

import com.developlife.reviewtwits.type.Gender;
import com.developlife.reviewtwits.type.JwtProvider;
import com.developlife.reviewtwits.type.UserRole;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.*;

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
    @Column(unique = true)
    private String accountId;
    @Setter
    private String accountPw;
    @Setter
    @Column(columnDefinition = "TIMESTAMP")
    private Date birthDate;

    @Column(unique = true, length = 30)
    private String phoneNumber;

    // 최초 업뎃만 가능하도록함
    public void setPhoneNumber(String phoneNumber) {
        if(!StringUtils.hasText(this.phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }
    }
    @Setter
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    @Enumerated(value = EnumType.STRING)
    private JwtProvider provider;

    public void setProvider(JwtProvider provider) {
        if(this.provider == null) {
            this.provider = provider;
        }
    }
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

    @Setter
    @Builder.Default
    String introduceText = "";

    @Setter
    String profileImageUuid;

    @Setter
    @Builder.Default
    String detailIntroduce = "";

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

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || Hibernate.getClass(this) != Hibernate.getClass(o)){
            return false;
        }

        User toCompare = (User)o;
        return (this.userId == toCompare.getUserId());
    }

    public int getAge() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.setTime(this.birthDate);
        return year - cal.get(Calendar.YEAR) + 1;
    }
}
