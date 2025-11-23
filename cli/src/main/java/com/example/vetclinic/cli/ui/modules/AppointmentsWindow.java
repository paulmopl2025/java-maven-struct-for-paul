package com.example.vetclinic.cli.ui.modules;

import com.example.vetclinic.cli.model.Appointment;
import com.example.vetclinic.cli.model.CreateAppointmentRequest;
import com.example.vetclinic.cli.service.AppointmentService;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class AppointmentsWindow extends BasicWindow {

    private final AppointmentService appointmentService;
    private final WindowBasedTextGUI gui;
    private final Table<String> table;

    public AppointmentsWindow(WindowBasedTextGUI gui, AppointmentService appointmentService) {
        super("Appointments Management");
        this.gui = gui;
        this.appointmentService = appointmentService;

        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new BorderLayout());

        // Toolbar
        Panel toolbar = new Panel();
        toolbar.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        toolbar.addComponent(new Button("Refresh", this::refreshTable));
        toolbar.addComponent(new Button("Create New", this::createAppointment));
        toolbar.addComponent(new Button("Complete", () -> updateStatus("COMPLETED")));
        toolbar.addComponent(new Button("Cancel", () -> updateStatus("CANCELLED")));
        toolbar.addComponent(new Button("Close", this::close));

        rootPanel.addComponent(toolbar.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

        // Table
        table = new Table<>("ID", "Date/Time", "Pet", "Vet", "Service", "Status");

        table.setSelectAction(() -> {
            new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                    .setTitle("Options")
                    .setDescription("Select an action")
                    .addAction("Complete", () -> updateStatus("COMPLETED"))
                    .addAction("Cancel", () -> updateStatus("CANCELLED"))
                    .addAction("Back", () -> {
                    })
                    .build()
                    .showDialog(gui);
        });

        rootPanel.addComponent(table.withBorder(Borders.singleLine()), BorderLayout.Location.CENTER);

        setComponent(rootPanel);
        setHints(Arrays.asList(Hint.CENTERED, Hint.EXPANDED));

        refreshTable();
    }

    private void refreshTable() {
        table.getTableModel().clear();
        List<Appointment> appointments = appointmentService.getAllAppointments();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Appointment appt : appointments) {
            table.getTableModel().addRow(
                    String.valueOf(appt.getId()),
                    appt.getAppointmentDate().format(formatter),
                    appt.getPetName() != null ? appt.getPetName() : String.valueOf(appt.getPetId()),
                    appt.getVetName() != null ? appt.getVetName() : String.valueOf(appt.getVetId()),
                    appt.getServiceName() != null ? appt.getServiceName() : String.valueOf(appt.getServiceId()),
                    appt.getStatus());
        }
    }

    private void createAppointment() {
        String dateStr = new TextInputDialogBuilder()
                .setTitle("Date (YYYY-MM-DD)")
                .setInitialContent(java.time.LocalDate.now().toString())
                .build()
                .showDialog(gui);
        if (dateStr == null)
            return;

        String timeStr = new TextInputDialogBuilder()
                .setTitle("Time (HH:MM)")
                .setInitialContent(
                        java.time.LocalTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES).toString())
                .build()
                .showDialog(gui);
        if (timeStr == null)
            return;

        String notes = new TextInputDialogBuilder().setTitle("Notes").build().showDialog(gui);
        String petIdStr = new TextInputDialogBuilder().setTitle("Pet ID").build().showDialog(gui);
        String vetIdStr = new TextInputDialogBuilder().setTitle("Vet ID").build().showDialog(gui);
        String serviceIdStr = new TextInputDialogBuilder().setTitle("Service ID").build().showDialog(gui);

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateStr + "T" + timeStr);
            Long petId = Long.parseLong(petIdStr);
            Long vetId = Long.parseLong(vetIdStr);
            Long serviceId = Long.parseLong(serviceIdStr);

            CreateAppointmentRequest request = new CreateAppointmentRequest(dateTime, notes, petId, vetId, serviceId);
            Appointment created = appointmentService.createAppointment(request);

            if (created != null) {
                MessageDialog.showMessageDialog(gui, "Success", "Appointment created with ID: " + created.getId());
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error", "Failed to create appointment.");
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            MessageDialog.showMessageDialog(gui, "Error", "Invalid input format.");
        }
    }

    private void updateStatus(String status) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showMessageDialog(gui, "Warning", "Please select an appointment.");
            return;
        }

        String idStr = table.getTableModel().getCell(0, selectedRow);
        Long id = Long.parseLong(idStr);

        if (appointmentService.updateStatus(id, status)) {
            MessageDialog.showMessageDialog(gui, "Success", "Appointment status updated to " + status);
            refreshTable();
        } else {
            MessageDialog.showMessageDialog(gui, "Error", "Failed to update status.");
        }
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
