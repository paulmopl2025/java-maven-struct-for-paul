package com.example.vetclinic.cli.ui.modules;

import com.example.vetclinic.cli.model.ClinicStatsDTO;
import com.example.vetclinic.cli.service.ClinicService;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

import java.util.Arrays;
import java.util.Map;

public class ClinicStatsWindow extends BasicWindow {

    private final ClinicService clinicService;
    private final WindowBasedTextGUI gui;
    private final Panel contentPanel;

    public ClinicStatsWindow(WindowBasedTextGUI gui, ClinicService clinicService) {
        super("Clinic Statistics");
        this.gui = gui;
        this.clinicService = clinicService;

        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new BorderLayout());

        // Toolbar
        Panel toolbar = new Panel();
        toolbar.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        toolbar.addComponent(new Button("Refresh", this::loadStats));
        toolbar.addComponent(new Button("Close", this::close));

        rootPanel.addComponent(toolbar.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

        // Content
        contentPanel = new Panel();
        contentPanel.setLayoutManager(new GridLayout(2));

        rootPanel.addComponent(contentPanel.withBorder(Borders.singleLine()), BorderLayout.Location.CENTER);

        setComponent(rootPanel);
        setHints(Arrays.asList(Hint.CENTERED));

        loadStats();
    }

    private void loadStats() {
        contentPanel.removeAllComponents();

        ClinicStatsDTO stats = clinicService.getStats();
        if (stats == null) {
            contentPanel.addComponent(new Label("Failed to load statistics."));
            return;
        }

        addStat("Total Vets:", String.valueOf(stats.getTotalVets()));
        addStat("Total Patients:", String.valueOf(stats.getTotalPatients()));
        addStat("Total Appointments:", String.valueOf(stats.getTotalAppointments()));
        addStat("Active Services:", String.valueOf(stats.getActiveServices()));

        contentPanel.addComponent(new EmptySpace());
        contentPanel.addComponent(new EmptySpace());

        contentPanel.addComponent(new Label("Appointments by Status:").withBorder(Borders.singleLine()));
        contentPanel.addComponent(new EmptySpace());

        if (stats.getAppointmentsByStatus() != null) {
            for (Map.Entry<String, Long> entry : stats.getAppointmentsByStatus().entrySet()) {
                addStat(entry.getKey() + ":", String.valueOf(entry.getValue()));
            }
        }
    }

    private void addStat(String label, String value) {
        contentPanel.addComponent(new Label(label));
        contentPanel.addComponent(new Label(value));
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
