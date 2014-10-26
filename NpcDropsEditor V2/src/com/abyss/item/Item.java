package com.abyss.item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.openrs.cache.CacheManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import com.abyss.resource.ItemImages;
import com.abyss.resource.ItemNames;

public class Item {

	private IntegerProperty id;
	private StringProperty name;

	private ObjectProperty<Image> image;

	public Item(int id) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(ItemNames.getItemName(id));
		this.image = new SimpleObjectProperty<Image>(ItemImages.getItemImages()
				.get(id));
	}

	public int getId() {
		return id.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getName() {
		return name.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public ObjectProperty<Image> imageProperty() {
		if (image.get() == null)
			fetchImage();
		return image;
	}

	public Image getImage() {
		if (image.get() == null)
			fetchImage();
		return image.get();
	}

	private void fetchImage() {
		ByteBuffer dataBuffer = null;
		try {
			dataBuffer = CacheManager.cache.read(0, getId()).getData();
		} catch (IOException | NullPointerException e) {

		}
		if (dataBuffer != null) {
			byte[] bytes = new byte[dataBuffer.limit()];
			dataBuffer.mark();
			dataBuffer.get(bytes);
			dataBuffer.reset();
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			Image itemImage = new Image(stream);
			image.set(itemImage);
			ItemImages.getItemImages().put(getId(), itemImage);
		} else {
			image.set(ItemImages.QUESTION_IMAGE);
			ItemImages.getItemImages().put(getId(), ItemImages.QUESTION_IMAGE);
		}
	}

	@Override
	public String toString() {
		return getId() + " - " + getName();
	}
}
