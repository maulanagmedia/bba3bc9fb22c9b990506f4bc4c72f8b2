package gmedia.net.id.finance.DetailPengajuan;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import gmedia.net.id.finance.DetailPengajuan.Adapter.ListBarangAdapter;
import gmedia.net.id.finance.R;
import gmedia.net.id.finance.Utils.ServerUrl;

public class ListBarangPengajuan extends AppCompatActivity {

    private Context context;
    private ListView lvBarang;
    private ProgressBar pbLoading;
    private TextView tvPo, tvOrderTo, tvSubtotal, tvPPn, tvDiskon, tvOngkir, tvTotal;
    private String idPo = "";
    private List<CustomItem> masterList = new ArrayList<>();
    private ListBarangAdapter adapter;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang_pengajuan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Purchase Order");

        context = this;
        session = new SessionManager(context);
        initUI();
        initData();
    }

    private void initUI() {

        lvBarang = (ListView) findViewById(R.id.lv_barang);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        tvPo = (TextView) findViewById(R.id.tv_po);
        tvOrderTo = (TextView) findViewById(R.id.tv_order_to);
        tvSubtotal = (TextView) findViewById(R.id.tv_subtotal);
        tvPPn = (TextView) findViewById(R.id.tv_ppn);
        tvDiskon = (TextView) findViewById(R.id.tv_diskon);
        tvOngkir = (TextView) findViewById(R.id.tv_ongkir);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            idPo = bundle.getString("id","");
        }

        masterList = new ArrayList<>();
        adapter = new ListBarangAdapter((Activity) context, masterList);
        lvBarang.setAdapter(adapter);

    }

    private void initData() {

        pbLoading.setVisibility(View.VISIBLE);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_po", idPo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerUrl.getBarangPO, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbLoading.setVisibility(View.GONE);
                masterList.clear();

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jResponse = response.getJSONObject("response");
                        JSONObject jHeader = jResponse.getJSONObject("header");
                        tvPo.setText(jHeader.getString("nomor"));
                        tvOrderTo.setText(jHeader.getString("perusahaan"));
                        String symbol = jHeader.getString("symbol");
                        tvSubtotal.setText(symbol + " " + iv.ChangeToCurrencyFormat(jHeader.getString("subtotal")));
                        tvPPn.setText(symbol + " " + iv.ChangeToCurrencyFormat(jHeader.getString("ppn")));
                        tvDiskon.setText(symbol + " " + iv.ChangeToCurrencyFormat(jHeader.getString("diskon")));
                        tvOngkir.setText(symbol + " " + iv.ChangeToCurrencyFormat(jHeader.getString("ongkir")));
                        tvTotal.setText(symbol + " " + iv.ChangeToCurrencyFormat(jHeader.getString("total")));

                        JSONArray jsonArray = jResponse.getJSONArray("detail");

                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            masterList.add(new CustomItem(
                                    jo.getString("id")
                                    ,jo.getString("nama_barang")
                                    ,jo.getString("qty")
                                    ,jo.getString("nama_ukuran")
                                    ,jo.getString("harga")
                                    ,jo.getString("total")
                                    ,jo.getString("keperluan")
                                    ,jo.getString("keterangan")
                                    ,jo.getString("symbol")
                            ));

                        }
                    }else{

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
