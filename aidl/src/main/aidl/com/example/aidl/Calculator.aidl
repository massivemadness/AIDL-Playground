package com.example.aidl;

import com.example.aidl.callback.AsyncCallback;

interface Calculator {
    void sum(int first, int second, AsyncCallback callback);
}