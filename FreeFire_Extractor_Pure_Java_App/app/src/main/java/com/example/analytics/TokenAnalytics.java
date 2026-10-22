package com.example.analytics;

import android.util.Base64;
import com.example.model.TokenReport;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class TokenAnalytics {
    private static final long TOTAL_SESSION_DURATION_SEC = 28800L; // 8 hours

    private TokenAnalytics() {}

    public static TokenReport analyze(String jwtToken, long fallbackUid) {
        if (jwtToken == null || jwtToken.trim().isEmpty() || !jwtToken.startsWith("eyJ")) {
            return null;
        }

        try {
            String[] parts = jwtToken.trim().split("\\.");
            if (parts.length < 2) return null;

            String payloadB64 = parts[1];
            // Fix padding
            while (payloadB64.length() % 4 != 0) {
                payloadB64 += "=";
            }

            byte[] decodedBytes = Base64.decode(payloadB64, Base64.URL_SAFE | Base64.NO_WRAP);
            String payloadJsonStr = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(payloadJsonStr);

            long accountId = payload.optLong("account_id", payload.optLong("external_uid", fallbackUid));
            String region = payload.optString("lock_region", payload.optString("noti_region", "BD"));
            String country = payload.optString("country_code", "BD");

            long exp = payload.optLong("exp", 0L);
            long lockRegionTime = payload.optLong("lock_region_time", 0L);

            long nowSec = System.currentTimeMillis() / 1000L;
            long lastLoginSec = exp > 0 ? (exp - TOTAL_SESSION_DURATION_SEC) : nowSec;
            long createdSec = lockRegionTime > 0 ? lockRegionTime : lastLoginSec;

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a 'BST'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+6"));

            String expFormatted = exp > 0 ? sdf.format(new Date(exp * 1000L)) : "N/A";
            String createdFormatted = sdf.format(new Date(createdSec * 1000L));
            String lastLoginFormatted = sdf.format(new Date(lastLoginSec * 1000L));

            long remainingSec = Math.max(0L, exp - nowSec);
            boolean isExpired = exp > 0 && nowSec >= exp;

            int percent = 0;
            if (exp > 0 && !isExpired) {
                long elapsed = TOTAL_SESSION_DURATION_SEC - remainingSec;
                percent = (int) Math.min(100L, Math.max(0L, (remainingSec * 100L) / TOTAL_SESSION_DURATION_SEC));
            }

            return new TokenReport(
                    jwtToken.trim(),
                    accountId,
                    region,
                    country,
                    exp,
                    createdSec,
                    lastLoginSec,
                    expFormatted,
                    createdFormatted,
                    lastLoginFormatted,
                    remainingSec,
                    percent,
                    isExpired
            );
        } catch (Exception e) {
            return null;
        }
    }
}
