package gmedia.net.id.finance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.finance.NotificationUtil.InitFirebaseSetting;
import gmedia.net.id.finance.Utils.ServerUrl;

public class LoginScreen extends AppCompatActivity {

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;

    private String refreshToken = "";
    private EditText edtUsername;
    private EditText edtPassword;
    private CheckBox cbRemeber;
    private Button btnLogin;
    private SessionManager session;
    private boolean visibleTapped;
    private ItemValidation iv = new ItemValidation();
    private ImageView ivVisible;
    private String username = "",password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Check close statement
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {
                exitState = true;
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }

        InitFirebaseSetting.getFirebaseSetting(LoginScreen.this);
        refreshToken = FirebaseInstanceId.getInstance().getToken();
        initUI();
    }

    private void initUI() {

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        cbRemeber = (CheckBox) findViewById(R.id.cb_save);
        ivVisible = (ImageView) findViewById(R.id.iv_visible);
        btnLogin = (Button) findViewById(R.id.btn_login);

        visibleTapped = true;
        session = new SessionManager(LoginScreen.this);

        if (session.isSaved()) {

            edtUsername.setText(session.getUserDetails().get(SessionManager.TAG_USERNAME));
            password = iv.decodeBase64(session.getUserDetails().get(SessionManager.TAG_PASSWORD));
            edtPassword.setText(password);
            cbRemeber.setChecked(true);
            validasiLogin();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validasiLogin();
            }
        });

        ivVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visibleTapped) {
                    edtPassword.setTransformationMethod(null);
                    edtPassword.setSelection(edtPassword.getText().length());
                    visibleTapped = false;
                    ivVisible.setImageResource(R.mipmap.ic_invisible);
                } else {
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    edtPassword.setSelection(edtPassword.getText().length());
                    visibleTapped = true;
                    ivVisible.setImageResource(R.mipmap.ic_visible);
                }
            }
        });
    }

    private void validasiLogin() {

        if (edtUsername.getText().length() <= 0) {
            edtUsername.setError("Username tidak boleh kosong");
            edtUsername.requestFocus();
            return;
        }else{
            edtUsername.setError(null);
        }

        if (edtPassword.getText().length() <= 0) {
            edtPassword.setError("Password tidak boleh kosong");
            edtPassword.requestFocus();
            return;
        }else{
            edtPassword.setError(null);
        }

        login();
    }

    private void login() {

        final ProgressDialog progressDialog = new ProgressDialog(LoginScreen.this, R.style.AppTheme_Login_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        password = iv.encodeBase64(edtPassword.getText().toString());

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("username", edtUsername.getText().toString());
            jBody.put("password", password);
            jBody.put("fcm_id", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(LoginScreen.this, jBody, "POST", ServerUrl.login, "", "", 0, edtUsername.getText().toString(), edtPassword.getText().toString(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "Terjadi kesalahan saat mengakses data, harap ulangi";

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");
                    if(iv.parseNullInteger(status) == 200){

                        String uid = response.getJSONObject("response").getString("id_employee");
                        String email = response.getJSONObject("response").getString("email");
                        String nama = response.getJSONObject("response").getString("nama");
                        session.createLoginSession(uid, email, nama ,edtUsername.getText().toString(),password, (cbRemeber.isChecked())? "1": "0");
                        Toast.makeText(LoginScreen.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }else{
                        Toast.makeText(LoginScreen.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginScreen.this, message, Toast.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onError(String result) {

                Toast.makeText(LoginScreen.this, "Terjadi kesalahan saat mengakses data, harap ulangi", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {

        // Origin backstage
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(LoginScreen.this, LoginScreen.class);
            intent.putExtra("exit", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //System.exit(0);
        }

        if(!exitState && !doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, timerClose);
    }
}
