package com.xtaing.core.computer.ElectronicComponent.CPU;

import com.xtaing.core.computer.ElectronicPart;

/**
 * CPU是中央处理器，本类用于描述CPU的制造规格。
 */
public class Craft extends ElectronicPart {
    public Craft(String model, int performance, double cost) {
        super(model, performance, cost);
    }
}
