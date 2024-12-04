package org.zir.dragonieze.dragon;

import org.zir.dragonieze.user.User;


public interface GeneralEntity {
    default void setId(Long id) {
    }

    ; //todo uuid

    default Long getId() {
        return null;
    }

    ;


    default void setUser(User user) {
    }

    ;

    default User getUser() {
        return null;
    }

    ;

    default void setCanEdit(boolean canEdit) {
    }

    ;

    default boolean getCanEdit() {
        return false;
    }

    ;
}

