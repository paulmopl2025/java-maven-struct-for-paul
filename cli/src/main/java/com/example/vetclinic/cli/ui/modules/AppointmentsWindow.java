package com.example.vetclinic.cli.ui.modules;

import com.example.vetclinic.cli.model.Appointment;
import com.example.vetclinic.cli.model.CreateAppointmentRequest;
import com.example.vetclinic.cli.model.UpdateAppointmentRequest;
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
    private final com.example.vetclinic.cli.service.VetService vetService;
    private final com.example.vetclinic.cli.service.ServiceService serviceService;
    private final WindowBasedTextGUI gui;
    private final Table<String> table;
    private List<Appointment> appointments;

    public AppointmentsWindow(WindowBasedTextGUI gui, AppointmentService appointmentService,
            com.example.vetclinic.cli.service.PetService petService,
            com.example.vetclinic.cli.service.VetService vetService,
            com.example.vetclinic.cli.service.ServiceService serviceService) {
        super("Appointments Management");
        this.gui = gui;
        this.appointmentService = appointmentService;
        this.petService = petService;
        this.vetService = vetService;
        this.serviceService = serviceService;

        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new BorderLayout());

        // Toolbar
        Panel toolbar = new Panel();
        toolbar.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        toolbar.addComponent(new Button("Refresh", this::refreshTable));
        toolbar.addComponent(new Button("Create New", this::createAppointment));
        toolbar.addComponent(new Button("Edit", this::editAppointment));
        toolbar.addComponent(new Button("Confirm", () -> updateStatus("CONFIRMED")));
        toolbar.addComponent(new Button("Cancel", () -> updateStatus("CANCELLED")));
        toolbar.addComponent(new Button("Close", this::close));

        rootPanel.addComponent(toolbar.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

        // Table
        table = new Table<>("ID", "Date/Time", "Pet", "Vet", "Service", "Status");

        table.setSelectAction(() -> {
            new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                    .setTitle("Options")
                    .setDescription("Select an action")
                    .addAction("Edit", this::editAppointment)
                    .addAction("Confirm", () -> updateStatus("CONFIRMED"))
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
        this.appointments = appointmentService.getAllAppointments();
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
        List<com.example.vetclinic.cli.model.VetDTO> vets = vetService.getAllVets();
        if (vets.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Error", "No vets found.");
            return;
        }
        com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder vetSelector = new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                .setTitle("Select Vet");
        final java.util.concurrent.atomic.AtomicReference<Long> selectedVetId = new java.util.concurrent.atomic.AtomicReference<>();
        for (com.example.vetclinic.cli.model.VetDTO vet : vets) {
            vetSelector.addAction("Dr. " + vet.getFullName(),
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

        // Get service duration for availability checking
        com.example.vetclinic.cli.model.ServiceDTO selectedService = services.stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElse(null);

        int serviceDuration = (selectedService != null && selectedService.getEstimatedDurationMinutes() != null)
                ? selectedService.getEstimatedDurationMinutes()
                : 30; // Default 30 minutes

        // Use calendar dialog for date selection
        java.time.LocalDate selectedDate = com.example.vetclinic.cli.ui.components.CalendarDialog.showCalendar(
                gui, "Select Appointment Date");

        if (selectedDate == null) {
            return;
        }

        // Get existing appointments for the selected vet and date
        List<Appointment> existingAppointments = appointmentService.getAppointmentsByVetAndDate(vetId, selectedDate);

        // Use time slot selector to show only available times
        LocalDateTime selectedDateTime = com.example.vetclinic.cli.ui.components.TimeSlotSelector.selectTimeSlot(
                gui, selectedDate, existingAppointments, serviceDuration);

        if (selectedDateTime == null) {
            return;
        }

        String notes = new TextInputDialogBuilder().setTitle("Notes (Optional)").build().showDialog(gui);

        try {
            CreateAppointmentRequest request = new CreateAppointmentRequest(selectedDateTime, notes, petId, vetId,
                    serviceId);
            Appointment created = appointmentService.createAppointment(request);

            if (created != null) {
                MessageDialog.showMessageDialog(gui, "Success",
                        "Appointment created successfully!\n\nDate: " + selectedDateTime.toLocalDate() +
                                "\nTime: " + selectedDateTime.toLocalTime() +
                                "\nID: " + created.getId());
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error",
                        "Failed to create appointment.\nCheck console for details.");
            }
        } catch (Exception e) {
            MessageDialog.showMessageDialog(gui, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showMessageDialog(gui, "Warning", "Please select an appointment to edit.");
            return;
        }

        if (appointments == null || selectedRow >= appointments.size()) {
            return;
        }

        Appointment currentAppointment = appointments.get(selectedRow);

        // Select Vet (Default to current)
        List<com.example.vetclinic.cli.model.VetDTO> vets = vetService.getAllVets();
        if (vets.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Error", "No vets found.");
            return;
        }

        com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder vetSelector = new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                .setTitle("Select Vet (Current: " + currentAppointment.getVetName() + ")");

        final java.util.concurrent.atomic.AtomicReference<Long> selectedVetId = new java.util.concurrent.atomic.AtomicReference<>(
                currentAppointment.getVetId());

        for (com.example.vetclinic.cli.model.VetDTO vet : vets) {
            vetSelector.addAction("Dr. " + vet.getFullName(), () -> selectedVetId.set(vet.getId()));
        }
        // Add option to keep current if not in list (though it should be)
        vetSelector.addAction("Keep Current", () -> selectedVetId.set(currentAppointment.getVetId()));

        vetSelector.build().showDialog(gui);
        Long vetId = selectedVetId.get();

        // Select Service
        List<com.example.vetclinic.cli.model.ServiceDTO> services = serviceService.getAllServices();
        if (services.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Error", "No services found.");
            return;
        }

        com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder serviceSelector = new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                .setTitle("Select Service (Current: " + currentAppointment.getServiceName() + ")");

        final java.util.concurrent.atomic.AtomicReference<Long> selectedServiceId = new java.util.concurrent.atomic.AtomicReference<>(
                currentAppointment.getServiceId());

        for (com.example.vetclinic.cli.model.ServiceDTO service : services) {
            serviceSelector.addAction(service.getName(), () -> selectedServiceId.set(service.getId()));
        }
        serviceSelector.addAction("Keep Current", () -> selectedServiceId.set(currentAppointment.getServiceId()));

        serviceSelector.build().showDialog(gui);
        Long serviceId = selectedServiceId.get();

        // Get service duration
        com.example.vetclinic.cli.model.ServiceDTO selectedService = services.stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElse(null);

        int serviceDuration = (selectedService != null && selectedService.getEstimatedDurationMinutes() != null)
                ? selectedService.getEstimatedDurationMinutes()
                : 30;

        // Date Selection
        java.time.LocalDate selectedDate = com.example.vetclinic.cli.ui.components.CalendarDialog.showCalendar(
                gui, "Select Date (Current: " + currentAppointment.getAppointmentDate().toLocalDate() + ")");

        if (selectedDate == null) {
            return;
        }

        // Time Selection
        List<Appointment> existingAppointments = appointmentService.getAppointmentsByVetAndDate(vetId, selectedDate);

        // Exclude current appointment from existing appointments to allow keeping same
        // slot
        existingAppointments.removeIf(a -> a.getId().equals(currentAppointment.getId()));

        LocalDateTime selectedDateTime = com.example.vetclinic.cli.ui.components.TimeSlotSelector.selectTimeSlot(
                gui, selectedDate, existingAppointments, serviceDuration);

        if (selectedDateTime == null) {
            return;
        }

        // Notes
        String currentNotes = currentAppointment.getNotes() != null ? currentAppointment.getNotes() : "";
        String notes = new TextInputDialogBuilder()
                .setTitle("Notes")
                .setInitialContent(currentNotes)
                .build()
                .showDialog(gui);

        try {
            UpdateAppointmentRequest request = new UpdateAppointmentRequest();
            request.setAppointmentDate(selectedDateTime);
            request.setNotes(notes);
            request.setVetId(vetId);
            request.setServiceId(serviceId);
            // Pet ID is not updatable in backend DTO, so we ignore it or set it to same
            // (doesn't matter)
            request.setPetId(currentAppointment.getPetId());

            boolean success = appointmentService.updateAppointment(currentAppointment.getId(), request);

            if (success) {
                MessageDialog.showMessageDialog(gui, "Success", "Appointment updated successfully!");
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error", "Failed to update appointment.");
            }
        } catch (Exception e) {
            MessageDialog.showMessageDialog(gui, "Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
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
