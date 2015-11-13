package cn.tootoo.soa.oms.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Created by Liu Yong
 * Desp:
 * Date: 2010-1-24
 * Time: 19:33:19
 */
public class MathUtils {
	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // 四舍五入
    public static synchronized String format(double number, int len) {
        String pos = "";
        for (int i = 0; i < len; i++) pos += "0";
        if (0 != pos.length()) pos = "#0." + pos;
        else pos = "#0";

        DecimalFormat df = new DecimalFormat(pos);

        return df.format(number);
    }

    public static  String format(BigDecimal number, int len) {
        String pos = "";
        for (int i = 0; i < len; i++) pos += "0";
        if (0 != pos.length()) pos = "#0." + pos;
        else pos = "#0";

        DecimalFormat df = new DecimalFormat(pos);

        return df.format(number.doubleValue());
    }

    public static  String format(Float number, int len) {
        String pos = "";
        for (int i = 0; i < len; i++) pos += "0";
        if (0 != pos.length()) pos = "#0." + pos;
        else pos = "#0";

        DecimalFormat df = new DecimalFormat(pos);

        return df.format(number.floatValue());
    }

    // 如3.300，返回"3.3"
    public static String realValue(Object number) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(number);
    }

    public static String round(BigDecimal number, int scale) {
        number = number.setScale(scale, BigDecimal.ROUND_DOWN);
        return number.toString();
    }

    public static void main(String[] args) {
        BigDecimal b = new BigDecimal(0.125);
        System.out.println(format(b.doubleValue(), 2));
//        char c = '"';
////        System.out.println(round(new BigDecimal(2.555555555555555555555), 0));
//        BigDecimal a = new BigDecimal(5);
//        BigDecimal b = new BigDecimal(3);
//
//        System.out.println(a.divide(b, 0, BigDecimal.ROUND_UP));
//        System.out.println(generateString(10));
    }

    public static String generateString(int length) {

	     StringBuffer sb = new StringBuffer();
	     Random random = new Random();
	     for (int i = 0; i < length; i++) {
	       sb.append(allChar.charAt(random.nextInt(allChar.length())));
	     }
	     return sb.toString();
	}

    String s = "<tr id=\"tr_g_$IDX\" bgcolor=\"#FFFFFF\" align=\"center\">\n" +
            "        <td id=\"sn_g_$IDX\">sn_g_$IDX_val</td>\n" +
            "        <td id=\"sku_g_$IDX\">sku_g_$IDX_val</td>\n" +
            "        <td id=\"tit_g_$IDX\">tit_g_$IDX_val</td>\n" +
            "        <td id=\"uni_g_$IDX\">uni_g_$IDX_val</td>\n" +
            "        <td id=\"pri_g_$IDX\">pri_g_$IDX_val</td>\n" +
            "        <td id=\"can_g_$IDX\">can_g_$IDX_val(mar_g_$IDX_val)</td>\n" +
            "        <td><label><input id=\"num_g_$IDX\" size=\"2\" maxlength=\"3\"></label></td>\n" +
            "        <td>\n" +
            "            <button id=\"add_g_$IDX\" onclick=\"addG($IDX)\" type=\"button\">&nbsp;+&nbsp;</button>\n" +
            "        </td>\n" +
            "        <div id=\"id_g_$IDX\" style=\"display: none\">id_g_$IDX_val</div>\n" +
            "    </tr>";
}
