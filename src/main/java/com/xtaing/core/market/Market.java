package com.xtaing.core.market;

import com.xtaing.core.material.SiliconWafer;
import com.xtaing.core.player.Player;

/**
 * Market是市场，本类提供材料的购买渠道。
 */
public class Market {
    private double waferPrice;

    public Market(double waferPrice) {
        this.waferPrice = waferPrice;
    }

    /**
     * 购买硅晶圆，资金不足时返回null。
     */
    public SiliconWafer buyWafer(Player player, int amount, double purity, int diameter) {
        double total = waferPrice * amount;
        if (player.getMoney() < total) {
            return null;
        }
        player.setMoney(player.getMoney() - total);
        return new SiliconWafer("硅晶圆", amount, total, purity, diameter);
    }

    public double getWaferPrice() {
        return waferPrice;
    }
}
