package gmedia.net.id.finance.Utils;

/**
 * Created by Shinmaul on 12/15/2017.
 */

public class ServerUrl {

    private static final String baseURL = "http://erpsmg.gmedia.id/pengajuan/";
    //private static final String baseURL = "http://192.168.20.128/pengajuan/";
    //private static final String baseURL = "http://192.168.20.68/pengajuan/";
    //private static final String baseURL = "http://192.168.20.65/gmedia_finance/api/";

    public static final String login = baseURL + "mobileauth/login/";

    public static final String getPengajuan = baseURL + "mobilepengajuan/get_pengajuan_dev/";
    public static final String getDetailPengajuan = baseURL + "mobilepengajuan/get_detail_pengajuan_dev/";
    public static final String updatePengajuan = baseURL + "mobilepengajuan/update_pengajuan/";
    public static final String updateDetailPengajuan = baseURL + "mobilepengajuan/update_detail_pengajuan/";
    public static final String getHistory = baseURL + "mobilepengajuan/get_history/";
    public static final String getTahunHeader = baseURL + "mobilepengajuan/get_tahun_header/";
    public static final String getBarangPO = baseURL + "mobilepengajuan/get_barang_po/";
}