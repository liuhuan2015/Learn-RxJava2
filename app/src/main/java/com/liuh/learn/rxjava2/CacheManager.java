package com.liuh.learn.rxjava2;

import com.liuh.learn.rxjava2.model.CategoryDataRequest;

/**
 * 缓存管理类
 */
public class CacheManager {

    private static CacheManager mCacheManager;

    private CategoryDataRequest mCategoryDataRequest;

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        if (mCacheManager == null) {
            mCacheManager = new CacheManager();
        }
        return mCacheManager;
    }

    public CategoryDataRequest getGirlsDataRequest() {
        return mCategoryDataRequest;
    }

    public void setGirlsDataRequest(CategoryDataRequest mCategoryDataRequest) {
        this.mCategoryDataRequest = mCategoryDataRequest;
    }
}
