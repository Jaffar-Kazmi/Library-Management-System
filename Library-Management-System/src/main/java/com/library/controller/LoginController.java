package com.library.controller;

import com.library.view.LoginPanel;
import com.library.model.User;
import com.library.service.AuthenticationService;

import javax.swing.*;

public class LoginController {
    private LoginPanel loginPanel;
    private AuthenticationService authenticationService;
    private LoginCallBack callBack;

    public LoginController(LoginPanel loginPanel,AuthenticationService authenticationService, LoginCallBack callBack){
        this.loginPanel = loginPanel;
        this.authenticationService = authenticationService;
        this.callBack = callBack;
    }

    public void handleLogin(String username, String password, String userType){
        if(!authenticationService.validateInput(username, password)){
            showError("Please enter both username and password");
            return;
        }

        User user = authenticationService.authenticate(username, password, userType);

        if (user != null) {
            if (callBack != null) {
                callBack.onLoginSuccess(user);
            }
        } else {
            if (callBack != null) {
                callBack.onLoginError("Invalid credentials or user type mismatch");
                loginPanel.clearPassword();
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(loginPanel, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    public interface LoginCallBack {
        void onLoginSuccess(User user);
        void onLoginError(String errorMessage);
    }
}

