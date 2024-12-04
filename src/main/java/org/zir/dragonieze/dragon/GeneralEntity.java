package org.zir.dragonieze.dragon;

import org.zir.dragonieze.user.User;


public interface GeneralEntity {
    void setId(Long id); //todo uuid
    long getId();

    void setUser(User user);
    User getUser();

    void setCanEdit(boolean canEdit);
    boolean getCanEdit();
}

