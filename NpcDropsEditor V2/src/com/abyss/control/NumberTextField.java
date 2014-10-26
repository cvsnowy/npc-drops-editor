package com.abyss.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;

public class NumberTextField extends TextField {

	/**
	 * numericOnly property if set, will allow accept only numeric input.
	 */
	private BooleanProperty numericOnly = new SimpleBooleanProperty(this,
			"numericOnly", false);

	public final boolean isNumericOnly() {
		return numericOnly.getValue();
	}

	public final void setNumericOnly(boolean value) {
		numericOnly.setValue(value);
	}

	public final BooleanProperty numericOnlyProperty() {
		return numericOnly;
	}

	/**
	 * maxSize property , determines the maximum size of the text that can be
	 * input.
	 */
	public IntegerProperty maxSize = new IntegerPropertyBase(1000) {

		@Override
		public String getName() {
			return "maxSize";
		}

		@Override
		public Object getBean() {
			return NumberTextField.this;
		}
	};

	public final IntegerProperty maxSizeProperty() {
		return maxSize;
	};

	public final int getMaxSize() {
		return maxSize.getValue();
	}

	public final void setMaxSize(int value) {
		maxSize.setValue(value);
	}

	/**
	 * this method is called when user inputs text into the textField
	 */
	@Override
	public void replaceText(int start, int end, String text) {
		if (numericOnly.getValue() && !text.equals("")) {
			if (!text.matches("[0-9]")) {
				return;
			}
		}
		if (getText().length() < getMaxSize() || text.equals("")) {
			super.replaceText(start, end, text);
		}
	}

	/**
	 * this method is called when user pastes text into the textField
	 */
	@Override
	public void replaceSelection(String text) {
		if (numericOnly.getValue() && !text.equals("")) {
			if (!text.matches("[0-9]+")) {
				return;
			}
		}
		super.replaceSelection(text);
		if (getText().length() > getMaxSize()) {
			String maxSubString = getText().substring(0, getMaxSize());
			setText(maxSubString);
			positionCaret(getMaxSize());
		}
	}

	/**
	 * Gets a number from the text field. Note that this will only work if the
	 * numbericOnly property is set to true.
	 * 
	 * @return A integer.
	 */
	public Integer getInteger() {
		if (numericOnly.get() == Boolean.TRUE)
			return Integer.parseInt(getText());
		else {
			return -1;
		}
	}
}