package com.orlando.starter;

public class Util {
  public static String coverHump(String source) {
    StringBuffer sb = new StringBuffer();
    for (char c : source.toCharArray()) {
      if (c >= 'A' && c <= 'Z') {
        if (sb.length() != 0) {
          sb.append('_');
        }
      }
      sb.append(c);
    }
    return sb.toString().toLowerCase();
  }
}
