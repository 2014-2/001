
package com.crtb.tunnelmonitor.utils;

/**
 * @author Tony Gao
 *
 */
public class CrtbTextUtils {

    public static String formatNumber(double n) {
        String s = null;
        if (n == (int) n) {
            s = String.format("%d", (int) n);
        } else {
            s = String.format("%1$.4f", n);
            while (s != null && s.contains(".") && s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
}
