package gmedia.net.id.finance.DetailPengajuan.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import gmedia.net.id.finance.DetailPengajuan.DetailPengajuan;
import gmedia.net.id.finance.DetailPengajuan.ListBarangPengajuan;
import gmedia.net.id.finance.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Shin on 1/8/2017.
 */

public class DetailPengajuanAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();
    private ProgressDialog progressDialog;

    public DetailPengajuanAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_detail_pengajuan, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvTanggal, tvPengaju, tvRekeningTujuan, tvNominal, tvKeterangan, tvTujuanPembayaran;
        private Button btnApprove, btnReject, btnLihatDetail, btnLihatBukti;
        private LinearLayout llPo;
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

            holder.llPo = (LinearLayout) convertView.findViewById(R.id.ll_po);
            holder.tvTanggal = (TextView) convertView.findViewById(R.id.tv_tanggal);
            holder.tvPengaju = (TextView) convertView.findViewById(R.id.tv_pengaju);
            holder.tvRekeningTujuan = (TextView) convertView.findViewById(R.id.tv_rekening_tujuan);
            holder.tvNominal = (TextView) convertView.findViewById(R.id.tv_nominal);
            holder.tvKeterangan = (TextView) convertView.findViewById(R.id.tv_keterangan);
            holder.tvTujuanPembayaran = (TextView) convertView.findViewById(R.id.tv_tujuan_pembayaran);

            holder.btnLihatDetail = (Button) convertView.findViewById(R.id.btn_lihat_detail);
            holder.btnLihatBukti = (Button) convertView.findViewById(R.id.btn_lihat_bukti);
            holder.btnApprove = (Button) convertView.findViewById(R.id.btn_approve);
            holder.btnReject = (Button) convertView.findViewById(R.id.btn_reject);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);

        holder.tvPengaju.setText(itemSelected.getItem2());
        holder.tvRekeningTujuan.setText(itemSelected.getItem3());
        holder.tvNominal.setText(iv.ChangeToRupiahFormat(iv.parseNullDouble(itemSelected.getItem4())));
        holder.tvKeterangan.setText(itemSelected.getItem5());
        holder.tvTujuanPembayaran.setText(itemSelected.getItem6());
        holder.tvTanggal.setText(itemSelected.getItem7());

        if(itemSelected.getItem8().equals("0")){

            holder.btnLihatDetail.setVisibility(View.GONE);
        }else{
            holder.btnLihatDetail.setVisibility(View.VISIBLE);

            holder.btnLihatDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ListBarangPengajuan.class);
                    intent.putExtra("id", itemSelected.getItem8());
                    ((Activity) context).startActivity(intent);
                }
            });
        }

        if(itemSelected.getItem9().isEmpty()){

            holder.btnLihatBukti.setVisibility(View.GONE);
        }else{
            holder.btnLihatBukti.setVisibility(View.VISIBLE);
            holder.btnLihatBukti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new DownloadFileFromURL().execute(itemSelected.getItem10());
                }
            });
        }

        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher))
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin menyetujui pengajuan "+ itemSelected.getItem2() + " sebesar "+iv.ChangeToRupiahFormat(iv.parseNullDouble(itemSelected.getItem4())) + " ?" )
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DetailPengajuan.updateDetailPengajuan(itemSelected.getItem1(), true, "");
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

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.layout_reject_reason, null);
                builder.setView(viewDialog);

                final EditText edtKeterangan= (EditText) viewDialog.findViewById(R.id.edt_keterangan);
                final ImageView ivSend = (ImageView) viewDialog.findViewById(R.id.iv_send);

                final AlertDialog alert = builder.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                ivSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher))
                                .setTitle("Konfirmasi")
                                .setMessage("Anda yakin menolak pengajuan "+ itemSelected.getItem2() + " sebesar "+iv.ChangeToRupiahFormat(iv.parseNullDouble(itemSelected.getItem4())) + " ?" )
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(alert != null) alert.dismiss();
                                        DetailPengajuan.updateDetailPengajuan(itemSelected.getItem1(), false, edtKeterangan.getText().toString());
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

                alert.show();
            }
        });

        return convertView;
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "downloadedfile.jpg");
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            if(f != null){
                String imagePath = String.valueOf(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f));
                // setting downloaded into image view
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imagePath), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        }
        private void showDialog(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        private void dismissDialog(){
            progressDialog.dismiss();
        }
    }
}
