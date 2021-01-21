package com.example.aidl.callback;

import com.example.aidl.base.AidlException;
import com.example.aidl.base.AidlResult;

interface AsyncCallback {
    void onSuccess(in AidlResult aidlResult);
    void onError(in AidlException aidlException);
}