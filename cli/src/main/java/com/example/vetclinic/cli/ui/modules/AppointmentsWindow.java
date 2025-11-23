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
    private final com.example.vetclinic.cli.service.PetService petService;
    private final com.example.vetclinic.cli.service.UserService userService;
    private final com.example.vetclinic.cli.service.ServiceService serviceService;
    private final WindowBasedTextGUI gui;
    private final Table<String> table;

    public AppointmentsWindow(WindowBasedTextGUI gui, AppointmentService appointmentService,
            com.example.vetclinic.cli.service.PetService petService,
            com.example.vetclinic.cli.service.UserService userService,
            com.example.vetclinic.cli.service.ServiceService serviceService) {
        super("Appointments Management");
        this.gui = gui;
        this.appointmentService = appointmentService;
        this.petService = petService;
        this.userService = userService;
        this.serviceService = serviceService;

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
        // Select Pet
        List<com.example.vetclinic.cli.model.Pet> pets = petService.getAllPets();
        if (pets.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Error", "No pets found. Create a pet first.");
            return;
        }
        com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder petSelector = new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                .setTitle("Select Pet");
        final java.util.concurrent.atomic.AtomicReference<Long> selectedPetId = new java.util.concurrent.atomic.AtomicReference<>();
        for (com.example.vetclinic.cli.model.Pet pet : pets) {
            petSelector.addAction(pet.getName() + " (ID: " + pet.getId() + ")", () -> selectedPetId.set(pet.getId()));
        }
        petSelector.build().showDialog(gui);
        if (selectedPetId.get() == null)
            return;
        Long petId = selectedPetId.get();

        // Select Vet
        List<com.example.vetclinic.cli.model.UserDTO> vets = userService.getVets();
        if (vets.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Error", "No vets found.");
            return;
        }
        com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder vetSelector = new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                .setTitle("Select Vet");
        final java.util.concurrent.atomic.AtomicReference<Long> selectedVetId = new java.util.concurrent.atomic.AtomicReference<>();
        for (com.example.vetclinic.cli.model.UserDTO vet : vets) {
            vetSelector.addAction(vet.getDisplayName() + " (ID: " + vet.getId() + ")",
                    () -> selectedVetId.set(vet.getId()));
        }
        vetSelector.build().showDialog(gui);
        if (selectedVetId.get() == null)
            return;
        Long vetId = selectedVetId.get();

        // Select Service
        List<com.example.vetclinic.cli.model.ServiceDTO> services = serviceService.getAllServices();
        if (services.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Error", "No services found.");
            return;
        }
        com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder serviceSelector = new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                .setTitle("Select Service");
        final java.util.concurrent.atomic.AtomicReference<Long> selectedServiceId = new java.util.concurrent.atomic.AtomicReference<>();
        for (com.example.vetclinic.cli.model.ServiceDTO service : services) {
            serviceSelector.addAction(service.getName() + " (ID: " + service.getId() + ")",
                    () -> selectedServiceId.set(service.getId()));
        }
        serviceSelector.build().showDialog(gui);
        if (selectedServiceId.get() == null)
            return;
        Long serviceId = selectedServiceId.get();

        String dateStr = new TextInputDialogBuilder()
                .setTitle("Date (YYYY-MM-DD)")
                .setInitialContent(java.time.LocalDate.now().plusDays(1).toString())
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

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateStr + "T" + timeStr);

            // Validate that the date is in the future
            if (dateTime.isBefore(LocalDateTime.now())) {
                MessageDialog.showMessageDialog(gui, "Error",
                        "Appointment date must be in the future.\nPlease select a date and time after now.");
                return;
            }

            CreateAppointmentRequest request = new CreateAppointmentRequest(dateTime, notes, petId, vetId, serviceId);
            Appointment created = appointmentService.createAppointment(request);

            if (created != null) {
                MessageDialog.showMessageDialog(gui, "Success", "Appointment created with ID: " + created.getId());
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error",
                        "Failed to create appointment.\nCheck console for details.");
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            MessageDialog.showMessageDialog(gui, "Error", "Invalid input format.\nDate: YYYY-MM-DD\nTime: HH:MM");
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

        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton result = com.googlecode.lanterna.gui2.dialogs.MessageDialog
                .showMessageDialog(gui, "Confirm", "Update status to " + status + "?",
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes,
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.No);

        if (result == com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes) {
            boolean success = false;
            if ("CONFIRMED".equals(status)) {
                success = appointmentService.confirmAppointment(id);
            } else if ("CANCELLED".equals(status)) {
                success = appointmentService.cancelAppointment(id);
            }

            if (success) {
                MessageDialog.showMessageDialog(gui, "Success", "Appointment status updated to " + status);
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error", "Failed to update status.");
            }
        }
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
