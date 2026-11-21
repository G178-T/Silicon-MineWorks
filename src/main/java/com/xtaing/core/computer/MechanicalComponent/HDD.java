package com.xtaing.core.computer.MechanicalComponent;

import com.xtaing.core.computer.MechanicalPart;

/**
 * HDD是机械硬盘，本类用于描述机械硬盘的规格。
 */
public class HDD extends MechanicalPart {
    public HDD(String model, int capacity, double cost) {
        super(model, capacity, cost);
    }
}
