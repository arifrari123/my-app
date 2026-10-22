package com.example.data;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AccountHistoryManager {
    private static final String PREF_NAME = "ff_history_prefs";
    private static final String KEY_ACCOUNTS = "recent_accounts";
    private final SharedPreferences prefs;

    public static class AccountItem {
        public String uid;
        public String passwordHex;
        public String nickname;
        public int level;

        public AccountItem(String uid, String passwordHex, String nickname, int level) {
            this.uid = uid;
            this.passwordHex = passwordHex;
            this.nickname = nickname;
            this.level = level;
        }
    }

    public AccountHistoryManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<AccountItem> getRecentAccounts() {
        List<AccountItem> list = new ArrayList<>();
        String jsonStr = prefs.getString(KEY_ACCOUNTS, "[]");
        try {
            JSONArray arr = new JSONArray(jsonStr);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                list.add(new AccountItem(
                        obj.optString("uid"),
                        obj.optString("passwordHex"),
                        obj.optString("nickname"),
                        obj.optInt("level")
                ));
            }
        } catch (Exception ignored) {}
        return list;
    }

    public void saveAccount(String uid, String passwordHex, String nickname, int level) {
        if (uid == null || uid.isEmpty()) return;
        List<AccountItem> current = getRecentAccounts();

        // Remove duplicate if already present
        for (int i = current.size() - 1; i >= 0; i--) {
            if (uid.equals(current.get(i).uid)) {
                current.remove(i);
            }
        }

        // Add to top
        current.add(0, new AccountItem(uid, passwordHex, nickname, level));

        // Keep at most 5 items
        while (current.size() > 5) {
            current.remove(current.size() - 1);
        }

        JSONArray arr = new JSONArray();
        for (AccountItem item : current) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("uid", item.uid);
                obj.put("passwordHex", item.passwordHex);
                obj.put("nickname", item.nickname);
                obj.put("level", item.level);
                arr.put(obj);
            } catch (Exception ignored) {}
        }

        prefs.edit().putString(KEY_ACCOUNTS, arr.toString()).apply();
    }
}
