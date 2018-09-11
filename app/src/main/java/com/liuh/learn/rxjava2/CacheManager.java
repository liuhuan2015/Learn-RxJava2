package com.liuh.learn.rxjava2;

public class CacheManager {

    private static CacheManager mCacheManager;

    private GirlsDataRequest mGirlsDataRequest;

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        if (mCacheManager == null) {
            mCacheManager = new CacheManager();
        }
        return mCacheManager;
    }

    public GirlsDataRequest getGirlsDataRequest() {
        return mGirlsDataRequest;
    }

    public void setGirlsDataRequest(GirlsDataRequest mGirlsDataRequest) {
        this.mGirlsDataRequest = mGirlsDataRequest;
    }
}
