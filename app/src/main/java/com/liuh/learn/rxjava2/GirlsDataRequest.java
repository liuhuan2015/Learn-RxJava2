package com.liuh.learn.rxjava2;

import java.util.List;

public class GirlsDataRequest {

    private boolean error;

    private List<GirlBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<GirlBean> getResults() {
        return results;
    }

    public void setResults(List<GirlBean> results) {
        this.results = results;
    }
}
