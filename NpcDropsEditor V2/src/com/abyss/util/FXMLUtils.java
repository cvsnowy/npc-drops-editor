package com.abyss.util;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public final class FXMLUtils {

	public static final void setAnchorPaneAnchors(Node... nodes) {
		for (Node node : nodes) {
			AnchorPane.setBottomAnchor(node, 0.0);
			AnchorPane.setLeftAnchor(node, 0.0);
			AnchorPane.setRightAnchor(node, 0.0);
			AnchorPane.setTopAnchor(node, 0.0);
		}
	}

	public static final void respectMinimumSize(Stage stage) {
		Scene scene = stage.getScene();
		if (scene.getRoot() instanceof Region) {
			Region root = (Region) scene.getRoot();
			stage.minHeightProperty().bind(
					Bindings.max(
							0,
							stage.heightProperty()
									.subtract(scene.heightProperty())
									.add(root.minHeightProperty())));
			stage.minWidthProperty().bind(
					Bindings.max(
							0,
							stage.widthProperty()
									.subtract(scene.widthProperty())
									.add(root.minWidthProperty())));
		}
	}
}
