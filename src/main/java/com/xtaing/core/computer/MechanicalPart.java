package com.xtaing.core.computer;

/**
 * MechanicalPart是机械组件，本类提供机械组件的公共字段。
 */
public abstract class MechanicalPart implements ComputerPart {
    private String model;
    private int capacity;
    private double cost;

    public MechanicalPart(String model, int capacity, double cost) {
        this.model = model;
        this.capacity = capacity;
        this.cost = cost;
    }

    public int getCapacity() {
        return capacity;
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
