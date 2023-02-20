package com.developlife.reviewtwits.type;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author ghdic
 * @since 2023/02/19
 */
public enum UserRole implements GrantedAuthority {
    // 임시로 두개 권한으로 나누어서 구현
    USER("ROLE_USER", "유저권한"),
    ADMIN("ROLE_ADMIN", "어드민권한");

    private String authority;
    private String description;

    private UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }


    @Override
    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }
}
