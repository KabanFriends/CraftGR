package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.config.ModConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

public class HttpUtil {

    public static RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(ModConfig.get("connectTimeout"))
                .setConnectionRequestTimeout(ModConfig.get("connectTimeout"))
                .setSocketTimeout(ModConfig.get("socketTimeout"))
                .build();
    }

    public static HttpGet get(String url) {
        HttpGet get = new HttpGet(url);
        get.setConfig(createRequestConfig());
        return get;
    }
}
