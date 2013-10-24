package util;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/24/13
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class NothingToSeeHere {

    public static String hashOne = "98gergetr345sgtcgfa9234adgad14tdgvd0sqnv7n3ksbamxlien249egrbn2";
    public static String hashTwo = "bnt34567ajkn245bvhagdfgh9bnjg3b234gbn23023jaevmak190578jslvopr";

    public final static byte[] clamps = {48, 57, 65, 90, 97, 122};
    public final static byte[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static String t(String s){
        byte[] bytes = new byte[s.length()*3];
        int i = -1;
        for(byte b : a(s)){
            bytes[++i] = b;
        }
        for(byte b : b(s)){
            bytes[++i] = b;
        }
        for(byte b : c(s)){
            bytes[++i] = b;
        }
        return deTranslate(bytes);
    }

    public static String f(String s){
        return null;
    }

    private static byte[] a(String s){
        byte[] bytes = translate(s);

        for(int i = 0; i < bytes.length; ++i){
            int index = bytes[i];
            byte c1, c2;

            c1 = translate((byte)hashOne.charAt(index));
            c2 = translate((byte)hashTwo.charAt(index));
            bytes[i] += c1 - c2;

            bytes[i] = clamp(bytes[i]);
        }

        return bytes;
    }

    private static byte[] b(String s){
        byte[] bytes = s.getBytes();
        byte[] abytes = a(s);

        for(int i = 0; i < bytes.length; ++i){
            int index = translate(bytes[i]);
            byte c1, c2;

            c1 = translate((byte)hashOne.charAt(index));
            c2 = translate((byte)hashTwo.charAt(index));
            bytes[i] += c1 - c2;

            int k = clampIndex(bytes[i], 0);
            if(k < 0)
                bytes[i] = clamp((byte)(abytes[i] + (0x1 | index << 1)));
            else
                bytes[i] = clamp((byte)(abytes[i] + (0x0 | index << 1)));
        }
        return bytes;
    }

    private static byte[] c(String s){
        byte[] bytes = translate(s);

        for(int i = 0; i < bytes.length; ++i){
            int index = bytes[i];
            int c1, c2;

            c1 = translate((byte)hashOne.charAt(index));
            c2 = translate((byte)hashTwo.charAt(index));

            bytes[i] += c1 - c2 + ((c2 << (c1/c2 + 1) | c1) > c2 ? c2 ^ 0xFF : c1 ^ 0xFF);
            bytes[i] += c2 - c1 + ((c1 << (c2/c1 + 1) | c2) > c1 ? c2 ^ 0xFF : c1 ^ 0xFF);
            bytes[i] = clamp(bytes[i]);

        }
        return bytes;
    }

    private static int clampIndex(byte b, int index){
        if(b > chars.length)
            return clampIndex((byte)(b - chars.length), index-1);
        if(b < 0)
            return clampIndex((byte)(chars.length + b), index+1);
        return index;

    }

    private static byte clamp(byte b){
        if(b >= chars.length)
            return clamp((byte)(b - chars.length));
        if(b < 0)
            return clamp((byte)(chars.length + b));
        return b;
    }


    private static int gi3(byte b){
        if(b < clamps[1] && b > clamps[0])
            return (byte)(b - '0');

        else if(b < clamps[3] && b > clamps[2])
            return (byte)(b - 'A' + 10);

        else if(b < clamps[5] && b > clamps[4])
            return (byte)(b - 'a' + 36);

        else if((b > clamps[3] && b < clamps[4]) || (b > clamps[1] && b < clamps[2]))
            throw new RuntimeException("Invalid password, only (a-z) / (A-Z) / (0-9) are allowed in this horrible obfuscator");
        else
            throw new RuntimeException("Wtf are you doing?");

    }

    public static String deTranslate(byte[] bytes){
        byte[] newBytes = new byte[bytes.length];


        for(int i = 0; i < bytes.length; ++i){
            newBytes[i] = chars[bytes[i]];
        }
        return new String(newBytes);
    }

    public static byte[] translate(String s){

        byte[] indices = new byte[s.length()];
        for(int i = 0; i < indices.length; ++i) {
            indices[i] = translate((byte) s.charAt(i));
        }
        return indices;
    }

    private static byte translate(byte b){
        for(int i = 0; i < chars.length; i++){
            if(chars[i] == b)
                return (byte)i;
        }
        return -1;
    }

    public static void main(String[] args){
        String test = "1238jyuyuge893u45hgyyjk90";
        String en = t(test);

        p(test);
        p(en);
    }

    public static void p(String s){
        System.out.println(s);
    }
}
