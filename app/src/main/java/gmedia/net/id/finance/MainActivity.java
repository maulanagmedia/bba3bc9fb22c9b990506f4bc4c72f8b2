package gmedia.net.id.finance;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.finance.Adapter.ListPengajuanAdapter;
import gmedia.net.id.finance.DetailPengajuan.DetailPengajuan;
import gmedia.net.id.finance.Utils.ServerUrl;

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
    private static SessionManager session;
    private ImageView ivSearch, ivHistory, ivLogout;
    private TextView tvTitle;
    private EditText edtSearch;
    private boolean isOnSearch = false;
    private ListPengajuanAdapter pengajuanAdapter;
    private Button btnRefresh;
    private boolean isLoading = false;
    private static View footerList;
    private boolean firstLoad = true;

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

        session = new SessionManager(this);

        if(!session.isLogin()){
            Toast.makeText(MainActivity.this, "Silahkan login terlebih dahulu", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        initUI();
    }

    private void initUI() {

        ivSearch = (ImageView) findViewById(R.id.iv_search);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        ivHistory = (ImageView) findViewById(R.id.iv_history);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        lvPengajuan = (ListView) findViewById(R.id.lv_pengajuan);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);

        //inital
        firstLoad = true;
        isLoading = false;
        isOnSearch = false;
        session = new SessionManager(MainActivity.this);
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
        getDataPengajuan();
    }

    private void initEvent() {

        if(firstLoad){
            firstLoad = false;

            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(edtSearch.getText().toString().length() == 0) {

                        startIndex = 0;
                        getDataPengajuan();
                    }
                }
            });
        }

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnSearch){
                    isOnSearch = false;
                    tvTitle.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.VISIBLE);
                    edtSearch.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }else{
                    isOnSearch = true;
                    tvTitle.setVisibility(View.VISIBLE);
                    edtSearch.setVisibility(View.GONE);
                }
            }
        });

        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, HistoryPengajuan.class);
                startActivity(intent);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startIndex = 0;
                getDataPengajuan();
                btnRefresh.setVisibility(View.GONE);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i == EditorInfo.IME_ACTION_SEARCH){

                    keyword = edtSearch.getText().toString();
                    startIndex = 0;
                    getDataPengajuan();

                    iv.hideSoftKey(MainActivity.this);
                    return true;
                }

                return false;
            }
        });

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin logout ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                                session = new SessionManager(MainActivity.this);
                                session.logoutUser(intent);
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
    }

    private void getDataPengajuan() {

        pbLoading.setVisibility(View.VISIBLE);
        masterList = new ArrayList<>();

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("keyword", edtSearch.getText().toString());
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(MainActivity.this, jBody, "POST", ServerUrl.getPengajuan, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
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
                            masterList.add(new CustomItem(jo.getString("id"), jo.getString("nama"), jo.getString("nomor"), jo.getString("keterangan"),  jo.getString("timestamp"), jo.getString("urgent"),jo.getString("flag"), jo.getString("sumber")));
                        }
                    }

                    setPengajuanAdapter(masterList);
                    pbLoading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setPengajuanAdapter(null);
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                    btnRefresh.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String result) {
                setPengajuanAdapter(null);
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setPengajuanAdapter(List<CustomItem> listItem){

        lvPengajuan.setAdapter(null);

        if(listItem != null && listItem.size() > 0){

            pengajuanAdapter = new ListPengajuanAdapter(MainActivity.this, listItem);
            lvPengajuan.setAdapter(pengajuanAdapter);

            lvPengajuan.setOnScrollListener(onScrollListener());

            lvPengajuan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                    String idHeader = (item.getItem1());
                    Intent intent = new Intent(MainActivity.this, DetailPengajuan.class);
                    intent.putExtra("id_header", idHeader);
                    intent.putExtra("nomor", item.getItem3());
                    startActivity(intent);
                }
            });
        }
    }

    private AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = lvPengajuan.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (lvPengajuan.getLastVisiblePosition() >= count - threshold && !isLoading) {

                        isLoading = true;
                        lvPengajuan.addFooterView(footerList);
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
            jBody.put("keyword", edtSearch.getText().toString());
            jBody.put("start", String.valueOf(startIndex));
            jBody.put("count", String.valueOf(count));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(MainActivity.this, jBody, "POST", ServerUrl.getPengajuan, "", "", 0, session.getUsername(), session.getPassword(), new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray items = response.getJSONArray("response");
                        for(int i  = 0; i < items.length(); i++){

                            JSONObject jo = items.getJSONObject(i);
                            moreList.add(new CustomItem(jo.getString("id"), jo.getString("nama"), jo.getString("nomor"), jo.getString("keterangan"),  jo.getString("timestamp"), jo.getString("urgent"),jo.getString("flag"), jo.getString("sumber")));
                        }

                        lvPengajuan.removeFooterView(footerList);
                        if(pengajuanAdapter!= null) pengajuanAdapter.addMoreData(moreList);
                    }
                    isLoading = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    isLoading = false;
                    lvPengajuan.removeFooterView(footerList);
                }
            }

            @Override
            public void onError(String result) {
                isLoading = false;
                lvPengajuan.removeFooterView(footerList);
            }
        });
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
