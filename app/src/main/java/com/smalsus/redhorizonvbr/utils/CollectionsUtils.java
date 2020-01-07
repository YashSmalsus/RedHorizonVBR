package com.smalsus.redhorizonvbr.utils;
import com.smalsus.redhorizonvbr.model.EventUser;

import java.util.ArrayList;
import java.util.Collection;


public class CollectionsUtils {

    public static ArrayList<String> getSelectedUserVideoId( Collection<EventUser> userCollection) {
        ArrayList<String> opponentsIds = new ArrayList<>();
        if (!userCollection.isEmpty()) {
            for (EventUser qbUser : userCollection) {
                opponentsIds.add(qbUser.getVideoId());
            }
        }

        return opponentsIds;
    }
}