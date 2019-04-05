package com.willkernel.kotlinapp.test;

/**
 * Created by willkernel
 * on 2019/3/31.
 */
public class UserManager {
    UserStore userStore;
    ApiService apiService;

    public UserManager(ApiService apiService, UserStore store) {
        this.apiService = apiService;
        this.userStore = store;
    }

    public void register() {
        apiService.register();
        userStore.login();
    }
}
