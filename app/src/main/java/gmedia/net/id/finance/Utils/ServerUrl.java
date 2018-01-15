package gmedia.net.id.finance.Utils;

/**
 * Created by Shinmaul on 12/15/2017.
 */

public class ServerUrl {

    private static final String baseURL = "http://appsmg.gmedia.net.id:2180/pengajuan/";
    //private static final String baseURL = "http://192.168.12.147/gmedia_finance/";

    public static final String login = baseURL + "mobileauth/login/";

    public static final String getPengajuan = baseURL + "mobilepengajuan/get_pengajuan/";
    public static final String getHistory = baseURL + "mobilepengajuan/get_history/";
}
