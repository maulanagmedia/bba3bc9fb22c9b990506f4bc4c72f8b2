package gmedia.net.id.finance;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import gmedia.net.id.finance.Adapter.ListHistoryAdapter;
import gmedia.net.id.finance.DetailPengajuan.DetailPengajuan;
import gmedia.net.id.finance.Utils.ServerUrl;

public class HistoryPengajuan extends AppCompatActivity {

    private Context context;
    private ListView lvHistory;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private ItemValidation iv = new ItemValidation();
    private List<CustomItem> masterList;
    private boolean firstLoad;
    private View footerList;
    private boolean isLoading = false;
    private boolean isOnSearch = false;
    private SessionManager session;
    private int startIndex = 0;
    private int count = 10;
    private String keyword = "";
    private ListHistoryAdapter historyAdapter;
    private Spinner spBulan, spTahun;
    private String bulanNow = "", tahunNow = "";
    private List<CustomItem> listBulanTahun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_pengajuan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("History");
        context = this;
        initUI();
    }

    private void initUI() {

        spBulan = (Spinner) findViewById(R.id.sp_bulan);
        spTahun = (Spinner) findViewById(R.id.sp_tahun);
        lvHistory = (ListView) findViewById(R.id.lv_history);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        //inital
        isLoading = false;
        isOnSearch = false;
        session = new SessionManager(HistoryPengajuan.this);
        masterList = new ArrayList<>();

        initEvent();

        startIndex = 0;
        keyword = "";
    }

    private void getTahunHeader() {

        pbLoading.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(HistoryPengajuan.this, new JSONObject(), "GET", ServerUrl.getTahunHeader, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    listBulanTahun = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listBulanTahun.add(new CustomItem(jo.getString("month"), jo.getString("year")));
                            if(i == 0) {
                                bulanNow = jo.getString("month");
                                tahunNow = jo.getString("year");
                            }
                        }
                    }

                    parseData(listBulanTahun);
                    pbLoading.setVisibility(View.GONE);
                } catch (JSONException e) {

                    e.printStackTrace();
                    parseData(listBulanTahun);
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(HistoryPengajuan.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String result) {
                parseData(listBulanTahun);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(HistoryPengajuan.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseData(final List<CustomItem> listItem){

        if(listItem != null && listItem.size() > 0){

            // Bulan
            List<String> bulanList = new ArrayList<>();

            bulanList.add("Januari");
            bulanList.add("Februari");
            bulanList.add("Maret");
            bulanList.add("April");
            bulanList.add("Mei");
            bulanList.add("Juni");
            bulanList.add("Juli");
            bulanList.add("Agustus");
            bulanList.add("September");
            bulanList.add("Oktober");
            bulanList.add("November");
            bulanList.add("Desember");
            setBulanAdapter(bulanList);

            // Tahun
            List<String> tahunList = new ArrayList<>();

            for(CustomItem item: listItem){

                tahunList.add(item.getItem2());
            }

            setTahunAdapter(tahunList);

        }

        spBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spBulan.getAdapter() != null && spTahun.getAdapter() != null){

                    if(spBulan.getSelectedView() != null){

                        ((TextView) spBulan.getSelectedView()).setTextColor(getResources().getColor(R.color.color_white));
                    }
                    bulanNow = String.valueOf(position+1);
                    startIndex = 0;
                    getHistory();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        spTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spBulan.getAdapter() != null && spTahun.getAdapter() != null){

                    ((TextView) spTahun.getSelectedView()).setTextColor(getResources().getColor(R.color.color_white));
                    tahunNow = parent.getItemAtPosition(position).toString();
                    startIndex = 0;
                    getHistory();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getHistory();
    }

    private void setBulanAdapter(List<String> listItem){

        spBulan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(HistoryPengajuan.this, R.layout.layout_simple_list, listItem);
            spBulan.setAdapter(adapter);
            spBulan.setSelection(0);
            if(bulanNow != null && bulanNow.length() > 0){
                spBulan.setSelection(iv.parseNullInteger(bulanNow) - 1);
            }
        }
    }

    private void setTahunAdapter(List<String> listItem){

        spTahun.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            ArrayAdapter adapter = new ArrayAdapter(HistoryPengajuan.this, R.layout.layout_simple_list, listItem);
            spTahun.setAdapter(adapter);
            spTahun.setSelection(0);
            if(tahunNow != null && tahunNow.length() > 0){
                int x = 0;
                for(String item:listItem){
                    if(item.equals(tahunNow)) spTahun.setSelection(x);
                    x++;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        keyword = "";
        getTahunHeader();
    }

    private void initEvent() {

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startIndex = 0;
                getHistory();
                btnRefresh.setVisibility(View.GONE);
            }
        });
    }

    private void getHistory() {

        startIndex = 0;
        pbLoading.setVisibility(View.VISIBLE);
        masterList = new ArrayList<>();
        isLoading = true;

        JSONObject jBody = new JSONObject();
        try {
            /*jBody.put("keyword", edtSearch.getText().toString());*/
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
            jBody.put("bulan", bulanNow);
            jBody.put("tahun", tahunNow);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(HistoryPengajuan.this, jBody, "POST", ServerUrl.getHistory, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            masterList.add(new CustomItem(jo.getString("id"),
                                    jo.getString("nama"),
                                    jo.getString("keterangan"),
                                    jo.getString("updated_time"),
                                    jo.getString("urgent"),
                                    jo.getString("status"),
                                    jo.getString("reason"),
                                    jo.getString("nomor"),
                                    iv.parseNullString(jo.getString("sumber")).toString().toLowerCase(),
                                    jo.getString("title")
                            ));
                        }
                    }

                    setPengajuanAdapter(masterList);
                    pbLoading.setVisibility(View.GONE);
                } catch (JSONException e) {

                    e.printStackTrace();
                    setPengajuanAdapter(null);
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(HistoryPengajuan.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String result) {
                isLoading = false;
                setPengajuanAdapter(null);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(HistoryPengajuan.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setPengajuanAdapter(List<CustomItem> listItem){

        lvHistory.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            historyAdapter = new ListHistoryAdapter(HistoryPengajuan.this, listItem);
            lvHistory.setAdapter(historyAdapter);

            lvHistory.setOnScrollListener(onScrollListener());

            lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);

                    Intent intent = new Intent(HistoryPengajuan.this, DetailPengajuan.class);
                    intent.putExtra("id_header", item.getItem1());
                    intent.putExtra("nomor", item.getItem8());
                    intent.putExtra("is_history", true);
                    startActivity(intent);


                    //getDetailPengajuan(item.getItem1());
                }
            });

            lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah anda yakin ingin membatalkan putusan pengajuan "+item.getItem3()+" ?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    batalkanPengajuan(item.getItem1());
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();

                    return true;
                }
            });
        }
    }

    private void batalkanPengajuan(final String id) {

        pbLoading.setVisibility(View.VISIBLE);
        JSONObject jData = new JSONObject();

        try {
            jData.put("status", "0");
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
                        getHistory();
                        //onBackPressed();
                    }else{
                        message = "Data tidak tersimpan";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    pbLoading.setVisibility(View.GONE);
                }

                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String result) {

                pbLoading.setVisibility(View.GONE);
                Toast.makeText(context, "Terjadi kesalahan saat mengases data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int countList = lvHistory.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (lvHistory.getLastVisiblePosition() >= countList - threshold && !isLoading) {

                        isLoading = true;
                        lvHistory.addFooterView(footerList);
                        startIndex += count;
                        getMoreData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }

        };
    }

    private void getMoreData() {

        isLoading = true;
        final List<CustomItem> moreList = new ArrayList<>();
        JSONObject jBody = new JSONObject();

        try {
            /*jBody.put("keyword", edtSearch.getText().toString());*/
            jBody.put("keyword", keyword);
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
            jBody.put("bulan", bulanNow);
            jBody.put("tahun", tahunNow);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(HistoryPengajuan.this, jBody, "POST", ServerUrl.getHistory, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            moreList.add(new CustomItem(jo.getString("id")
                                    , jo.getString("nama")
                                    , jo.getString("keterangan")
                                    , jo.getString("updated_time")
                                    ,  jo.getString("urgent")
                                    , jo.getString("status")
                                    , jo.getString("reason")
                                    , jo.getString("nomor")
                                    , iv.parseNullString(jo.getString("sumber"))
                                    , jo.getString("title")
                            ));
                        }

                        lvHistory.removeFooterView(footerList);
                        if(historyAdapter!= null) historyAdapter.addMoreData(moreList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    lvHistory.removeFooterView(footerList);
                }
            }

            @Override
            public void onError(String result) {
                isLoading = false;
                lvHistory.removeFooterView(footerList);
            }
        });
    }

    @Override
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

                startIndex = 0;
                keyword = queryText;
                iv.hideSoftKey(HistoryPengajuan.this);
                getHistory();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newFilter = !TextUtils.isEmpty(newText) ? newText : "";
                if(newText.length() == 0){

                    startIndex = 0;
                    keyword = "";
                    getHistory();
                }

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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
