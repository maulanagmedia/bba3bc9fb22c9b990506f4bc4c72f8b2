package gmedia.net.id.finance.Utils;

/**
 * Created by Shinmaul on 12/15/2017.
 */

public class ServerUrl {

    private static final String baseURL = "http://appsmg.gmedia.net.id:2180/pengajuan/";
    //private static final String baseURL = "http://192.168.12.147/gmedia_finance/api/";

    public static final String login = baseURL + "mobileauth/login/";

    public static final String getPengajuan = baseURL + "mobilepengajuan/get_pengajuan/";
    public static final String getDetailPengajuan = baseURL + "mobilepengajuan/get_detail_pengajuan/";
    public static final String updatePengajuan = baseURL + "mobilepengajuan/update_pengajuan/";
    public static final String updateDetailPengajuan = baseURL + "mobilepengajuan/update_detail_pengajuan/";
    public static final String getHistory = baseURL + "mobilepengajuan/get_history/";
    public static final String getTahunHeader = baseURL + "mobilepengajuan/get_tahun_header/";
}
