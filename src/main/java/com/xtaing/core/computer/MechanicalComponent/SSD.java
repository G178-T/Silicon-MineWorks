package com.xtaing.core.computer.MechanicalComponent;

import com.xtaing.core.computer.MechanicalPart;

/**
 * SSD是固态硬盘，本类用于描述固态硬盘的规格。
 */
public class SSD extends MechanicalPart {
    public SSD(String model, int capacity, double cost) {
        super(model, capacity, cost);
    }
}
