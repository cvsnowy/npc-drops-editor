package com.abyss.resource;

import java.util.Map;

import javafx.collections.FXCollections;
import javafx.scene.image.Image;

public class ItemImages {

	public static final Image QUESTION_IMAGE = new Image(
			ItemImages.class.getResourceAsStream("/resource/image/question.png"));

	private static Map<Integer, Image> itemImages = FXCollections
			.observableHashMap();

	public static Map<Integer, Image> getItemImages() {
		return itemImages;
	}

}
