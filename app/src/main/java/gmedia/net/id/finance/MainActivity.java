package gmedia.net.id.finance;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.finance.Adapter.ListPengajuanAdapter;

public class MainActivity extends AppCompatActivity {

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;

    private static ListView lvPengajuan;
    private static List<CustomItem> masterList;
    private static ProgressBar pbLoading;
    private static int startIndex = 0;
    private static int count = 10;
    private static String keyword = "";
    private static ItemValidation iv = new ItemValidation();
    private ImageView ivSearch, ivHistory;
    private TextView tvTitle;
    private EditText edtSearch;
    private boolean isOnSearch = false;
    private ListPengajuanAdapter pengajuanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        initUI();
    }

    private void initUI() {

        ivSearch = (ImageView) findViewById(R.id.iv_search);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        ivHistory = (ImageView) findViewById(R.id.iv_history);
        lvPengajuan = (ListView) findViewById(R.id.lv_pengajuan);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        //inital
        isOnSearch = false;
        masterList = new ArrayList<>();

        initEvent();

        startIndex = 0;
        getDataPengajuan();
    }

    private void initEvent() {

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnSearch){
                    isOnSearch = false;
                    tvTitle.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.VISIBLE);
                }else{
                    isOnSearch = true;
                    tvTitle.setVisibility(View.VISIBLE);
                    edtSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getDataPengajuan() {

        pbLoading.setVisibility(View.VISIBLE);
        masterList = new ArrayList<>();

        pbLoading.setVisibility(View.GONE);
        masterList.add(new CustomItem("1","Sianne Hoo", "Pengajuan Transfer Dana", "Mohon disetejui pengajuan untuk operasional",  "2017-12-18", "2"));
        masterList.add(new CustomItem("2","Sianne Hoo", "Pengajuan Transfer Dana", "Mohon disetejui pengajuan untuk operasional", "2017-12-17", "1"));
        masterList.add(new CustomItem("3","Sianne Hoo", "Pengajuan Transfer Dana", "Mohon disetejui pengajuan untuk operasional", "2017-12-16", "3"));
        setPengajuanAdapter(masterList);

        /*JSONObject jBody = new JSONObject();
        try {
            jBody.put("nomeja", edtNoMeja.getText().toString());
            jBody.put("tgl", iv.ChangeFormatDateString(edtTanggal.getText().toString(), FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("start_index", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", serverURL.getRiwayatOrder(), "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listTransaksi = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listTransaksi.add(new CustomItem(jo.getString("nobukti"), jo.getString("urutan"), jo.getString("pelanggan"), jo.getString("total"), jo.getString("usertgl"), jo.getString("nomeja"), jo.getString("nama"), jo.getString("jml_item"), jo.getString("cashier_status"), jo.getString("kitchen_status"), jo.getString("bar_status"), jo.getString("print_no"), jo.getString("status"), jo.getString("jumlah_plg")));
                        }
                    }

                    getListTransaksi(listTransaksi);
                    pbLoadTransaksi.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    getListTransaksi(null);
                    pbLoadTransaksi.setVisibility(View.GONE);
                    Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {
                pbLoadTransaksi.setVisibility(View.GONE);
                getListTransaksi(null);
                Toast.makeText(context, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
            }
        });*/
    }

    private void setPengajuanAdapter(List<CustomItem> listItem){

        lvPengajuan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            pengajuanAdapter = new ListPengajuanAdapter(MainActivity.this, listItem);
            lvPengajuan.setAdapter(pengajuanAdapter);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

                return true;
            }
        });

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public void onBackPressed() {

        // Origin backstage
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
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
