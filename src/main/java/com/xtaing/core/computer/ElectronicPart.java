package com.xtaing.core.computer;

/**
 * ElectronicPart是电子元件，本类提供电子元件的公共字段。
 */
public abstract class ElectronicPart implements ComputerPart {
    private String model;
    private int performance;
    private double cost;

    public ElectronicPart(String model, int performance, double cost) {
        this.model = model;
        this.performance = performance;
        this.cost = cost;
    }

    public int getPerformance() {
        return performance;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public double getCost() {
        return cost;
    }
}
