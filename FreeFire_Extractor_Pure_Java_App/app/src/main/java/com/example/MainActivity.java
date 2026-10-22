package com.example;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.data.AccountHistoryManager;
import com.example.model.DeviceProfile;
import com.example.model.PlayerProfile;
import com.example.model.TokenReport;
import com.example.network.GameApiClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GameApiClient apiClient;
    private AccountHistoryManager historyManager;
    private CountDownTimer countdownTimer;

    // Views
    private View viewAuth;
    private View viewDashboard;

    // Auth Views
    private Button btnModeGuest;
    private Button btnModeJwt;
    private View containerGuestInputs;
    private View containerJwtInputs;
    private EditText inputGuestUid;
    private EditText inputGuestPassword;
    private EditText inputJwtToken;
    private TextView btnPastePassword;
    private TextView btnPasteJwt;
    private Button btnActionExtract;
    private TextView tvAuthError;
    private View containerLoading;
    private TextView tvLoadingText;

    // Dashboard Views
    private ImageView btnDashboardBack;
    private TextView tvDashNickname;
    private TextView tvDashAccountId;
    private TextView tvDashRegion;
    private TextView tvDashLevel;
    private TextView tvDashExp;
    private TextView tvDashCredit;

    private TabLayout tabLayoutDashboard;
    private View tabContentOverview;
    private View tabContentRanks;
    private View tabContentLoadout;
    private View tabContentToken;

    private TextView tvDashSignature;
    private TextView tvDashClan;
    private TextView tvDashLastLogin;
    private TextView tvDashBrRank;
    private TextView tvDashCsRank;
    private TextView tvDashCharacter;
    private TextView tvDashClothes;
    private TextView tvDashPet;
    private TextView tvDashCountdown;
    private TextView tvDashRawJwt;
    private Button btnCopyToken;

    private boolean isGuestMode = true;
    private String currentJwtToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GameApiClient();
        historyManager = new AccountHistoryManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        viewAuth = findViewById(R.id.view_auth);
        viewDashboard = findViewById(R.id.view_dashboard);

        btnModeGuest = findViewById(R.id.btn_mode_guest);
        btnModeJwt = findViewById(R.id.btn_mode_jwt);
        containerGuestInputs = findViewById(R.id.container_guest_inputs);
        containerJwtInputs = findViewById(R.id.container_jwt_inputs);

        inputGuestUid = findViewById(R.id.input_guest_uid);
        inputGuestPassword = findViewById(R.id.input_guest_password);
        inputJwtToken = findViewById(R.id.input_jwt_token);

        btnPastePassword = findViewById(R.id.btn_paste_password);
        btnPasteJwt = findViewById(R.id.btn_paste_jwt);
        btnActionExtract = findViewById(R.id.btn_action_extract);
        tvAuthError = findViewById(R.id.tv_auth_error);
        containerLoading = findViewById(R.id.container_loading);
        tvLoadingText = findViewById(R.id.tv_loading_text);

        btnDashboardBack = findViewById(R.id.btn_dashboard_back);
        tvDashNickname = findViewById(R.id.tv_dash_nickname);
        tvDashAccountId = findViewById(R.id.tv_dash_account_id);
        tvDashRegion = findViewById(R.id.tv_dash_region);
        tvDashLevel = findViewById(R.id.tv_dash_level);
        tvDashExp = findViewById(R.id.tv_dash_exp);
        tvDashCredit = findViewById(R.id.tv_dash_credit);

        tabLayoutDashboard = findViewById(R.id.tab_layout_dashboard);
        tabContentOverview = findViewById(R.id.tab_content_overview);
        tabContentRanks = findViewById(R.id.tab_content_ranks);
        tabContentLoadout = findViewById(R.id.tab_content_loadout);
        tabContentToken = findViewById(R.id.tab_content_token);

        tvDashSignature = findViewById(R.id.tv_dash_signature);
        tvDashClan = findViewById(R.id.tv_dash_clan);
        tvDashLastLogin = findViewById(R.id.tv_dash_last_login);
        tvDashBrRank = findViewById(R.id.tv_dash_br_rank);
        tvDashCsRank = findViewById(R.id.tv_dash_cs_rank);
        tvDashCharacter = findViewById(R.id.tv_dash_character);
        tvDashClothes = findViewById(R.id.tv_dash_clothes);
        tvDashPet = findViewById(R.id.tv_dash_pet);
        tvDashCountdown = findViewById(R.id.tv_dash_countdown);
        tvDashRawJwt = findViewById(R.id.tv_dash_raw_jwt);
        btnCopyToken = findViewById(R.id.btn_copy_token);

        // Setup Tabs
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Overview"));
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Ranks & Scores"));
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Character & Loadout"));
        tabLayoutDashboard.addTab(tabLayoutDashboard.newTab().setText("Live Token Analytics"));
    }

    private void setupListeners() {
        btnModeGuest.setOnClickListener(v -> switchMode(true));
        btnModeJwt.setOnClickListener(v -> switchMode(false));

        btnPastePassword.setOnClickListener(v -> {
            String clip = getClipboardText();
            if (!clip.isEmpty()) {
                inputGuestPassword.setText(clip.replaceAll("\\s+", ""));
            }
        });

        btnPasteJwt.setOnClickListener(v -> {
            String clip = getClipboardText();
            if (!clip.isEmpty()) {
                inputJwtToken.setText(clip.trim());
            }
        });

        btnActionExtract.setOnClickListener(v -> {
            hideError();
            if (isGuestMode) {
                String uid = inputGuestUid.getText().toString().trim();
                String pwd = inputGuestPassword.getText().toString().trim();
                if (uid.length() < 8) {
                    showError("Invalid UID: Guest UID must be between 8 and 12 digits.");
                    return;
                }
                if (pwd.length() < 16) {
                    showError("Invalid Password: Password must be a 64-character hex string.");
                    return;
                }
                showDeviceSelectorDialog(uid, pwd);
            } else {
                String jwt = inputJwtToken.getText().toString().trim();
                if (jwt.isEmpty() || !jwt.startsWith("eyJ")) {
                    showError("Invalid JWT Token: Token must start with 'eyJ...'");
                    return;
                }
                performJwtExtraction(jwt);
            }
        });

        btnDashboardBack.setOnClickListener(v -> showAuthScreen());

        tabLayoutDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                tabContentOverview.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
                tabContentRanks.setVisibility(pos == 1 ? View.VISIBLE : View.GONE);
                tabContentLoadout.setVisibility(pos == 2 ? View.VISIBLE : View.GONE);
                tabContentToken.setVisibility(pos == 3 ? View.VISIBLE : View.GONE);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnCopyToken.setOnClickListener(v -> {
            if (!currentJwtToken.isEmpty()) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Free Fire JWT", currentJwtToken);
                cm.setPrimaryClip(clip);
                Toast.makeText(this, "8-Hour Session JWT Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchMode(boolean guest) {
        this.isGuestMode = guest;
        hideError();
        if (guest) {
            btnModeGuest.setBackgroundResource(R.drawable.bg_button_primary);
            btnModeGuest.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnModeJwt.setBackgroundColor(0);
            btnModeJwt.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            containerGuestInputs.setVisibility(View.VISIBLE);
            containerJwtInputs.setVisibility(View.GONE);
            btnActionExtract.setText(R.string.btn_continue);
        } else {
            btnModeJwt.setBackgroundResource(R.drawable.bg_button_primary);
            btnModeJwt.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnModeGuest.setBackgroundColor(0);
            btnModeGuest.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            containerJwtInputs.setVisibility(View.VISIBLE);
            containerGuestInputs.setVisibility(View.GONE);
            btnActionExtract.setText(R.string.btn_extract_jwt);
        }
    }

    private void showDeviceSelectorDialog(final String uid, final String pwd) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.dialog_device_selector, null);
        dialog.setContentView(sheetView);

        final RadioGroup rg = sheetView.findViewById(R.id.rg_device_options);
        final Button btnConfirm = sheetView.findViewById(R.id.btn_confirm_device);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            int checkedId = rg.getCheckedRadioButtonId();
            DeviceProfile device = (checkedId == R.id.rb_device_emulator)
                    ? DeviceProfile.EMULATOR_PC
                    : DeviceProfile.MOBILE_ZTE;
            performGuestExtraction(uid, pwd, device);
        });

        dialog.show();
    }

    private void performGuestExtraction(final String uid, final String pwd, final DeviceProfile device) {
        setLoading(true, "Authenticating with Garena & MajorLogin...");
        apiClient.extractFromCredentials(uid, pwd, device, new GameApiClient.ApiCallback() {
            @Override
            public void onSuccess(PlayerProfile profile, TokenReport tokenReport) {
                setLoading(false, "");
                historyManager.saveAccount(uid, pwd, profile.getNickname(), profile.getLevel());
                populateDashboard(profile, tokenReport);
            }

            @Override
            public void onFailure(String errorTitle, String errorMessage) {
                setLoading(false, "");
                showError(errorTitle + ": " + errorMessage);
            }
        });
    }

    private void performJwtExtraction(final String jwt) {
        setLoading(true, "Fetching Player Profile from Game Server...");
        apiClient.extractFromJwt(jwt, new GameApiClient.ApiCallback() {
            @Override
            public void onSuccess(PlayerProfile profile, TokenReport tokenReport) {
                setLoading(false, "");
                populateDashboard(profile, tokenReport);
            }

            @Override
            public void onFailure(String errorTitle, String errorMessage) {
                setLoading(false, "");
                showError(errorTitle + ": " + errorMessage);
            }
        });
    }

    private void populateDashboard(PlayerProfile profile, TokenReport tokenReport) {
        currentJwtToken = profile.getRawJwtToken();

        tvDashNickname.setText(profile.getNickname());
        tvDashAccountId.setText("Account ID: " + profile.getAccountId());
        tvDashRegion.setText(profile.getRegion());
        tvDashLevel.setText("Level " + profile.getLevel());
        tvDashExp.setText(String.format(Locale.US, "%,d", profile.getExp()));
        tvDashCredit.setText(profile.getCreditScore() + "/100");

        tvDashSignature.setText(profile.getSignature());
        tvDashClan.setText(profile.getClanName());
        tvDashLastLogin.setText(profile.getLastLoginTimestamp());

        tvDashBrRank.setText(profile.getBrRankTitle() + " (" + String.format(Locale.US, "%,d pts", profile.getBrRankPoints()) + ")");
        tvDashCsRank.setText(profile.getCsRankTitle());

        tvDashCharacter.setText(profile.getActiveCharacterName() + " [ID: " + profile.getActiveCharacterId() + "]");

        if (profile.getClothesIds() != null && !profile.getClothesIds().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Long id : profile.getClothesIds()) {
                sb.append(id).append(", ");
            }
            tvDashClothes.setText(sb.substring(0, sb.length() - 2));
        } else {
            tvDashClothes.setText("Default Clothing");
        }

        if (profile.getPetId() > 0) {
            tvDashPet.setText(profile.getPetSpecies() + " (" + profile.getPetName() + ") [Lv. " + profile.getPetLevel() + "]");
        } else {
            tvDashPet.setText("No Pet Equipped");
        }

        tvDashRawJwt.setText(currentJwtToken);

        // Start countdown timer
        startCountdown(tokenReport.getRemainingSeconds());

        // Switch to Dashboard View
        viewAuth.setVisibility(View.GONE);
        viewDashboard.setVisibility(View.VISIBLE);
    }

    private void startCountdown(long remainingSeconds) {
        if (countdownTimer != null) countdownTimer.cancel();
        countdownTimer = new CountDownTimer(remainingSeconds * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000L;
                long hours = sec / 3600;
                long minutes = (sec % 3600) / 60;
                long seconds = sec % 60;
                tvDashCountdown.setText(String.format(Locale.US, "%02dh %02dm %02ds", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvDashCountdown.setText("Session Expired");
            }
        }.start();
    }

    private void showAuthScreen() {
        if (countdownTimer != null) countdownTimer.cancel();
        viewDashboard.setVisibility(View.GONE);
        viewAuth.setVisibility(View.VISIBLE);
    }

    private void setLoading(boolean loading, String text) {
        containerLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnActionExtract.setEnabled(!loading);
        if (loading) tvLoadingText.setText(text);
    }

    private void showError(String msg) {
        tvAuthError.setText(msg);
        tvAuthError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvAuthError.setVisibility(View.GONE);
    }

    private String getClipboardText() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null && cm.hasPrimaryClip() && cm.getPrimaryClip().getItemCount() > 0) {
            CharSequence text = cm.getPrimaryClip().getItemAt(0).getText();
            return text != null ? text.toString() : "";
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        if (countdownTimer != null) countdownTimer.cancel();
        super.onDestroy();
    }
}
