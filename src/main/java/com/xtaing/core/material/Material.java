package com.xtaing.core.material;

/**
 * Material是原材料，本类提供原材料的公共字段。
 */
public abstract class Material {
    private String name;
    private int amount;
    private double value;
    private double purity;

    public Material(String name, int amount, double value, double purity) {
        this.name = name;
        this.amount = amount;
        this.value = value;
        this.purity = purity;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public double getValue() {
        return value;
    }

    public double getPurity() {
        return purity;
    }
}
