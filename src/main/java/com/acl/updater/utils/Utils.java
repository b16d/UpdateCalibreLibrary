package com.acl.updater.utils;

import org.jsoup.Jsoup;

public class Utils {

    public static  String html2text(String html) {
        return Jsoup.parse(html).text();
    }

}
