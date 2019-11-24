package com.xiaoyv.http.cookie;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import com.tencent.smtt.sdk.CookieManager;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class HttpPersistentCookieJar extends PersistentCookieJar {
    public HttpPersistentCookieJar(CookieCache cache, CookiePersistor persistor) {
        super(cache, persistor);
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        super.saveFromResponse(url, cookies);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        for (Cookie cookie : cookies) {
            cookieManager.setCookie(url.toString(), cookie.toString());
        }
        cookieManager.flush();
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return super.loadForRequest(url);
    }
}
