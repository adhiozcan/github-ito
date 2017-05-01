package id.net.iconpln.apps.ito.socket;

import android.app.Application;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Ozcan on 13/04/2017.
 */

public class ItoHttpClient {
    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static final int DEFAULT_CONNECT_TIMEOUT           = 6;
    private static final int DEFAULT_WRITE_TIMEOUT             = 18;
    private static final int DEFAULT_READ_TIMEOUT              = 10;

    private static Application application;
    private static File        fileCache;

    private static volatile OkHttpClient itoHttpClient;

    public static void init(Application application) {
        ItoHttpClient.application = application;
    }

    public static OkHttpClient getItoHttpClient() {
        // its called double check locking pattern
        if (itoHttpClient == null) {
            synchronized (ItoHttpClient.class) {
                if (itoHttpClient == null) {
                    itoHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                            .cache(itoCache())
                            .addInterceptor(provideLoggingAbility())
                            .addNetworkInterceptor(new StethoInterceptor())
                            .build();
                }
            }
        }
        return itoHttpClient;
    }

    private static HttpLoggingInterceptor provideLoggingAbility() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    private static Cache itoCache() {
        final @javax.annotation.Nullable File baseDir = application.getCacheDir();
        if (baseDir != null)
            fileCache = new File(baseDir, "HttpResponseCache");
        return new Cache(fileCache, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
    }

    private ItoHttpClient() {
        if (itoHttpClient != null) {
            throw new RuntimeException("Please Use getClient() to use this single object");
        }
    }
}
