package com.depthpulse.dto;

import java.util.List;

/**
 * Simple POJO representing the response of the (simulated) GetBox tickers endpoint.
 */
public class TickersResponse {

    private List<String> stocks;
    private List<String> indexes;
    private List<String> futures;

    public List<String> getStocks() {
        return stocks;
    }

    public void setStocks(List<String> stocks) {
        this.stocks = stocks;
    }

    public List<String> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<String> indexes) {
        this.indexes = indexes;
    }

    public List<String> getFutures() {
        return futures;
    }

    public void setFutures(List<String> futures) {
        this.futures = futures;
    }

    @Override
    public String toString() {
        return "TickersResponse{" +
                "stocks=" + stocks +
                ", indexes=" + indexes +
                ", futures=" + futures +
                '}';
    }
}

