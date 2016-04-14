package com.tappitz.app.rest.service;

/**
 * Created by sampaio on 08-10-2015.
 */
public interface CallbackMultiple<T,V> {
    public void success(T response);
    //public void success2(T response);
    public void failed(V error);

//    public interface Service<T,U> {
//        T executeService(U... args);
//    }
}
