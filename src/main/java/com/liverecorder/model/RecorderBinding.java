package com.liverecorder.model;

import org.bukkit.entity.Player;

/**
 * 录制者绑定数据类
 * 维护录制者与目标玩家之间的绑定关系
 */
public class RecorderBinding {

    /**
     * 绑定模式
     */
    public enum Mode {
        /** 自动模式：按照预设节奏自动切换跟拍对象 */
        AUTO,
        /** 手动模式：只能通过命令手动切换跟拍对象 */
        MANUAL
    }

    /** 录制者玩家 */
    private final Player recorder;

    /** 当前绑定的目标玩家 */
    private Player target;

    /** 绑定模式 */
    private Mode mode;

    /** 绑定是否激活（授权有效） */
    private boolean active;

    /** 上次切换目标的时间戳 */
    private long lastSwitchTime;

    /** 是否正在跟随中 */
    private boolean following;

    /**
     * 创建一个新的录制者绑定
     *
     * @param recorder 录制者玩家
     * @param target   初始目标玩家
     * @param mode     绑定模式
     */
    public RecorderBinding(Player recorder, Player target, Mode mode) {
        this.recorder = recorder;
        this.target = target;
        this.mode = mode;
        this.active = true;
        this.lastSwitchTime = System.currentTimeMillis();
        this.following = false;
    }

    /**
     * 切换目标玩家
     *
     * @param newTarget 新的目标玩家
     */
    public void switchTarget(Player newTarget) {
        this.target = newTarget;
        this.lastSwitchTime = System.currentTimeMillis();
    }

    /**
     * 切换绑定模式
     *
     * @param mode 新的绑定模式
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * 检查是否应该自动切换（仅自动模式下生效）
     *
     * @param intervalMs 自动切换间隔（毫秒）
     * @return 是否应该切换
     */
    public boolean shouldAutoSwitch(long intervalMs) {
        if (mode != Mode.AUTO) {
            return false;
        }
        return System.currentTimeMillis() - lastSwitchTime >= intervalMs;
    }

    // ========== Getter / Setter ==========

    public Player getRecorder() {
        return recorder;
    }

    public Player getTarget() {
        return target;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getLastSwitchTime() {
        return lastSwitchTime;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return "RecorderBinding{" +
                "recorder=" + recorder.getName() +
                ", target=" + (target != null ? target.getName() : "null") +
                ", mode=" + mode +
                ", active=" + active +
                '}';
    }
}