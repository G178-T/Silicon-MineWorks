package com.xtaing.core.material;

/**
 * SiliconWafer是硅晶圆，本类描述从市场购买的硅晶圆的属性。
 */
public class SiliconWafer extends Material {
    private int diameter;

    public SiliconWafer(String name, int amount, double value, double purity, int diameter) {
        super(name, amount, value, purity);
        this.diameter = diameter;
    }

    public int getDiameter() {
        return diameter;
    }
}
