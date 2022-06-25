package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import org.apache.http.client.methods.HttpGet;

public class HttpUtil {

    public static HttpGet get(String url) {
        HttpGet get = new HttpGet(url);
        get.setConfig(CraftGR.getRequestConfig());
        return get;
    }
}
