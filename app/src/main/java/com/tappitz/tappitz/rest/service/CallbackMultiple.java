package com.tappitz.tappitz.rest.service;

/**
 * Created by sampaio on 08-10-2015.
 */
public interface CallbackMultiple<T> {
    public void success(T response);
    //public void success2(T response);
    public void failed(Object error);

//    public interface Service<T,U> {
//        T executeService(U... args);
//    }
}
