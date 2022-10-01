package com.github.haroldjcastillo.rsm.store;

public interface Action<T> {
    
    T apply(T value);
}
