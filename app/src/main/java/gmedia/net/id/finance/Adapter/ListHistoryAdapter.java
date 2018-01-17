package gmedia.net.id.finance.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class ListHistoryAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListHistoryAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_history, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private RelativeLayout rlContainer, rlStatus;
        private TextView tvItem0, tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvStatus;
        private RatingBar rbStatus;
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
            convertView = inflater.inflate(R.layout.adapter_history, null);

            holder.rlContainer = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            holder.rlStatus = (RelativeLayout) convertView.findViewById(R.id.rl_status);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tvItem0 = (TextView) convertView.findViewById(R.id.tv_item0);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item5);
            holder.rbStatus = (RatingBar) convertView.findViewById(R.id.rb_status);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);

        if(itemSelected.getItem6().equals("1")){ // Approve

            holder.tvStatus.setText("Approve");
            holder.rlStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_left_blue));
        }else if(itemSelected.getItem6().equals("2")){ // Reject

            holder.tvStatus.setText("Reject");
            holder.rlStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_left_orange));
        }

        holder.tvItem0.setText(iv.ChangeFormatDateString(itemSelected.getItem4(), FormatItem.formatTimestamp, FormatItem.formatDateDisplay2));
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText(itemSelected.getItem3());
        if(itemSelected.getItem7().equals("")){

            holder.tvItem3.setVisibility(View.GONE);
        }else{

            holder.tvItem3.setVisibility(View.VISIBLE);
            holder.tvItem3.setText("Alasan penolakan: " + itemSelected.getItem7());
        }
        holder.tvItem4.setText(itemSelected.getItem9());
        holder.tvItem5.setText(itemSelected.getItem8());
        int rating = iv.parseNullInteger(itemSelected.getItem5());
        holder.rbStatus.setNumStars(rating);
        holder.rbStatus.setRating(iv.parseNullFloat(itemSelected.getItem5()));

        return convertView;
    }
}
