package gmedia.net.id.finance.DetailPengajuan.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.finance.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class DetailPengajuanAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public DetailPengajuanAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_detail_pengajuan, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvTanggal, tvPengaju, tvRekeningTujuan, tvNominal, tvKeterangan, tvTujuanPembayaran;
        private Button btnApprove, btnReject;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        //int tipeViewList = getItemViewType(position);

        if(convertView == null){

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_detail_pengajuan, null);

            holder.tvTanggal = (TextView) convertView.findViewById(R.id.tv_tanggal);
            holder.tvPengaju = (TextView) convertView.findViewById(R.id.tv_pengaju);
            holder.tvRekeningTujuan = (TextView) convertView.findViewById(R.id.tv_rekening_tujuan);
            holder.tvNominal = (TextView) convertView.findViewById(R.id.tv_nominal);
            holder.tvKeterangan = (TextView) convertView.findViewById(R.id.tv_keterangan);
            holder.tvTujuanPembayaran = (TextView) convertView.findViewById(R.id.tv_tujuan_pembayaran);

            holder.btnApprove = (Button) convertView.findViewById(R.id.btn_approve);
            holder.btnReject = (Button) convertView.findViewById(R.id.btn_reject);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);

        return convertView;
    }
}
