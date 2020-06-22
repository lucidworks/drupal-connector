package com.lucidworks.fusion.connector.service;

import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DrupalUserService {

    private DrupalOkHttp drupalOkHttp;

    public DrupalUserService(DrupalOkHttp drupalOkHttp) {
        this.drupalOkHttp = drupalOkHttp;
    }

    /**
     * Request to login the user in order to have the JWT token
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
    public DrupalLoginResponse login(String url, String username, String password) {
        log.info("Trying to login the user {}", username);

        DrupalLoginRequest drupalLoginRequest = new DrupalLoginRequest(username, password);

        return drupalOkHttp.loginResponse(url, drupalLoginRequest);
    }

    /**
     * Logout function
     *
     * @param url
     * @param drupalLoginResponse
     * @return true if logout request is successful, else false
     */
    public boolean logout(String url, DrupalLoginResponse drupalLoginResponse) {
        log.info("Trying to logout the user {}");

        return drupalOkHttp.logout(url, drupalLoginResponse);
    }

}
