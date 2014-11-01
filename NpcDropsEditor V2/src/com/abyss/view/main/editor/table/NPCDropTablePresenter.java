package com.abyss.view.main.editor.table;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

import javax.inject.Inject;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.dialog.Dialog.Actions;

import com.abyss.item.Item;
import com.abyss.npc.NPC;
import com.abyss.npc.NPCDrop;
import com.abyss.resource.ItemImages;
import com.abyss.resource.ItemNames;
import com.abyss.view.PresenterModel;

public class NPCDropTablePresenter implements Initializable {

	private final int DEFAULT_ITEM = 0;// dwarf remains

	@FXML
	TableView<NPCDrop> dropTableView;

	@FXML
	TableColumn<NPCDrop, Image> imageColumn;
	@FXML
	TableColumn<NPCDrop, Number> idColumn;
	@FXML
	TableColumn<NPCDrop, String> nameColumn;
	@FXML
	TableColumn<NPCDrop, Number> rateColumn;
	@FXML
	TableColumn<NPCDrop, Number> minColumn;
	@FXML
	TableColumn<NPCDrop, Number> maxColumn;

	@Inject
	PresenterModel model;

	private ObservableList<NPCDrop> modifiedDrops;

	private BooleanProperty requiresSaving;

	// Control disabling
	public BooleanProperty disableOnSelectionProperty;

