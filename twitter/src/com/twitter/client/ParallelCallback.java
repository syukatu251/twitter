package com.twitter.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ParallelCallback<T> implements AsyncCallback<T> {

    /** The data that is returned from the service call. */
    private T data;

    /** A reference to the parent callback, which runs when all are complete. */
    private ParentCallback parentCallback;

    /**
     * Standard handleSuccess method, which is called when the service call completes.
     */
    @Override
    public void onSuccess(T t) {
        this.data = t;
        parentCallback.done();
    }

    /**
     * Method that can be used by the parent callback to get the data from this service call and
     * process it.
     */
    public T getData() {
        return data;
    }

    /**
     * Called by the parent callback, to inject a reference to itself into the child.
     */
    protected void setParent(ParentCallback parentCallback) {
        this.parentCallback = parentCallback;
    }

	/**
	 * Handle the error.
	 */
    @Override
    public void onFailure(Throwable arg0) {
        // TODO Auto-generated method stub
    }

}