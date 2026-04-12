package com.liverecorder.model;

import java.util.UUID;

/**
 * 直播日志数据类
 * 记录直播操作日志
 */
public class LiveLog {

    /**
     * 日志类型
     */
    public enum LogType {
        /** 开始直播 */
        START,
        /** 结束直播 */
        END,
        /** 切换目标 */
        SWITCH,
        /** 拒绝直播 */
        DECLINED,
        /** 同意直播 */
        ACCEPTED
    }

    /** 日志 ID */
    private final int id;

    /** 日志类型 */
    private final LogType logType;

    /** 录制者 UUID */
    private final UUID recorderUuid;

    /** 录制者名称 */
    private final String recorderName;

    /** 目标玩家 UUID */
    private final UUID targetUuid;

    /** 目标玩家名称 */
    private final String targetName;

    /** 时间戳 */
    private final long timestamp;

    /** 额外信息（可选） */
    private String extraInfo;

    /**
     * 创建直播日志
     *
     * @param id            日志 ID
     * @param logType       日志类型
     * @param recorderUuid  录制者 UUID
     * @param recorderName  录制者名称
     * @param targetUuid    目标玩家 UUID
     * @param targetName    目标玩家名称
     * @param timestamp     时间戳
     */
    public LiveLog(int id, LogType logType, UUID recorderUuid, String recorderName,
                   UUID targetUuid, String targetName, long timestamp) {
        this.id = id;
        this.logType = logType;
        this.recorderUuid = recorderUuid;
        this.recorderName = recorderName;
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.timestamp = timestamp;
        this.extraInfo = "";
    }

    /**
     * 创建直播日志（带额外信息）
     *
     * @param id            日志 ID
     * @param logType       日志类型
     * @param recorderUuid  录制者 UUID
     * @param recorderName  录制者名称
     * @param targetUuid    目标玩家 UUID
     * @param targetName    目标玩家名称
     * @param timestamp     时间戳
     * @param extraInfo     额外信息
     */
    public LiveLog(int id, LogType logType, UUID recorderUuid, String recorderName,
                   UUID targetUuid, String targetName, long timestamp, String extraInfo) {
        this(id, logType, recorderUuid, recorderName, targetUuid, targetName, timestamp);
        this.extraInfo = extraInfo;
    }

    /**
     * 获取日志描述
     *
     * @return 日志描述
     */
    public String getDescription() {
        switch (logType) {
            case START:
                return String.format("录制者 %s 开始直播 %s", recorderName, targetName);
            case END:
                return String.format("录制者 %s 结束直播 %s", recorderName, targetName);
            case SWITCH:
                return String.format("录制者 %s 切换到目标 %s", recorderName, targetName);
            case DECLINED:
                return String.format("玩家 %s 拒绝被直播", targetName);
            case ACCEPTED:
                return String.format("玩家 %s 同意被直播", targetName);
            default:
                return "未知日志类型";
        }
    }

    // ========== Getter ==========

    public int getId() {
        return id;
    }

    public LogType getLogType() {
        return logType;
    }

    public UUID getRecorderUuid() {
        return recorderUuid;
    }

    public String getRecorderName() {
        return recorderName;
    }

    public UUID getTargetUuid() {
        return targetUuid;
    }

    public String getTargetName() {
        return targetName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String toString() {
        return "LiveLog{" +
                "id=" + id +
                ", logType=" + logType +
                ", recorderName='" + recorderName + '\'' +
                ", targetName='" + targetName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
