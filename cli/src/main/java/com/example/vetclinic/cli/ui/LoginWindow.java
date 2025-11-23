package com.example.vetclinic.cli.ui;

import com.example.vetclinic.cli.service.AuthService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.util.Arrays;

public class LoginWindow extends BasicWindow {

    private final AuthService authService;
    private final WindowBasedTextGUI gui;

    public LoginWindow(WindowBasedTextGUI gui, AuthService authService) {
        super("Vet Clinic Login");
        this.gui = gui;
        this.authService = authService;

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new GridLayout(2));

        contentPanel.addComponent(new Label("Username:"));
        TextBox usernameBox = new TextBox();
        contentPanel.addComponent(usernameBox);

        contentPanel.addComponent(new Label("Password:"));
        TextBox passwordBox = new TextBox().setMask('*');
        contentPanel.addComponent(passwordBox);

        contentPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        Button loginButton = new Button("Login", () -> {
            String username = usernameBox.getText();
            String password = passwordBox.getText();

            if (username.isEmpty() || password.isEmpty()) {
                MessageDialog.showMessageDialog(gui, "Error", "Please enter username and password.");
                return;
            }

            String error = authService.login(username, password);
            if (error == null) {
                MessageDialog.showMessageDialog(gui, "Success", "Login successful!");
                close();
                new DashboardWindow(gui, authService).show();
            } else {
                MessageDialog.showMessageDialog(gui, "Login Failed", error);
            }
        });

        contentPanel.addComponent(loginButton);

        Button exitButton = new Button("Exit", () -> System.exit(0));
        contentPanel.addComponent(exitButton);

        setComponent(contentPanel);
        setHints(Arrays.asList(Hint.CENTERED));
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
