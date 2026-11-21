package com.xtaing.core.player;

/**
 * Player是玩家，本类描述玩家的资金状态。
 */
public class Player {
    private double money;

    public Player(double money) {
        this.money = money;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
