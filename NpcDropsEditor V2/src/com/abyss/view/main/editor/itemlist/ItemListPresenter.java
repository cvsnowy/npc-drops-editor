package com.abyss.view.main.editor.itemlist;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import javax.inject.Inject;

import com.abyss.item.Item;
import com.abyss.resource.ItemNames;
import com.abyss.view.PresenterModel;

public class ItemListPresenter implements Initializable {

	@FXML
	TextField searchTextField;

	@FXML
	ListView<Item> itemListView;

	@Inject
	PresenterModel model;

	@FXML
	Button addAllSelectedItemsButton;

	public ObjectProperty<Item> draggedItemProperty;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model.setItemListPresenter(this);
		itemListView.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE);
		draggedItemProperty = new SimpleObjectProperty<Item>();
		setupItemListCellFactory();
		setupFiltering();
		registerListDragAndDrop();

	}

	@FXML
	public void addAllSelectedItemsButtonPressed() {
		ObservableList<Item> selectedItems = itemListView.getSelectionModel()
				.getSelectedItems();
		if (model.getDropEditorPresenter().getViewingTabPresenter() != null) {
			model.getDropEditorPresenter()
					.getViewingTabPresenter()
					.addNewDropItem(
							selectedItems.toArray(new Item[selectedItems.size()]));
		}

	}

	@FXML
	public void itemListPressed(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				if (!itemListView.getSelectionModel().isEmpty()) {
					Item selectedItem = itemListView.getSelectionModel()
							.getSelectedItem();
					if (model.getDropEditorPresenter().getViewingTabPresenter() != null) {
						model.getDropEditorPresenter().getViewingTabPresenter()
								.addNewDropItem(selectedItem);
					}
				}

			}
		}

	}


	private void setupItemListCellFactory() {
		for (int itemId : ItemNames.getNames().keySet()) {
			itemListView.getItems().add(new Item(itemId));
		}
		itemListView
				.setCellFactory(new Callback<ListView<Item>, ListCell<Item>>() {
					@Override
					public ListCell<Item> call(ListView<Item> arg0) {
						ListCell<Item> cell = new ListCell<Item>() {
							@Override
							public void updateItem(Item item, boolean empty) {
								super.updateItem(item, empty);
								if (item != null || !empty) {
									HBox box = new HBox();
									StackPane pane = new StackPane();
									ImageView r = new ImageView();

									Image im = item.getImage();
									r.setImage(im);
									pane.getChildren().addAll(r);
									Text t = new Text(item.toString());
									Tooltip tooltip = new Tooltip(String
											.valueOf(item.getId())
											+ " - "
											+ item.getName());
									Tooltip.install(box, tooltip);
									box.getChildren().addAll(pane, t);
									box.setSpacing(10);
									box.setPadding(new Insets(5, 5, 5, 5));
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

	private void setupFiltering() {
		// ItemList
		FilteredList<Item> filteredItems = new FilteredList<>(
				itemListView.getItems(), i -> true);
		searchTextField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					filteredItems.setPredicate(item -> {
						if (newValue == null || newValue.isEmpty()) {
							return true;
						}

						if (item.getName().toLowerCase()
								.contains(newValue.toLowerCase())) {
							return true;
						}

						return false;
					});
				});
		SortedList<Item> sortedData = new SortedList<>(filteredItems);
		itemListView.setItems(sortedData);

	}

	private void registerListDragAndDrop() {
		itemListView.setOnDragDetected((t) -> {
			Item item = itemListView.getSelectionModel().getSelectedItem();
			if (item != null) {
				Dragboard db = itemListView.startDragAndDrop(TransferMode.ANY);
				ClipboardContent content = new ClipboardContent();
				content.putString(item.toString());
				db.setDragView(item.getImage());
				db.setContent(content);
				draggedItemProperty.set(item);

			}
			t.consume();
		});

		itemListView.setOnDragDone((e) -> {
			if (e.getTransferMode() == null) // if transfer failed
					draggedItemProperty.set(null);// reset the dragged item

				e.consume();
			});
	}

}
