package com.github.elenterius.magianaturalis.util.access;

import java.util.UUID;

public class UserAccess {

    private UUID id;
    private byte accessLevel;

    public UserAccess() {}

    public UserAccess(UUID id, byte accessType) {
        this.id = id;
        this.accessLevel = accessType;
    }

    public UUID getUUID() {
        return id;
    }

    public void setUUID(UUID id) {
        this.id = id;
    }

    public byte getAccessLevel() {
        return accessLevel;
    }

    public void setAccesLevel(byte accessType) {
        accessLevel = accessType;
    }

    public boolean hasAccess() {
        return accessLevel >= 0;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != UserAccess.class) return false;
        UserAccess user = (UserAccess) obj;
        return id.equals(user.id) && accessLevel == user.accessLevel;
    }

    public String toString() {
        return "(" + id.toString() + "," + accessLevel + ")";
    }
}
