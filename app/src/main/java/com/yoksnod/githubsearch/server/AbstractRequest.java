package com.yoksnod.githubsearch.server;

public abstract class AbstractRequest<Params, Result extends RequestResult> {

    private final Params mParams;

    public AbstractRequest(Params params) {
        mParams = params;
    }

    public abstract Result execute();

    public Params getParams() {
        return mParams;
    }
}
