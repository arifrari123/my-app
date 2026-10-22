package com.example.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import com.example.analytics.TokenAnalytics;
import com.example.config.AppConfig;
import com.example.crypto.CryptoEngine;
import com.example.model.CharacterCatalog;
import com.example.model.DeviceProfile;
import com.example.model.PetCatalog;
import com.example.model.PlayerProfile;
import com.example.model.TokenReport;
import com.example.protobuf.ProtobufEngine;
import com.example.protobuf.ProtobufField;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameApiClient {

    public interface ApiCallback {
        void onSuccess(PlayerProfile profile, TokenReport tokenReport);
        void onFailure(String errorTitle, String errorMessage);
    }

    private final OkHttpClient client;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FORM_MEDIA = MediaType.parse("application/x-www-form-urlencoded");

    private static final String MOBILE_TEMPLATE_B64 =
            "GhMyMDI2LTEwLTIxIDEzOjQwOjAwIglmcmVlIGZpcmUoAToIMS4xMzIuMTFCFkFuZHJvaWQgT1MgMTQgLyBB" +
            "UEktMzRKCEhhbmRoZWxkUgxHcmFtZWVucGhvbmVaEkNhcnJpZXJEYXRhTmV0d29ya2DnDGjQBXIDMzIweh1B" +
            "Uk02NCBGUCBBU0lNRCBBRVMgfCAxNjAwIHwgOIABgBiKARRQb3dlclZSIFJvZ3VlIEdFODMyMpIBHU9wZW5H" +
            "TCBFUyAzLjIgdjEucjQ5cDEtMDNiZXQwmgErR29vZ2xlfGM3Y2Y3Y2ExLTJjYzEtNGE4OS05NThkLThiYWEw" +
            "ZWEzYmM5YqIBDTE4Mi41LjEzNS4yMjWqAQJlbrIBIDMzOTRmMDI0YjM4NGFiZjBhMjQ4ZDMwNjZlZmNjYTJk" +
            "ugEBNMIBCEhhbmRoZWxkygENWlRFIEJsYWRlIEEzNdIBAkJE6gFANWU3MGJkMmVmNGNhZGRhYWEzNTQ0ZmM5" +
            "MjE3MGExMmZmZGRiOTViNWNmNzJiMDk2MDJhMmI3YjhlNzhhNzY4ZfABAcoCDEdyYW1lZW5waG9uZdICAjRH" +
            "ygMgNzQyOGIyNTNkZWZjMTY0MDE4YzYwNGExZWJiZmViZGbgA97VBugD6rAD8APkBoAE97QDiATe1QaQBPe0" +
            "A5gE3tUGyAQC0gRaL2RhdGEvYXBwL35+NkN3MDI4Z19sZC04WjB6MnFock9MQT09L2NvbS5kdHMuZnJlZWZp" +
            "cmV0aC1pM3dpYUVtcHg4Q2JpUlZoVU14X2FnPT0vbGliL2FybTY04AQB6gR6YjhlMGNkNWUyOTVlZWU0MmY1" +
            "ODYwZDNjODZlNDgzZGR8L2RhdGEvYXBwL35+NkN3MDI4Z19sZC04WjB6MnFock9MQT09L2NvbS5kdHMuZnJl" +
            "ZWZpcmV0aC1pM3dpYUVtcHg4Q2JpUlZoVU14X2FnPT0vYmFzZS5hcGvwBAb4BAGKBQIzMpoFCjIwMTkxMjEy" +
            "MjmoBQOyBQlPcGVuR0xFUzK4Bf8XwAUEyAWCceoFCTNyZF9wYXJ0efIFcEtxc0hUeWI3eUhqc0FpeU5XVVVa" +
            "YTBEU09ENlUwQWhEUTc2c21BbzFXZUhISHBTbUo4S01SeVJyeGIydHRnSnFPOVlPcU8vSzZBT1JBdkRNQVgv" +
            "cUJTOCtKYWhJa3JNN2tIZnpsZlVMYmdSQW9aOXL4BefkBoIGLHsiY3VyX3JhdGUiOls2MCw5MCwxMjBdLCJz" +
            "dXBwb3J0X2V0YzIiOnRydWV9iAYBkAYBmgYBNKIGATS4BgHABuWcA8gGAdIGVWh0dHBzOi8vZGwuZ21jLmZy" +
            "ZWVmaXJlbW9iaWxlLmNvbS9saXZlL0FCSG90VXBkYXRlcy98MjExYzkzMzE2OGY1NTkwMmM3ZGZiZmQ4YzRl" +
            "Mjk1N2TaBhIxLjNmYjhhZGJmNDVmNDRjZTk=";

    private static final String EMULATOR_TEMPLATE_B64 =
            "GhMyMDI2LTEwLTE2IDAzOjMxOjAyIglmcmVlIGZpcmUoAToIMS4xMzIuMTFCMEFuZHJvaWQgT1MgMTAgLyBB" +
            "UEktMjkgKFFEMUEuMTkwODIxLjAxMS81ODQ5MjE2KUoISGFuZGhlbGRSDUNoaW5hIFRlbGVjb21aBFdJRklg" +
            "gApo0AVyAzMyMHodQVJNNjQgRlAgQVNJTUQgQUVTIHwgMjIwOCB8IDiAAZhzigEJTWFsaS1HNjEwkgE+T3Bl" +
            "bkdMIEVTIDMuMiB2MS5nMThwMC0wMWVhYzAuYWZjMGM0NGQyYmY3MDBiN2IzY2RkZDg5YzlhOThkZGKaAStH" +
            "b29nbGV8YzdjZjdjYTEtMmNjMS00YTg5LTk1OGQtOGJhYTBlYTNiYzliogENMTU1LjExNy42MS42OaoBAmV1" +
            "sgEgMzM5NGYwMjRiMzg0YWJmMGEyNDhkMzA2NmVmY2NhMmS6AQE0wgEISGFuZGhlbGTKAQ5Hb29nbGUgUGl4" +
            "ZWwgNNIBAlNH6gFANWU3MGJkMmVmNGNhZGRhYWEzNTQ0ZmM5MjE3MGExMmZmZGRiOTViNWNmNzJiMDk2MDJh" +
            "MmI3YjhlNzhhNzY4ZfABAcoCDUNoaW5hIFRlbGVjb23SAgRXSUZJygMgNzQyOGIyNTNkZWZjMTY0MDE4YzYw" +
            "NGExZWJiZmViZGbgA6CmDegDpo8K8AOgpg34A6aPCoAEqeYKiASgpg2QBKnmCpgEoKYNyAQD0gQ/L2RhdGEv" +
            "YXBwL2NvbS5kdHMuZnJlZWZpcmV0aC1fRXhNOU5kV2V5aHRMNURpT2NidnRBPT0vbGliL2FybTY04AQB6gRf" +
            "YjhlMGNkNWUyOTVlZWU0MmY1ODYwZDNjODZlNDgzZGR8L2RhdGEvYXBwL2NvbS5kdHMuZnJlZWZpcmV0aC1f" +
            "RXhNOU5kV2V5aHRMNURpT2NidnRBPT0vYmFzZS5hcGvwBAP4BAKKBQI2NJoFCjIwMTkxMjEyMjmoBQOyBQlP" +
            "cGVuR0xFUzK4Bf8fwAUE4AWZiQLqBQdhbmRyb2lk8gWIAUtxc0hUMldrcWRKM2pXRzFrWGVGYkE1RytGVjFv" +
            "R0lvWFFyYlF6SHJMZGhkZGVyYjZKUzVIV2YvSzJ3a1o0UURGdUdlVG14TWtsaE9aQTdpRm04T29CR0xvdmx0" +
            "Z00vZS9meW9OYnJoc2pjYUpSNTh3YjI1czBKM2p6U1p3bk1lY2FSUzlnPT2CBiZ7ImN1cl9yYXRlIjpudWxs" +
            "LCJzdXBwb3J0X2V0YzIiOmZhbHNlfYgGAZAGAZoGATCiBgE0sgYJSwMSEAIOBAZhuAYBwAav+APIBgHSBo8Ba" +
            "HR0cHM6Ly9kbC5jZG4uZnJlZWZpcmVtb2JpbGUuY29tL2xpdmUvQUJIb3RVcGRhdGVzL3xodHRwczovL2RsLWNv" +
            "cmUuY2RuLmZyZWVmaXJlbW9iaWxlLmNvbS9saXZlL0FCSG90VXBkYXRlcy98N2ZmYWM2MDdlZjI5ZDdmZjVk" +
            "YjE1ZjBhYjBiY2RmNzbaBhIxLjBjODcwMTliZDMxMTgwMGI=";

    private static final byte[] OLD_TOKEN_BYTES = "5e70bd2ef4caddaaa3544fc92170a12ffddb95b5cf72b09602a2b7b8e78a768e".getBytes(StandardCharsets.UTF_8);
    private static final byte[] OLD_OPENID_BYTES = "3394f024b384abf0a248d3066efcca2d".getBytes(StandardCharsets.UTF_8);

    public GameApiClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public void extractFromCredentials(final String guestUid, final String passwordHex, final DeviceProfile device, final ApiCallback callback) {
        executor.execute(() -> {
            try {
                final String cleanUid = guestUid.trim();
                final String cleanPassword = passwordHex.trim().replaceAll("\\s+", "");
                Long uidLong = null;
                try { uidLong = Long.parseLong(cleanUid); } catch (Exception ignored) {}

                // 1. Garena MSDK OAuth Token Grant
                JSONObject authJson = new JSONObject();
                authJson.put("client_id", AppConfig.CLIENT_ID);
                authJson.put("client_secret", AppConfig.CLIENT_SECRET);
                authJson.put("client_type", AppConfig.CLIENT_TYPE);
                authJson.put("device_id", "");
                authJson.put("password", cleanPassword);
                authJson.put("response_type", "token");
                if (uidLong != null) {
                    authJson.put("uid", uidLong);
                } else {
                    authJson.put("uid", cleanUid);
                }

                Request authReq = new Request.Builder()
                        .url(AppConfig.GARENA_AUTH_URL)
                        .header("Content-Type", "application/json")
                        .header("User-Agent", "GarenaMSDK/2.0.0")
                        .post(RequestBody.create(JSON_MEDIA, authJson.toString()))
                        .build();

                Response authResp = client.newCall(authReq).execute();
                String authBody = authResp.body() != null ? authResp.body().string() : "";

                if (!authResp.isSuccessful() || authBody.isEmpty()) {
                    postError(callback, "Garena Authentication Failed", parseError(authBody, authResp.code()));
                    return;
                }

                JSONObject respObj = new JSONObject(authBody);
                JSONObject dataObj = respObj.optJSONObject("data");
                if (dataObj == null) dataObj = respObj;

                String accessToken = dataObj.optString("access_token", "");
                String openId = dataObj.optString("open_id", "");

                if (accessToken.isEmpty() || openId.isEmpty()) {
                    String errorMsg = respObj.optString("error", "Invalid UID or Password");
                    postError(callback, "Authentication Rejected", errorMsg);
                    return;
                }

                // 2. MajorLogin (Generate 8-hour Live Game Session JWT)
                String templateB64 = (device == DeviceProfile.EMULATOR_PC) ? EMULATOR_TEMPLATE_B64 : MOBILE_TEMPLATE_B64;
                byte[] decLogin = Base64.decode(templateB64, Base64.DEFAULT);
                byte[] payloadWithToken = replaceBytes(decLogin, OLD_TOKEN_BYTES, accessToken.getBytes(StandardCharsets.UTF_8));
                byte[] loginPayload = replaceBytes(payloadWithToken, OLD_OPENID_BYTES, openId.getBytes(StandardCharsets.UTF_8));

                byte[] encryptedPayload = CryptoEngine.encrypt(loginPayload);

                Request loginReq = new Request.Builder()
                        .url(AppConfig.LOGIN_GATEWAY_URL)
                        .header("Host", "loginbp.ppmainecoonghj.com")
                        .header("User-Agent", "UnityPlayer/2018.4.12f1 (UnityWebRequest/1.0, libcurl/8.5.0-DEV)")
                        .header("Accept", "*/*")
                        .header("Accept-Encoding", "deflate, gzip")
                        .header("Authorization", "Bearer")
                        .header("X-GA", "v1 1")
                        .header("ReleaseVersion", AppConfig.RELEASE_VERSION)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("X-Unity-Version", "2018.4.12f1")
                        .header("X-Forwarded-For", "103.108.140.1")
                        .header("Client-IP", "103.108.140.1")
                        .header("X-Real-IP", "103.108.140.1")
                        .header("X-GA-SV", AppConfig.X_GA_SV)
                        .post(RequestBody.create(FORM_MEDIA, encryptedPayload))
                        .build();

                Response loginResp = client.newCall(loginReq).execute();
                byte[] loginBytes = loginResp.body() != null ? loginResp.body().bytes() : new byte[0];
                String gameJwtToken = loginResp.isSuccessful() ? extractJwtFromBytes(loginBytes) : "";

                if (gameJwtToken.isEmpty() || !gameJwtToken.startsWith("eyJ")) {
                    String errBody = new String(loginBytes, StandardCharsets.UTF_8).trim();
                    postError(callback, "MajorLogin Failed", "Server returned code " + loginResp.code() + ": " + errBody);
                    return;
                }

                // 3. Extract Full Player Profile with Real JWT
                extractFromJwtInternal(gameJwtToken, cleanUid, false, callback);

            } catch (Exception e) {
                postError(callback, "Connection Error", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            }
        });
    }

    public void extractFromJwt(final String jwtToken, final ApiCallback callback) {
        executor.execute(() -> {
            try {
                extractFromJwtInternal(jwtToken.trim(), "", true, callback);
            } catch (Exception e) {
                postError(callback, "JWT Profile Error", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            }
        });
    }

    private void extractFromJwtInternal(String jwtToken, String guestUid, boolean isDirectAuth, ApiCallback callback) throws Exception {
        long fallbackUid = 0L;
        try { fallbackUid = Long.parseLong(guestUid); } catch (Exception ignored) {}

        TokenReport tokenReport = TokenAnalytics.analyze(jwtToken, fallbackUid);
        if (tokenReport == null) {
            postError(callback, "Invalid JWT Token", "The provided token is not a valid 8-hour Free Fire session JWT (starts with 'eyJ').");
            return;
        }

        long accountId = tokenReport.getAccountId() != 0L ? tokenReport.getAccountId() : fallbackUid;
        String region = tokenReport.getRegion();

        PlayerProfile profile = fetchPlayerProfile(jwtToken, accountId, region, guestUid, isDirectAuth);

        // Fetch BR & CS rank details
        try {
            RankDetails br = fetchBrRank(jwtToken);
            CsDetails cs = fetchCsRank(jwtToken);

            profile = new PlayerProfile(
                    profile.getAccountId(),
                    profile.getGuestUid(),
                    profile.getNickname(),
                    profile.getLevel(),
                    profile.getExp(),
                    profile.getNextLevelExp(),
                    profile.getRegion(),
                    profile.getClanName(),
                    profile.getSignature(),
                    profile.getCreditScore(),
                    br.rankTitle,
                    53,
                    br.rankPoints,
                    br.maxRankTitle,
                    cs.rankTitle,
                    40,
                    cs.protectionPoints,
                    profile.getActiveCharacterId(),
                    profile.getActiveCharacterName(),
                    profile.getPetId(),
                    profile.getPetName(),
                    profile.getPetSpecies(),
                    profile.getPetLevel(),
                    profile.getClothesIds(),
                    profile.getAccountCreatedDate(),
                    profile.getLastLoginTimestamp(),
                    profile.getRawJwtToken(),
                    profile.isDirectJwtAuth()
            );
        } catch (Exception ignored) {}

        final PlayerProfile finalProfile = profile;
        final TokenReport finalReport = tokenReport;
        mainHandler.post(() -> callback.onSuccess(finalProfile, finalReport));
    }

    private PlayerProfile fetchPlayerProfile(String jwtToken, long accountId, String region, String guestUid, boolean isDirectAuth) throws Exception {
        ByteArrayOutputStream reqOut = new ByteArrayOutputStream();
        reqOut.write(ProtobufEngine.encodeVarintField(1, accountId));
        reqOut.write(ProtobufEngine.encodeVarintField(2, 9L));
        reqOut.write(ProtobufEngine.encodeVarintField(3, 1L));
        reqOut.write(ProtobufEngine.encodeVarintField(4, 1L));

        byte[] encryptedReq = CryptoEngine.encrypt(reqOut.toByteArray());

        Request req = new Request.Builder()
                .url(AppConfig.CLIENT_GATEWAY_URL + "/GetPlayerPersonalShow")
                .header("Host", "clientbp.ppmainecoonghj.com")
                .header("User-Agent", "UnityPlayer/2018.4.12f1 (UnityWebRequest/1.0, libcurl/8.5.0-DEV)")
                .header("Accept", "*/*")
                .header("Authorization", "Bearer " + jwtToken)
                .header("X-GA", "v1 1")
                .header("ReleaseVersion", AppConfig.RELEASE_VERSION)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-Unity-Version", "2018.4.12f1")
                .header("X-Forwarded-For", "103.108.140.1")
                .header("Client-IP", "103.108.140.1")
                .header("X-Real-IP", "103.108.140.1")
                .post(RequestBody.create(FORM_MEDIA, encryptedReq))
                .build();

        Response resp = client.newCall(req).execute();
        byte[] rawBytes = resp.body() != null ? resp.body().bytes() : new byte[0];

        if (!resp.isSuccessful() || rawBytes.length == 0) {
            throw new IOException("GetPlayerPersonalShow failed with HTTP " + resp.code());
        }

        // Response is RAW UNENCRYPTED Protobuf
        List<ProtobufField> fields = ProtobufEngine.decode(rawBytes);

        String nickname = "";
        int level = 0;
        long exp = 0L;
        String clanName = "";
        long brRankId = 0L;
        long brRankPoints = 0L;
        long activeCharId = 0L;
        List<Long> clothesIds = new ArrayList<>();
        long petId = 0L;
        String petName = "";
        int petLevel = 0;
        String signature = "";
        int creditScore = 100;

        for (ProtobufField f : fields) {
            if (f.getFieldNum() == 1 && f.getWireType() == 2) {
                List<ProtobufField> sub = ProtobufEngine.decode(f.getRawBytes());
                for (ProtobufField sf : sub) {
                    if (sf.getFieldNum() == 3) nickname = sf.asString();
                    else if (sf.getFieldNum() == 6) level = (int) sf.getVarintValue();
                    else if (sf.getFieldNum() == 7) exp = sf.getVarintValue();
                    else if (sf.getFieldNum() == 13) clanName = sf.asString();
                    else if (sf.getFieldNum() == 14) brRankId = sf.getVarintValue();
                    else if (sf.getFieldNum() == 15) brRankPoints = sf.getVarintValue();
                }
            } else if (f.getFieldNum() == 2 && f.getWireType() == 2) {
                List<ProtobufField> sub = ProtobufEngine.decode(f.getRawBytes());
                for (ProtobufField sf : sub) {
                    if (sf.getFieldNum() == 1) activeCharId = sf.getVarintValue();
                    else if (sf.getFieldNum() == 3 || sf.getFieldNum() == 4) {
                        if (sf.getWireType() == 2) {
                            clothesIds.addAll(ProtobufEngine.unpackVarints(sf.getRawBytes()));
                        } else if (sf.getWireType() == 0) {
                            clothesIds.add(sf.getVarintValue());
                        }
                    }
                }
            } else if (f.getFieldNum() == 8 && f.getWireType() == 2) {
                List<ProtobufField> sub = ProtobufEngine.decode(f.getRawBytes());
                for (ProtobufField sf : sub) {
                    if (sf.getFieldNum() == 1) petId = sf.getVarintValue();
                    else if (sf.getFieldNum() == 2) petName = sf.asString();
                    else if (sf.getFieldNum() == 3) petLevel = (int) sf.getVarintValue();
                }
            } else if (f.getFieldNum() == 9 && f.getWireType() == 2) {
                List<ProtobufField> sub = ProtobufEngine.decode(f.getRawBytes());
                for (ProtobufField sf : sub) {
                    if (sf.getFieldNum() == 9) signature = sf.asString();
                }
            } else if (f.getFieldNum() == 11 && f.getWireType() == 2) {
                List<ProtobufField> sub = ProtobufEngine.decode(f.getRawBytes());
                for (ProtobufField sf : sub) {
                    if (sf.getFieldNum() == 1) creditScore = (int) sf.getVarintValue();
                }
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a 'BST'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+6"));
        String nowFormatted = sdf.format(new Date());

        String petSpecies = (petId > 0) ? PetCatalog.getPetSpecies(petId) : "None";

        return new PlayerProfile(
                accountId,
                isDirectAuth ? "" : guestUid,
                nickname.isEmpty() ? "Player_" + accountId : nickname,
                level,
                exp,
                Math.max(1000L, level * 1500L),
                region,
                clanName.isEmpty() ? "None" : clanName,
                signature.isEmpty() ? "Enjoying Free Fire!" : signature,
                creditScore,
                CharacterCatalog.getRankTitle(brRankId),
                53,
                brRankPoints,
                CharacterCatalog.getRankTitle(brRankId),
                "Unranked",
                40,
                0L,
                activeCharId,
                CharacterCatalog.getCharacterName(activeCharId),
                petId,
                petName,
                petSpecies,
                petLevel,
                clothesIds,
                "N/A",
                nowFormatted,
                jwtToken,
                isDirectAuth
        );
    }

    private static class RankDetails {
        String rankTitle = "Unranked";
        long rankPoints = 0L;
        String maxRankTitle = "Unranked";
    }

    private RankDetails fetchBrRank(String jwtToken) {
        RankDetails res = new RankDetails();
        try {
            byte[] reqBytes = new byte[]{0x08, 0x01};
            byte[] encryptedReq = CryptoEngine.encrypt(reqBytes);

            Request req = new Request.Builder()
                    .url(AppConfig.CLIENT_GATEWAY_URL + "/GetBRRankingInfoEx")
                    .header("Host", "clientbp.ppmainecoonghj.com")
                    .header("User-Agent", "UnityPlayer/2018.4.12f1 (UnityWebRequest/1.0, libcurl/8.5.0-DEV)")
                    .header("Accept", "*/*")
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("X-GA", "v1 1")
                    .header("ReleaseVersion", AppConfig.RELEASE_VERSION)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .post(RequestBody.create(FORM_MEDIA, encryptedReq))
                    .build();

            Response resp = client.newCall(req).execute();
            if (!resp.isSuccessful() || resp.body() == null) return res;

            List<ProtobufField> fields = ProtobufEngine.decode(resp.body().bytes());
            for (ProtobufField f1 : fields) {
                if (f1.getFieldNum() == 1 && f1.getWireType() == 2) {
                    List<ProtobufField> sub = ProtobufEngine.decode(f1.getRawBytes());
                    for (ProtobufField s1 : sub) {
                        if (s1.getFieldNum() == 1 && s1.getWireType() == 2) {
                            List<ProtobufField> s1Fields = ProtobufEngine.decode(s1.getRawBytes());
                            for (ProtobufField sf : s1Fields) {
                                if (sf.getFieldNum() == 3) res.rankTitle = CharacterCatalog.getRankTitle(sf.getVarintValue());
                                else if (sf.getFieldNum() == 4) res.maxRankTitle = CharacterCatalog.getRankTitle(sf.getVarintValue());
                                else if (sf.getFieldNum() == 5) res.rankPoints = sf.getVarintValue();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return res;
    }

    private static class CsDetails {
        String rankTitle = "Unranked";
        long protectionPoints = 0L;
    }

    private CsDetails fetchCsRank(String jwtToken) {
        CsDetails res = new CsDetails();
        try {
            byte[] reqBytes = new byte[]{0x08, 0x01};
            byte[] encryptedReq = CryptoEngine.encrypt(reqBytes);

            Request req = new Request.Builder()
                    .url(AppConfig.CLIENT_GATEWAY_URL + "/InitPlayerCSRankingInfoEx")
                    .header("Host", "clientbp.ppmainecoonghj.com")
                    .header("User-Agent", "UnityPlayer/2018.4.12f1 (UnityWebRequest/1.0, libcurl/8.5.0-DEV)")
                    .header("Accept", "*/*")
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("X-GA", "v1 1")
                    .header("ReleaseVersion", AppConfig.RELEASE_VERSION)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .post(RequestBody.create(FORM_MEDIA, encryptedReq))
                    .build();

            Response resp = client.newCall(req).execute();
            if (!resp.isSuccessful() || resp.body() == null) return res;

            List<ProtobufField> fields = ProtobufEngine.decode(resp.body().bytes());
            for (ProtobufField f : fields) {
                if (f.getWireType() == 2) {
                    List<ProtobufField> inner = ProtobufEngine.decode(f.getRawBytes());
                    for (ProtobufField inf : inner) {
                        if (inf.getFieldNum() == 3) res.rankTitle = CharacterCatalog.getRankTitle(inf.getVarintValue());
                        else if (inf.getFieldNum() == 5 || inf.getFieldNum() == 14) res.protectionPoints = inf.getVarintValue();
                    }
                } else if (f.getFieldNum() == 3) {
                    res.rankTitle = CharacterCatalog.getRankTitle(f.getVarintValue());
                }
            }
        } catch (Exception ignored) {}
        return res;
    }

    private void postError(final ApiCallback callback, final String title, final String message) {
        mainHandler.post(() -> callback.onFailure(title, message));
    }

    private static String extractJwtFromBytes(byte[] bytes) {
        String asciiStr = new String(bytes, StandardCharsets.ISO_8859_1);
        int idx = asciiStr.indexOf("eyJ");
        if (idx == -1) return "";

        String rawToken = asciiStr.substring(idx);
        String[] parts = rawToken.split("\\.");
        if (parts.length >= 3) {
            String part3 = parts[2].length() > 43 ? parts[2].substring(0, 43) : parts[2];
            return parts[0] + "." + parts[1] + "." + part3;
        }

        int endIdx = -1;
        for (int i = 0; i < rawToken.length(); i++) {
            char c = rawToken.charAt(i);
            if (c <= ' ' || c == '"' || c == '}' || c == '\n' || c == '\r' || c == '\0') {
                endIdx = i;
                break;
            }
        }
        return endIdx != -1 ? rawToken.substring(0, endIdx) : rawToken;
    }

    private static byte[] replaceBytes(byte[] source, byte[] target, byte[] replacement) {
        if (source == null || target == null || source.length < target.length) return source;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i = 0;
        while (i <= source.length - target.length) {
            boolean matches = true;
            for (int j = 0; j < target.length; j++) {
                if (source[i + j] != target[j]) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                out.write(replacement, 0, replacement.length);
                i += target.length;
            } else {
                out.write(source[i]);
                i++;
            }
        }
        while (i < source.length) {
            out.write(source[i]);
            i++;
        }
        return out.toByteArray();
    }

    private static String parseError(String body, int httpCode) {
        try {
            JSONObject json = new JSONObject(body);
            String err = json.optString("error_description", json.optString("error", json.optString("msg", body)));
            if ("auth_error".equals(err)) return "Authentication Rejected (Invalid UID or Password)";
            if ("error_params".equals(err)) return "Parameter Format Error";
            if ("error_user_not_found".equals(err)) return "Guest Account does not exist";
            return err;
        } catch (Exception e) {
            return "Server returned HTTP " + httpCode + (body.isEmpty() ? "" : ": " + body.substring(0, Math.min(100, body.length())));
        }
    }
}
