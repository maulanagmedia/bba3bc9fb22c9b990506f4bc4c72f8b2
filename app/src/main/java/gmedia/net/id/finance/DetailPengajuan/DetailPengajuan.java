package gmedia.net.id.finance.DetailPengajuan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.SessionManager;

import java.util.List;

import gmedia.net.id.finance.HistoryPengajuan;
import gmedia.net.id.finance.R;

public class DetailPengajuan extends AppCompatActivity {

    private ListView lvPengajuan;
    private ProgressBar pbLoading;
    private Button btnRefresh;
    private ItemValidation iv = new ItemValidation();
    private static SessionManager session;
    private static List<CustomItem> listPengajuan;
    private static Context context;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail_pengajuan, menu);

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
