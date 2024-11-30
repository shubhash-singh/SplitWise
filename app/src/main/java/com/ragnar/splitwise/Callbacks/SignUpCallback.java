package com.ragnar.splitwise.Callbacks;

import java.util.Map;

public interface SignUpCallback {
    void onSuccess(Map<String, String> userData);
    void onFailure(String message);

}
