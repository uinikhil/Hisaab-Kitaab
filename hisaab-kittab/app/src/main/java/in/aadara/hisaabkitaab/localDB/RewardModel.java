package in.aadara.hisaabkitaab.localDB;

/**
 * Created by umashankarpathak on 17/01/18.
 */

public class RewardModel {
    private int reward;
    private int current_capacity;

    public int getCurrent_capacity() {
        return current_capacity;
    }

    public int getReward() {
        return reward;
    }

    public void setCurrent_capacity(int current_capacity) {
        this.current_capacity = current_capacity;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
