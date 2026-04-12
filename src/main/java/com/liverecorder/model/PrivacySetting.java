package com.liverecorder.model;

import java.util.UUID;

/**
 * 隐私设置数据类
 * 维护玩家的隐私偏好设置
 */
public class PrivacySetting {

    /**
     * 隐私同意状态
     */
    public enum ConsentStatus {
        /** 同意被直播 */
        ACCEPTED,
        /** 拒绝被直播 */
        DECLINED,
        /** 未设置，需要询问 */
        UNSET
    }

    /** 玩家 UUID */
    private final UUID playerUuid;

    /** 玩家名称 */
    private String playerName;

    /** 隐私同意状态 */
    private ConsentStatus consentStatus;

    /** 是否隐身 */
    private boolean invisible;

    /** 最后更新时间戳 */
    private long lastUpdated;

    /**
     * 创建新的隐私设置
     *
     * @param playerUuid 玩家 UUID
     * @param playerName 玩家名称
     */
    public PrivacySetting(UUID playerUuid, String playerName) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.consentStatus = ConsentStatus.UNSET;
        this.invisible = false;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 创建新的隐私设置（带初始状态）
     *
     * @param playerUuid   玩家 UUID
     * @param playerName   玩家名称
     * @param consentStatus 初始同意状态
     */
    public PrivacySetting(UUID playerUuid, String playerName, ConsentStatus consentStatus) {
        this(playerUuid, playerName);
        this.consentStatus = consentStatus;
    }

    /**
     * 更新玩家名称
     *
     * @param playerName 新玩家名称
     */
    public void updatePlayerName(String playerName) {
        this.playerName = playerName;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 设置隐私同意状态
     *
     * @param consentStatus 新的同意状态
     */
    public void setConsentStatus(ConsentStatus consentStatus) {
        this.consentStatus = consentStatus;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 设置隐身状态
     *
     * @param invisible 是否隐身
     */
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
        this.lastUpdated = System.currentTimeMillis();
    }

    /**
     * 检查玩家是否可以被直播
     *
     * @return 是否可以直播
     */
    public boolean canBeStreamed() {
        return consentStatus == ConsentStatus.ACCEPTED;
    }

    /**
     * 检查玩家是否拒绝被直播
     *
     * @return 是否拒绝
     */
    public boolean hasDeclined() {
        return consentStatus == ConsentStatus.DECLINED;
    }

    /**
     * 检查是否需要询问玩家
     *
     * @return 是否需要询问
     */
    public boolean needsPrompt() {
        return consentStatus == ConsentStatus.UNSET;
    }

    // ========== Getter / Setter ==========

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ConsentStatus getConsentStatus() {
        return consentStatus;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return "PrivacySetting{" +
                "playerUuid=" + playerUuid +
                ", playerName='" + playerName + '\'' +
                ", consentStatus=" + consentStatus +
                ", invisible=" + invisible +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