	private NPC npc;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		modifiedDrops = FXCollections.observableArrayList();
		requiresSaving = new SimpleBooleanProperty();
		disableOnSelectionProperty = new SimpleBooleanProperty(true);
		dropTableView.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE);
		addContextMenuToTable();
		registerTableViewDragAndDrop();
		setupIdColumn();
		setupMaxColumn();
		setupMinColumn();
		setupNameColumn();
		setupRateColumn();
		setupImageColumn();
		dropTableView.getSelectionModel().selectedItemProperty()
				.addListener((e, oldVal, newVal) -> {
					// for disabling and enabling controls based on selection.
						disableOnSelectionProperty.set(newVal == null);
					});

	}

	private void addContextMenuToTable() {
		ContextMenu tableMenu = new ContextMenu();
		MenuItem deleteItem = new MenuItem("Delete");
		deleteItem.disableProperty().bind(disableOnSelectionProperty);
		deleteItem.setOnAction((event) -> {
			removeSelectedItems();

		});
		tableMenu.getItems().add(deleteItem);
		dropTableView.setContextMenu(tableMenu);

	}

	private void setupImageColumn() {
		imageColumn.setCellValueFactory(data -> data.getValue().getItem()
				.imageProperty());
		imageColumn
				.setCellFactory(new Callback<TableColumn<NPCDrop, Image>, TableCell<NPCDrop, Image>>() {
					@Override
					public TableCell<NPCDrop, Image> call(
							TableColumn<NPCDrop, Image> arg0) {
						TableCell<NPCDrop, Image> cell = new TableCell<NPCDrop, Image>() {
							@Override
							public void updateItem(Image item, boolean empty) {
								super.updateItem(item, empty);
								if (item != null || !empty) {
									HBox box = new HBox();
									StackPane pane = new StackPane();
									ImageView r = new ImageView();
									box.setAlignment(Pos.CENTER);
									r.setImage(item);
									pane.getChildren().addAll(r);
									box.getChildren().addAll(pane);
									box.setPadding(new Insets(2, 2, 2, 2));
									setGraphic(box);
								} else {
									setGraphic(null);
								}
							}
						};
						return cell;
					}

				});

	}

	private void setupIdColumn() {
		idColumn.setCellValueFactory(data -> data.getValue().getItem()
				.idProperty());
		idColumn.setCellFactory(TextFieldTableCell
				.forTableColumn(new NumberStringConverter()));
		idColumn.setOnEditCommit((event) -> {
			int newItemId = event.getNewValue().intValue();
			NPCDrop drop = event.getRowValue();

			if (newItemId == drop.getItem().getId()) {
				event.consume();
				return;
			}

			if (hasDuplicateItem(newItemId)) {
				TableColumn<?, ?> tableColumn = (TableColumn<?, ?>) event
						.getSource();
				Dialogs.create()
						.title("Duplicate Item")
						.masthead("Duplicate Item: " + newItemId)
						.message(
								"There is already an item with this ID found. Please use another ID.")
						.showInformation();

				// Temporary fix for cells values not returning to old value
				// after editing.
				tableColumn.setVisible(false);
				tableColumn.setVisible(true);
				event.consume();
				return;
			}

			drop.getItem().idProperty().set(newItemId);
			drop.getItem().nameProperty().set(ItemNames.getItemName(newItemId));
			drop.getItem()
					.imageProperty()
					.set(ItemImages.getItemImages().get(
							event.getNewValue().intValue()));
			setSavingStatus(true);
		});
	}

	private void setupNameColumn() {
		nameColumn.setCellValueFactory(data -> data.getValue().getItem()
				.nameProperty());

	}

	private void setupRateColumn() {
		rateColumn.setCellValueFactory(data -> data.getValue().rateProperty());
		rateColumn.setCellFactory(TextFieldTableCell
				.forTableColumn(new NumberStringConverter()));
		rateColumn.setOnEditCommit((event) -> {
			NPCDrop drop = event.getRowValue();
			drop.rateProperty().set(event.getNewValue().doubleValue());
			setSavingStatus(true);
		});

	}

	private void setupMinColumn() {
		minColumn.setCellValueFactory(data -> data.getValue()
				.minAmountProperty());
		minColumn.setCellFactory(TextFieldTableCell
				.forTableColumn(new NumberStringConverter()));
		minColumn.setOnEditCommit((event) -> {
			NPCDrop drop = event.getRowValue();
			drop.minAmountProperty().set(event.getNewValue().intValue());
			setSavingStatus(true);
		});

	}

	private void setupMaxColumn() {
		maxColumn.setCellValueFactory(data -> data.getValue()
				.maxAmountProperty());
		maxColumn.setCellFactory(TextFieldTableCell
				.forTableColumn(new NumberStringConverter()));
		maxColumn.setOnEditCommit((event) -> {
			NPCDrop drop = event.getRowValue();
			drop.maxAmountProperty().set(event.getNewValue().intValue());
			setSavingStatus(true);
		});

	}

	private void registerTableViewDragAndDrop() {
		dropTableView.setOnDragOver((event) -> {
			Object source = model.getItemListPresenter().draggedItemProperty
					.get();
			if (event.getDragboard().hasString() && source instanceof Item) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();

		});

		dropTableView.setOnDragDropped((event) -> {
			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasString()) {
				Item item = model.getItemListPresenter().draggedItemProperty
						.get();
				addNewDropItem(item);
				success = true;

			}
			model.getItemListPresenter().draggedItemProperty.set(null);
			event.setDropCompleted(success);
			event.consume();
		});

		dropTableView.setOnDragExited((e) -> {
			e.consume();

		});

	}

	public final void saveDropTable(boolean quick) {

		boolean userSave = false;

		if (!quick && tableNeedsSaving()) {
			Action action = Dialogs
					.create()
					.masthead("Saving Table for: [" + getNpc().toString() + "]")
					.message("Do you want to save the drop table for this NPC?")
					.showConfirm();
			if (action.equals(Actions.YES)) {
				userSave = true;
			}
		} else if (quick && tableNeedsSaving()) {
			userSave = true;
		}

		if (userSave) {
			NPC npc = getNpc();
			setSavingStatus(false);
			npc.getDrops().clear();
			npc.getDrops().addAll(getModifiedDrops());
			model.getMainWindowPresenter().setStatusMessage("Saved!");
		}
	}

	public void removeAllItems() {
		modifiedDrops.clear();
		dropTableView.requestFocus();
	}

	public void removeSelectedItems() {
		ObservableList<NPCDrop> selectedItems = dropTableView
				.getSelectionModel().getSelectedItems();

		int lastSelectedIndex = dropTableView.getSelectionModel()
				.getSelectedIndex();

		boolean confirmation = true;
		if (selectedItems.size() > 1) {
			Action action = Dialogs
					.create()
					.masthead("Delete Items")
					.message(
							"Are you sure you want to delete the selected items?")
					.showConfirm();
			if (action.equals(Actions.YES))
				confirmation = true;
			else
				confirmation = false;
		}

		if (confirmation) {
			modifiedDrops.removeAll(selectedItems);
			dropTableView.getSelectionModel().clearAndSelect(lastSelectedIndex);
		}

		/*
		 * Old code for single selection deletion int selectedDropIndex =
		 * dropTableView.getSelectionModel() .getSelectedIndex();
		 * modifiedDrops.remove(selectedDropIndex);
		 * dropTableView.getSelectionModel().select(selectedDropIndex);
		 * dropTableView.requestFocus(); /
		 */
	}

	public void addNewDropItem(Item... itemToAdd) {
		if (itemToAdd.length == 0) {
			modifiedDrops.add(new NPCDrop(new Item(DEFAULT_ITEM), 100, 1, 1,
					false));
		} else if (itemToAdd != null) {
			ObservableList<Item> duplicateItemsList = FXCollections
					.observableArrayList();

			for (Item item : itemToAdd) {
				if (hasDuplicateItem(item.getId())) {
					duplicateItemsList.add(item);
					continue;
				}

				Item referenceItem = new Item(item.getId());
				modifiedDrops.add(new NPCDrop(referenceItem, 100, 1, 1, false));

			}

			if (!duplicateItemsList.isEmpty()) {
				Dialogs.create()
						.title("Duplicate Item"
								+ (duplicateItemsList.size() > 1 ? "s" : ""))
						.message(
								duplicateItemsList.size() == 1 ? "You cannot add the same item to a drop table!"
										: "The following items could not be added because they already exist: "
												+ duplicateItemsList)
						.showError();
				duplicateItemsList.clear();
			}
		}

		int lastIndex = modifiedDrops.size() - 1;
		dropTableView.getSelectionModel().clearSelection();
		dropTableView.getSelectionModel().selectLast();
		dropTableView.scrollTo(lastIndex);
		dropTableView.requestFocus();

		/*
		 * OLD CODE. Supports only adding one item at a time
		 * 
		 * / if (refItem != null) {
		 * 
		 * if (hasDuplicateItem(refItem.getId())) { Dialogs.create()
		 * .title("Duplicate Item") .masthead(refItem.toString()) .message(
		 * "You cannot add the same item to a drop table!") .showError();
		 * return; }
		 * 
		 * Item item = new Item(refItem.getId()); modifiedDrops.add(new
		 * NPCDrop(item, 100, 1, 1, false)); } else { // default item - dwarf
		 * remains modifiedDrops.add(new NPCDrop(new Item(DEFAULT_ITEM), 100, 1,
		 * 1, false)); }
		 * 
		 * /
		 */

	}

	private boolean hasDuplicateItem(int id) {
		for (NPCDrop drop : modifiedDrops) {
			if (drop.getItem() != null) {
				if (drop.getItem().getId() == DEFAULT_ITEM
						&& id == DEFAULT_ITEM)
					continue;
				if (drop.getItem().getId() == id) {
					return true;
				}
			}
		}

		return false;
	}

	public void populateDropList(NPC npc) {
		this.npc = npc;
		if (!npc.getDrops().isEmpty()) {
			for (NPCDrop drop : npc.getDrops()) {
				modifiedDrops.add(new NPCDrop(new Item(drop.getItem().getId()),
						drop.getRate(), drop.getMinAmount(), drop
								.getMaxAmount(), drop.getRarity()));
			}
		}

		dropTableView.setItems(modifiedDrops);
		listenForItemChanges();

	}

	private void listenForItemChanges() {
		modifiedDrops.addListener((ListChangeListener<? super NPCDrop>) e -> {
			// if there are ANY modifications to the drop table, it will mark
			// this drop definition for saving.
				setSavingStatus(true);
			});

	}

	public void setSavingStatus(boolean status) {
		if (tableNeedsSaving() != status)
			requiresSaving.set(status);
	}

	public boolean tableNeedsSaving() {
		return requiresSaving.get();
	}

	public NPC getNpc() {
		return npc;
	}

	public ObservableList<NPCDrop> getModifiedDrops() {
		return modifiedDrops;
	}

}
