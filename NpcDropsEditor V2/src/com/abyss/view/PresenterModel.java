package com.abyss.view;

import javafx.beans.property.SimpleObjectProperty;

import com.abyss.view.main.MainWindowPresenter;
import com.abyss.view.main.droplist.DropListPresenter;
import com.abyss.view.main.editor.DropEditorPresenter;
import com.abyss.view.main.editor.itemlist.ItemListPresenter;

public class PresenterModel {

	private final SimpleObjectProperty<MainWindowPresenter> mainWindowPresenterObjectProperty = new SimpleObjectProperty<MainWindowPresenter>();

	private final SimpleObjectProperty<DropEditorPresenter> dropEditorPresenter = new SimpleObjectProperty<DropEditorPresenter>();

	private final SimpleObjectProperty<DropListPresenter> dropListPresenter = new SimpleObjectProperty<DropListPresenter>();


	private final SimpleObjectProperty<ItemListPresenter> itemListPresenter = new SimpleObjectProperty<ItemListPresenter>();

	
	public final void setMainWindowPresenter(MainWindowPresenter value) {
		mainWindowPresenterObjectProperty.set(value);
	}

	public final MainWindowPresenter getMainWindowPresenter() {
		return mainWindowPresenterObjectProperty.get();
	}

	public final void setDropEditorPresenter(DropEditorPresenter value) {
		dropEditorPresenter.set(value);
	}

	public final DropEditorPresenter getDropEditorPresenter() {
		return dropEditorPresenter.get();
	}

	public final void setDropListPresenter(DropListPresenter value) {
		dropListPresenter.set(value);
	}

	public final DropListPresenter getDropListPresenter() {
		return dropListPresenter.get();
	}

	public ItemListPresenter getItemListPresenter() {
		return itemListPresenter.get();
	}

	public final void setItemListPresenter(ItemListPresenter value) {
		itemListPresenter.set(value);
	}

}