package gmedia.net.id.finance.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

public class ListPengajuanAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListPengajuanAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_pengajuan, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private RelativeLayout rlContainer;
        private TextView tvItem0, tvItem1, tvItem2, tvItem3, tvTanggal;
        private RatingBar rbStatus;
        private TextView tvLabel;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    /*@Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        int hasil = 0;
        final CustomItem item = items.get(position);
        if(!item.getItem9().equals("1") || !item.getItem10().equals("1") || !item.getItem11().equals("1")){
            hasil = 0;
        }else{
            hasil = 1;
        }
        return hasil;
    }*/

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
            convertView = inflater.inflate(R.layout.adapter_list_pengajuan, null);

            holder.rlContainer = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            holder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
            holder.tvItem0 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvTanggal = (TextView) convertView.findViewById(R.id.tv_tanggal);
            holder.rbStatus = (RatingBar) convertView.findViewById(R.id.rb_status);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);

        holder.tvItem0.setText((itemSelected.getItem2().length() > 0) ? itemSelected.getItem2().substring(0,1): "");
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(itemSelected.getItem4());
        holder.tvTanggal.setText(iv.ChangeFormatDateString(itemSelected.getItem5(), FormatItem.formatDate, FormatItem.formatDateDisplay1));
        int rating = iv.parseNullInteger(itemSelected.getItem6());
        holder.rbStatus.setNumStars(rating);
        holder.rbStatus.setRating(iv.parseNullFloat(itemSelected.getItem6()));

        return convertView;
    }
}
