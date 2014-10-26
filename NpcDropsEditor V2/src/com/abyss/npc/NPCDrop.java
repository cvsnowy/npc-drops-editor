package com.abyss.npc;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import com.abyss.item.Item;

public class NPCDrop {
	private Item item;
	private IntegerProperty minAmount, maxAmount;
	private DoubleProperty rate;
	private boolean rare;

	public NPCDrop(Item item, double rate, int minAmount, int maxAmount,
			boolean rare) {
		this.item = item;
		this.rate = new SimpleDoubleProperty(rate);
		this.minAmount = new SimpleIntegerProperty(minAmount);
		this.maxAmount = new SimpleIntegerProperty(maxAmount);
		this.rare = rare;
	}

	public DoubleProperty rateProperty() {
		return rate;
	}

	public IntegerProperty minAmountProperty() {
		return minAmount;
	}

	public IntegerProperty maxAmountProperty() {
		return maxAmount;
	}

	public int getMinAmount() {
		return minAmount.get();
	}

	public int getExtraAmount() {
		return getMaxAmount() - getMinAmount();
	}

	public int getMaxAmount() {
		return maxAmount.get();
	}

	public double getRate() {
		return rate.get();
	}

	public Item getItem() {
		return item;
	}

	public boolean getRarity() {
		return rare;
	}

}
