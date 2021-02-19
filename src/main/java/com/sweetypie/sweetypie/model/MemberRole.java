package com.sweetypie.sweetypie.model;

import lombok.Getter;

@Getter
public enum MemberRole {

    ADMIN("ROLE_ADMIN"), MEMBER("ROLE_MEMBER");

    private final String roleName;

    MemberRole(String roleName) {
        this.roleName = roleName;
    }
}
