package com.example.vetclinic.cli;

import com.example.vetclinic.cli.service.AuthService;
import com.example.vetclinic.cli.storage.StorageService;
import com.example.vetclinic.cli.ui.DashboardWindow;
import com.example.vetclinic.cli.ui.LoginWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class VetClinicCLI {

    public static void main(String[] args) {
        try {
            // Setup Terminal and Screen
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();

            // Create GUI
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);

            // Initialize Services
            StorageService storageService = new StorageService();
            AuthService authService = new AuthService(storageService);

            // Check session or show login
            if (authService.isAuthenticated()) {
                new DashboardWindow(gui, authService).show();
            } else {
                new LoginWindow(gui, authService).show();
            }

            screen.stopScreen();
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
