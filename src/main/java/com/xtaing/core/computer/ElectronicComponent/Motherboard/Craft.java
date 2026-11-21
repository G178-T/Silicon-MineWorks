package com.xtaing.core.computer.ElectronicComponent.Motherboard;

import com.xtaing.core.computer.ElectronicPart;

/**
 * Motherboard是主板，本类用于描述主板的制造规格。
 */
public class Craft extends ElectronicPart {
    public Craft(String model, int performance, double cost) {
        super(model, performance, cost);
    }
}
