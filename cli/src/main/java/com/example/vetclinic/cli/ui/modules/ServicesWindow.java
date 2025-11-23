package com.example.vetclinic.cli.ui.modules;

import com.example.vetclinic.cli.model.ServiceDTO;
import com.example.vetclinic.cli.model.ServiceType;
import com.example.vetclinic.cli.service.ServiceService;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.table.Table;

import java.util.Arrays;
import java.util.List;

public class ServicesWindow extends BasicWindow {

    private final ServiceService serviceService;
    private final WindowBasedTextGUI gui;
    private final Table<String> table;

    public ServicesWindow(WindowBasedTextGUI gui, ServiceService serviceService) {
        super("Veterinary Services");
        this.gui = gui;
        this.serviceService = serviceService;

        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new BorderLayout());

        // Toolbar
        Panel toolbar = new Panel();
        toolbar.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        toolbar.addComponent(new Button("Refresh", this::refreshTable));
        toolbar.addComponent(new Button("View Details", this::viewDetails));
        toolbar.addComponent(new Button("Close", this::close));

        rootPanel.addComponent(toolbar.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

        // Table
        table = new Table<>("ID", "Name", "Type", "Cost", "Duration (min)", "Status");

        table.setSelectAction(this::viewDetails);

        rootPanel.addComponent(table.withBorder(Borders.singleLine()), BorderLayout.Location.CENTER);

        setComponent(rootPanel);
        setHints(Arrays.asList(Hint.CENTERED, Hint.EXPANDED));

        refreshTable();
    }

    private void refreshTable() {
        table.getTableModel().clear();
        List<ServiceDTO> services = serviceService.getAllServices();

        for (ServiceDTO service : services) {
            table.getTableModel().addRow(
                    String.valueOf(service.getId()),
                    service.getName(),
                    service.getServiceType() != null ? formatServiceType(service.getServiceType()) : "N/A",
                    service.getBaseCost() != null ? "$" + service.getBaseCost() : "N/A",
                    service.getEstimatedDurationMinutes() != null
                            ? String.valueOf(service.getEstimatedDurationMinutes())
                            : "N/A",
                    service.getActive() != null && service.getActive() ? "Active" : "Inactive");
        }
    }

    private String formatServiceType(ServiceType type) {
        switch (type) {
            case VACCINATION:
                return "Vaccination";
            case CHECKUP:
                return "Checkup";
            case EMERGENCY:
                return "Emergency";
            case SURGERY:
                return "Surgery";
            case GROOMING:
                return "Grooming";
            case DENTAL:
                return "Dental";
            case LABORATORY:
                return "Laboratory";
            case IMAGING:
                return "Imaging";
            default:
                return type.toString();
        }
    }

    private void viewDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            MessageDialog.showMessageDialog(gui, "Info", "Please select a service to view details.");
            return;
        }

        String idStr = table.getTableModel().getCell(0, selectedRow);
        Long id = Long.parseLong(idStr);

        // Find the service in the list
        List<ServiceDTO> services = serviceService.getAllServices();
        ServiceDTO service = services.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (service == null) {
            MessageDialog.showMessageDialog(gui, "Error", "Service not found.");
            return;
        }

        // Build details message
        StringBuilder details = new StringBuilder();
        details.append("Service Details\n\n");
        details.append("ID: ").append(service.getId()).append("\n");
        details.append("Name: ").append(service.getName()).append("\n");
        details.append("Type: ")
                .append(service.getServiceType() != null ? formatServiceType(service.getServiceType()) : "N/A")
                .append("\n");
        details.append("Description: ").append(service.getDescription() != null ? service.getDescription() : "N/A")
                .append("\n");
        details.append("Base Cost: ").append(service.getBaseCost() != null ? "$" + service.getBaseCost() : "N/A")
                .append("\n");
        details.append("Duration: ")
                .append(service.getEstimatedDurationMinutes() != null
                        ? service.getEstimatedDurationMinutes() + " minutes"
                        : "N/A")
                .append("\n");
        details.append("Status: ").append(service.getActive() != null && service.getActive() ? "Active" : "Inactive")
                .append("\n");

        MessageDialog.showMessageDialog(gui, "Service Details", details.toString());
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
