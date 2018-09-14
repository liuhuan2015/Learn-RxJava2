package com.liuh.learn.rxjava2.model;

import java.util.List;

public class CategoryDataRequest {

    private boolean error;

    private List<ItemBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ItemBean> getResults() {
        return results;
    }

    public void setResults(List<ItemBean> results) {
        this.results = results;
    }


    @Override
    public String toString() {
        return "CategoryDataRequest{" +
                "error=" + error +
                ", results=" + results +
                '}';
    }
}
