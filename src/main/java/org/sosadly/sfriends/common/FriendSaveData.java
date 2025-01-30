package org.sosadly.sfriends.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.*;

public class FriendSaveData extends SavedData {
    private final Map<String, Set<String>> friendsMap = new HashMap<>();

    public Map<String, Set<String>> getFriendsMap() {
        return friendsMap;
    }

    public static FriendSaveData load(CompoundTag nbt) {
        FriendSaveData data = new FriendSaveData();
        ListTag list = nbt.getList("Friends", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            String key = entry.getString("Owner");
            Set<String> set = new HashSet<>();
            ListTag uuids = entry.getList("Uuids", 8);
            for (int j = 0; j < uuids.size(); j++) {
                set.add(uuids.getString(j));
            }
            data.friendsMap.put(key, set);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (Map.Entry<String, Set<String>> e : friendsMap.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putString("Owner", e.getKey());
            ListTag arr = new ListTag();
            for (String f : e.getValue()) {
                arr.add(StringTag.valueOf(f));
            }
            entry.put("Uuids", arr);
            list.add(entry);
        }
        nbt.put("Friends", list);
        return nbt;
    }
}
