package gmedia.net.id.finance.DetailPengajuan.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.finance.DetailPengajuan.DetailPengajuan;
import gmedia.net.id.finance.DetailPengajuan.ListBarangPengajuan;
import gmedia.net.id.finance.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListBarangAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListBarangAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_barang, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem0, tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
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
            convertView = inflater.inflate(R.layout.adapter_barang, null);

            holder.tvItem0 = (TextView) convertView.findViewById(R.id.tv_item0);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item5);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);

        holder.tvItem0.setText(itemSelected.getItem2());
        holder.tvItem1.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem3()) + " " + itemSelected.getItem4());
        holder.tvItem2.setText(itemSelected.getItem9() +  " " + iv.ChangeToCurrencyFormat(itemSelected.getItem5()));
        holder.tvItem3.setText(itemSelected.getItem9() +  " " + iv.ChangeToCurrencyFormat(itemSelected.getItem6()));
        holder.tvItem4.setText(itemSelected.getItem7());
        holder.tvItem5.setText(itemSelected.getItem8());

        return convertView;
    }
}
