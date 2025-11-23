package com.example.vetclinic.cli.ui.modules;

import com.example.vetclinic.cli.model.CreateOwnerRequest;
import com.example.vetclinic.cli.model.Owner;
import com.example.vetclinic.cli.service.OwnerService;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;

import java.util.Arrays;
import java.util.List;

public class OwnersWindow extends BasicWindow {

    private final OwnerService ownerService;
    private final WindowBasedTextGUI gui;
    private final Table<String> table;

    public OwnersWindow(WindowBasedTextGUI gui, OwnerService ownerService) {
        super("Owners Management");
        this.gui = gui;
        this.ownerService = ownerService;

        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new BorderLayout());

        // Toolbar
        Panel toolbar = new Panel();
        toolbar.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        toolbar.addComponent(new Button("Refresh", this::refreshTable));
        toolbar.addComponent(new Button("Create New", this::createOwner));
        toolbar.addComponent(new Button("Delete Selected", this::deleteOwner));
        toolbar.addComponent(new Button("Close", this::close));

        rootPanel.addComponent(toolbar.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

        // Table
        table = new Table<>("ID", "First Name", "Last Name", "Email", "Phone", "City");
        table.setSelectAction(() -> {
            String[] options = new String[] { "Edit", "Delete", "Cancel" };
            new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                    .setTitle("Options")
                    .setDescription("Select an action")
                    .addAction("Edit", () -> MessageDialog.showMessageDialog(gui, "Info", "Edit not implemented yet"))
                    .addAction("Delete", this::deleteOwner)
                    .addAction("Cancel", () -> {
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
        List<Owner> owners = ownerService.getAllOwners();
        for (Owner owner : owners) {
            table.getTableModel().addRow(
                    String.valueOf(owner.getId()),
                    owner.getFirstName(),
                    owner.getLastName(),
                    owner.getEmail(),
                    owner.getPhone(),
                    owner.getCity());
        }
    }

    private void createOwner() {
        String firstName = new TextInputDialogBuilder().setTitle("First Name").build().showDialog(gui);
        if (firstName == null)
            return;

        String lastName = new TextInputDialogBuilder().setTitle("Last Name").build().showDialog(gui);
        if (lastName == null)
            return;

        String email = new TextInputDialogBuilder().setTitle("Email").build().showDialog(gui);
        String phone = new TextInputDialogBuilder().setTitle("Phone").build().showDialog(gui);
        String address = new TextInputDialogBuilder().setTitle("Address").build().showDialog(gui);
        String city = new TextInputDialogBuilder().setTitle("City").build().showDialog(gui);

        CreateOwnerRequest request = new CreateOwnerRequest(firstName, lastName, email, phone, address, city);
        Owner created = ownerService.createOwner(request);

        if (created != null) {
            MessageDialog.showMessageDialog(gui, "Success", "Owner created with ID: " + created.getId());
            refreshTable();
        } else {
            MessageDialog.showMessageDialog(gui, "Error", "Failed to create owner.");
        }
    }

    private void deleteOwner() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showMessageDialog(gui, "Warning", "Please select an owner to delete.");
            return;
        }

        String idStr = table.getTableModel().getCell(0, selectedRow);
        Long id = Long.parseLong(idStr);

        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton result = com.googlecode.lanterna.gui2.dialogs.MessageDialog
                .showMessageDialog(gui, "Confirm", "Are you sure you want to delete Owner ID: " + id + "?",
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes,
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.No);

        if (result == com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes) {
            if (ownerService.deleteOwner(id)) {
                MessageDialog.showMessageDialog(gui, "Success", "Owner deleted.");
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error", "Failed to delete owner.");
            }
        }
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
