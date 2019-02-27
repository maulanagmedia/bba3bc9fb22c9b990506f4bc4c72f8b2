package gmedia.net.id.finance.DetailPengajuan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.finance.DetailPengajuan.Adapter.DetailPengajuanAdapter;
import gmedia.net.id.finance.HistoryPengajuan;
import gmedia.net.id.finance.R;
import gmedia.net.id.finance.Utils.ServerUrl;

public class DetailPengajuan extends AppCompatActivity {

    private static ListView lvPengajuan;
    private static ProgressBar pbLoading;
    private static Button btnRefresh;
    private static ItemValidation iv = new ItemValidation();
    private static SessionManager session;
    private static List<CustomItem> listPengajuan;
    private static Context context;
    private static String idHeader = "";
    private static Button btnApprove, btnReject;
    private String nomorPengajuan;
    private static TextView tvTotal;
    private boolean isHistory = false;
    private LinearLayout llFooter;
    private static double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengajuan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        setTitle("Detail Pengajuan");

        initIU();
    }

    private void initIU() {

        lvPengajuan = (ListView) findViewById(R.id.lv_pengajuan);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        btnApprove = (Button) findViewById(R.id.btn_approve);
        btnReject = (Button) findViewById(R.id.btn_reject);
        llFooter = (LinearLayout) findViewById(R.id.ll_footer);
        tvTotal.setText("Total: " + iv.ChangeToRupiahFormat(total));

        session = new SessionManager(context);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            idHeader = bundle.getString("id_header");
            if(idHeader != null && idHeader.length() > 0){
                nomorPengajuan = bundle.getString("nomor");
                isHistory = bundle.getBoolean("is_history", false);
                if(isHistory){
                    llFooter.setVisibility(View.GONE);
                }
                getSupportActionBar().setSubtitle(nomorPengajuan);
                getDetailPengajuan();
                initEvent();
            }else{
                idHeader = "";
            }
        }
    }

    private void initEvent() {

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnRefresh.setVisibility(View.GONE);
                if(idHeader.length() > 0)
                getDetailPengajuan();
            }
        });

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher))
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin menyetujui pengajuan "+ nomorPengajuan +" ?" )
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                updatePengajuan(idHeader, true, "");
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.layout_reject_reason, null);
                builder.setView(viewDialog);

                final EditText edtKeterangan= (EditText) viewDialog.findViewById(R.id.edt_keterangan);
                final ImageView ivSend = (ImageView) viewDialog.findViewById(R.id.iv_send);

                final AlertDialog alert = builder.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                ivSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher))
                                .setTitle("Konfirmasi")
                                .setMessage("Anda yakin menolak pengajuan " + nomorPengajuan + " ?" )
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(alert != null) alert.dismiss();
                                        updatePengajuan(idHeader, false, edtKeterangan.getText().toString());
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();

                    }
                });

                alert.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private static void getDetailPengajuan() {

        pbLoading.setVisibility(View.VISIBLE);
        listPengajuan = new ArrayList<>();
        total = 0;

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_header", idHeader);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerUrl.getDetailPengajuan, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listPengajuan = new ArrayList<>();
                    total = 0;

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listPengajuan.add(new CustomItem(
                                    jo.getString("id")
                                    ,jo.getString("pemohon")
                                    ,jo.getString("rekening_tujuan")
                                    ,jo.getString("nominal")
                                    ,jo.getString("keterangan")
                                    ,jo.getString("tujuan_pembayaran")
                                    ,jo.getString("date")
                                    ,jo.getString("id_po")
                                    ,jo.getString("bukti")
                                    ,jo.getString("link")
                            ));

                            total += iv.parseNullDouble(jo.getString("nominal"));
                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    setDetailPengajuanAdapter(listPengajuan);
                    pbLoading.setVisibility(View.GONE);
                    btnRefresh.setVisibility(View.GONE);
                    tvTotal.setText("Total: " + iv.ChangeToRupiahFormat(total));
                } catch (JSONException e) {
                    e.printStackTrace();
                    setDetailPengajuanAdapter(null);
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String result) {
                setDetailPengajuanAdapter(null);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private static void setDetailPengajuanAdapter(List<CustomItem> listItem) {

        lvPengajuan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            DetailPengajuanAdapter adapter = new DetailPengajuanAdapter((Activity) context, listItem);
            lvPengajuan.setAdapter(adapter);
        }
    }

    public void updatePengajuan(final String id, final boolean isApproved, final String keterangan) {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jData = new JSONObject();

        try {
            jData.put("status", isApproved ? "1" : "2");
            jData.put("reason", keterangan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("id", id);
            jBody.put("data", jData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerUrl.updatePengajuan, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "Terjadi kesalahan saat mengakses data";
                try {

                    pbLoading.setVisibility(View.GONE);
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        message = response.getJSONObject("response").getString("message");
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                        showDialog(1, id, isApproved, keterangan, "");

                        //onBackPressed();
                    }else{
                        message = "Data tidak tersimpan";
                        showDialog(2, id, isApproved, keterangan, message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    pbLoading.setVisibility(View.GONE);
                    showDialog(2, id, isApproved, keterangan, message);
                }
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                showDialog(2, id, isApproved, keterangan, "Terjadi kesalahan saat mengases data");
            }
        });
    }

    private void showDialog(int state, final String id, final boolean isApproved, final String keterangan, String message){

        if(state == 1){

            if(isApproved){

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.layout_success, null);
                builder.setView(viewDialog);
                builder.setCancelable(false);

                final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
                tvText1.setText("Pengajuan berhasil disetujui");
                final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);

                final AlertDialog alert = builder.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {

                        if(alert != null) alert.dismiss();
                        onBackPressed();
                    }
                });

                alert.show();
            }else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.layout_warning, null);
                builder.setView(viewDialog);
                builder.setCancelable(false);

                final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
                tvText1.setText("Pengajuan berhasil ditolak");
                final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);

                final AlertDialog alert = builder.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {

                        if(alert != null) alert.dismiss();
                        onBackPressed();
                    }
                });

                alert.show();
            }
        }else if(state == 2){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate(R.layout.layout_failed, null);
            builder.setView(viewDialog);
            builder.setCancelable(false);

            final TextView tvText1 = (TextView) viewDialog.findViewById(R.id.tv_text1);
            tvText1.setText(message);
            final Button btnOK = (Button) viewDialog.findViewById(R.id.btn_ok);

            final AlertDialog alert = builder.create();
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {

                    if(alert != null) alert.dismiss();
                    updatePengajuan(id, isApproved, keterangan);
                }
            });

            alert.show();
        }

    }

    public static void updateDetailPengajuan(String id, boolean isApproved, String keterangan) {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jData = new JSONObject();

        try {
            jData.put("status", isApproved ? "1" : "2");
            jData.put("reason", keterangan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("id", id);
            jBody.put("data", jData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerUrl.updateDetailPengajuan, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                String message = "Terjadi kesalahan saat mengakses data, harap ulangi kembali";
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        message = response.getJSONObject("response").getString("message");
                        getDetailPengajuan();
                    }

                    pbLoading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    pbLoading.setVisibility(View.GONE);
                }

                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail_pengajuan, menu);

        menu.getItem(0).setVisible(!isHistory);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_history:
                Intent intent = new Intent(DetailPengajuan.this, HistoryPengajuan.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
