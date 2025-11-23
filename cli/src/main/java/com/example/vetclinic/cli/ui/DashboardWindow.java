package com.example.vetclinic.cli.ui;

import com.example.vetclinic.cli.service.AuthService;
import com.example.vetclinic.cli.ui.modules.AppointmentsWindow;
import com.example.vetclinic.cli.ui.modules.ClinicStatsWindow;
import com.example.vetclinic.cli.ui.modules.OwnersWindow;
import com.example.vetclinic.cli.ui.modules.PetsWindow;
import com.example.vetclinic.cli.ui.modules.ServicesWindow;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import java.util.Arrays;

public class DashboardWindow extends BasicWindow {

    private final WindowBasedTextGUI gui;
    private final AuthService authService;
    private final Panel mainContentPanel;

    public DashboardWindow(WindowBasedTextGUI gui, AuthService authService) {
        super("Vet Clinic Dashboard");
        this.gui = gui;
        this.authService = authService;

        // Main Layout: Border Layout
        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new BorderLayout());

        // Header
        Panel headerPanel = new Panel();
        headerPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        headerPanel.addComponent(new Label("User: " + authService.getSession().getUsername()));
        headerPanel.addComponent(new EmptySpace(new TerminalSize(2, 0)));
        headerPanel.addComponent(new Label("Role: " + authService.getSession().getRole()));
        rootPanel.addComponent(headerPanel.withBorder(Borders.singleLine("Status")), BorderLayout.Location.TOP);

        // Sidebar (Menu)
        Panel sidebarPanel = new Panel();
        sidebarPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        sidebarPanel.addComponent(new Button("Home", this::showHome));
        sidebarPanel.addComponent(new Button("Owners", this::showOwners));
        sidebarPanel.addComponent(new Button("Pets", this::showPets));
        sidebarPanel.addComponent(new Button("Services", this::showServices));
        sidebarPanel.addComponent(new Button("Appointments", this::showAppointments));
        sidebarPanel.addComponent(new Button("Medical History", this::showMedicalHistory));

        if ("ADMIN".equals(authService.getSession().getRole())) {
            sidebarPanel.addComponent(new Button("Users", this::showUsers));
            sidebarPanel.addComponent(new Button("Clinic Config", this::showClinicConfig));
        }

        sidebarPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        sidebarPanel.addComponent(new Button("Logout", this::logout));
        sidebarPanel.addComponent(new Button("Exit", () -> System.exit(0)));

        rootPanel.addComponent(sidebarPanel.withBorder(Borders.singleLine("Menu")), BorderLayout.Location.LEFT);

        // Main Content Area
        mainContentPanel = new Panel();
        mainContentPanel.setLayoutManager(new GridLayout(1));
        showHome(); // Default view

        rootPanel.addComponent(mainContentPanel.withBorder(Borders.singleLine("Content")),
                BorderLayout.Location.CENTER);

        setComponent(rootPanel);
        setHints(Arrays.asList(Hint.FULL_SCREEN));
    }

    private void clearContent() {
        mainContentPanel.removeAllComponents();
    }

    private void showHome() {
        clearContent();
        mainContentPanel.addComponent(new Label("Welcome to Vet Clinic Management System"));
        mainContentPanel.addComponent(new EmptySpace());
        mainContentPanel.addComponent(new Label("Select a module from the menu to begin."));
    }

    private void showOwners() {
        new OwnersWindow(gui, new com.example.vetclinic.cli.service.OwnerService(authService)).show();
    }

    private void showPets() {
        new PetsWindow(gui,
                new com.example.vetclinic.cli.service.PetService(authService),
                new com.example.vetclinic.cli.service.OwnerService(authService)).show();
    }

    private void showAppointments() {
        new AppointmentsWindow(gui,
                new com.example.vetclinic.cli.service.AppointmentService(authService),
                new com.example.vetclinic.cli.service.PetService(authService),
                new com.example.vetclinic.cli.service.VetService(authService),
                new com.example.vetclinic.cli.service.ServiceService(authService)).show();
    }

    private void showServices() {
        new ServicesWindow(gui, new com.example.vetclinic.cli.service.ServiceService(authService)).show();
    }

    private void showMedicalHistory() {
        new com.example.vetclinic.cli.ui.modules.MedicalHistoryWindow(gui,
                new com.example.vetclinic.cli.service.MedicalRecordService(authService),
                new com.example.vetclinic.cli.service.PetService(authService),
                new com.example.vetclinic.cli.service.UserService(authService)).show();
    }

    private void showUsers() {
        clearContent();
        mainContentPanel.addComponent(new Label("Users Module - Coming Soon"));
    }

    private void showClinicConfig() {
        new ClinicStatsWindow(gui, new com.example.vetclinic.cli.service.ClinicService(authService)).show();
    }

    private void logout() {
        authService.logout();
        close();
        new LoginWindow(gui, authService).show();
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
