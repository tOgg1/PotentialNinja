package util;

public class ServerInfo {

    public static String decryptIp(String hash){
        String raw_0 = NothingToSeeHere.f(hash, ipfactory);

        if(raw_0.length() != 12)
            return null;

        return raw_0.substring(0,3) + "." + raw_0.substring(3,6) + "." + raw_0.substring(6,9) + "." + raw_0.substring(9,12);
    }

    public static int port = 2674;
    public static String ipenc = "32qhRbIP9DMVD8M";
    public static int ipfactory = 30;
    public static int max_connections = 100;

    public static int code_begin = 0x00;
    public static int code_end = 0x01;
    public static int code_recipient = 0x02;
    public static int code_subject = 0x03;
    public static int code_message = 0x04;

    public static int code_ok = 0xF0;
    public static int code_error = 0xF1;
    public static int code_invalid = 0xF2;
}
