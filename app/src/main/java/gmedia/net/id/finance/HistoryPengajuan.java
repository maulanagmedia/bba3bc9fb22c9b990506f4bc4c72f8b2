package gmedia.net.id.finance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import gmedia.net.id.finance.Adapter.ListPengajuanAdapter;
import gmedia.net.id.finance.Utils.ServerUrl;

public class HistoryPengajuan extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_pengajuan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("History");

        initUI();
    }

    private void initUI() {

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

    @Override
    protected void onResume() {
        super.onResume();

        startIndex = 0;
        keyword = "";
        getHistory();
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

        pbLoading.setVisibility(View.VISIBLE);
        masterList = new ArrayList<>();

        JSONObject jBody = new JSONObject();
        try {
            /*jBody.put("keyword", edtSearch.getText().toString());*/
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(HistoryPengajuan.this, jBody, "POST", ServerUrl.getHistory, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    masterList = new ArrayList<>();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length(); i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            masterList.add(new CustomItem(jo.getString("id"), jo.getString("nama"), jo.getString("keterangan"), jo.getString("timestamp"),  jo.getString("urgent"), jo.getString("status")));
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
        }
    }

    private AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = lvHistory.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (lvHistory.getLastVisiblePosition() >= count - threshold && !isLoading) {

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
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(HistoryPengajuan.this, jBody, "POST", ServerUrl.getPengajuan, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            moreList.add(new CustomItem(jo.getString("id"), jo.getString("nama"), jo.getString("keterangan"), jo.getString("timestamp"),  jo.getString("urgent"), jo.getString("status")));
                        }

                        lvHistory.removeFooterView(footerList);
                        if(historyAdapter!= null) historyAdapter.addMoreData(moreList);
                    }
                    isLoading = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    isLoading = false;
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
