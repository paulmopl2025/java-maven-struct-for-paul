package com.example.vetclinic.cli.ui.modules;

import com.example.vetclinic.cli.model.CreatePetRequest;
import com.example.vetclinic.cli.model.Pet;
import com.example.vetclinic.cli.service.PetService;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class PetsWindow extends BasicWindow {

    private final PetService petService;
    private final WindowBasedTextGUI gui;
    private final Table<String> table;

    public PetsWindow(WindowBasedTextGUI gui, PetService petService) {
        super("Pets Management");
        this.gui = gui;
        this.petService = petService;

        Panel rootPanel = new Panel();
        rootPanel.setLayoutManager(new BorderLayout());

        // Toolbar
        Panel toolbar = new Panel();
        toolbar.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        toolbar.addComponent(new Button("Refresh", this::refreshTable));
        toolbar.addComponent(new Button("Create New", this::createPet));
        toolbar.addComponent(new Button("Delete Selected", this::deletePet));
        toolbar.addComponent(new Button("Close", this::close));

        rootPanel.addComponent(toolbar.withBorder(Borders.singleLine()), BorderLayout.Location.TOP);

        // Table
        table = new Table<>("ID", "Name", "Species", "Breed", "Owner ID");

        table.setSelectAction(() -> {
            String[] options = new String[] { "Edit", "Delete", "Cancel" };
            new com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder()
                    .setTitle("Options")
                    .setDescription("Select an action")
                    .addAction("Edit", () -> MessageDialog.showMessageDialog(gui, "Info", "Edit not implemented yet"))
                    .addAction("Delete", this::deletePet)
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
        List<Pet> pets = petService.getAllPets();
        for (Pet pet : pets) {
            table.getTableModel().addRow(
                    String.valueOf(pet.getId()),
                    pet.getName(),
                    pet.getSpecies(),
                    pet.getBreed(),
                    String.valueOf(pet.getOwnerId()));
        }
    }

    private void createPet() {
        String name = new TextInputDialogBuilder().setTitle("Name").build().showDialog(gui);
        if (name == null)
            return;

        String species = new TextInputDialogBuilder().setTitle("Species").build().showDialog(gui);
        String breed = new TextInputDialogBuilder().setTitle("Breed").build().showDialog(gui);
        String birthDateStr = new TextInputDialogBuilder().setTitle("Birth Date (YYYY-MM-DD)").build().showDialog(gui);
        String ownerIdStr = new TextInputDialogBuilder().setTitle("Owner ID").build().showDialog(gui);

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            Long ownerId = Long.parseLong(ownerIdStr);

            CreatePetRequest request = new CreatePetRequest(name, species, breed, birthDate, ownerId);
            Pet created = petService.createPet(request);

            if (created != null) {
                MessageDialog.showMessageDialog(gui, "Success", "Pet created with ID: " + created.getId());
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error", "Failed to create pet.");
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            MessageDialog.showMessageDialog(gui, "Error", "Invalid input format.");
        }
    }

    private void deletePet() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showMessageDialog(gui, "Warning", "Please select a pet to delete.");
            return;
        }

        String idStr = table.getTableModel().getCell(0, selectedRow);
        Long id = Long.parseLong(idStr);

        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton result = com.googlecode.lanterna.gui2.dialogs.MessageDialog
                .showMessageDialog(gui, "Confirm", "Are you sure you want to delete Pet ID: " + id + "?",
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes,
                        com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.No);

        if (result == com.googlecode.lanterna.gui2.dialogs.MessageDialogButton.Yes) {
            if (petService.deletePet(id)) {
                MessageDialog.showMessageDialog(gui, "Success", "Pet deleted.");
                refreshTable();
            } else {
                MessageDialog.showMessageDialog(gui, "Error", "Failed to delete pet.");
            }
        }
    }

    public void show() {
        gui.addWindowAndWait(this);
    }
}
