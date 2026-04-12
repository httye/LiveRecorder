package com.liverecorder.database;

import com.liverecorder.LiveRecorder;
import com.liverecorder.model.LiveLog;
import com.liverecorder.model.PrivacySetting;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * 数据库管理器
 * 使用 SQLite 存储隐私设置和直播日志
 */
public class DatabaseManager {

    private final LiveRecorder plugin;
    private Connection connection;

    public DatabaseManager(LiveRecorder plugin) {
        this.plugin = plugin;
        initialize();
    }

    /**
     * 初始化数据库
     */
    private void initialize() {
        try {
            // 获取数据文件夹
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            // 数据库文件路径
            File dbFile = new File(dataFolder, "privacy.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            // 加载 SQLite JDBC 驱动
            Class.forName("org.sqlite.JDBC");

            // 建立连接
            connection = DriverManager.getConnection(url);
            plugin.getLogger().info("数据库连接成功: " + dbFile.getAbsolutePath());

            // 创建表
            createTables();

        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "数据库初始化失败", e);
        }
    }

    /**
     * 创建数据表
     */
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // 创建隐私设置表
            String createPrivacyTable = "CREATE TABLE IF NOT EXISTS privacy_settings (" +
                    "player_uuid TEXT PRIMARY KEY," +
                    "player_name TEXT NOT NULL," +
                    "consent_status TEXT NOT NULL," +
                    "invisible INTEGER NOT NULL DEFAULT 0," +
                    "last_updated INTEGER NOT NULL" +
                    ")";
            stmt.execute(createPrivacyTable);

            // 创建直播日志表
            String createLogTable = "CREATE TABLE IF NOT EXISTS live_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "log_type TEXT NOT NULL," +
                    "recorder_uuid TEXT," +
                    "recorder_name TEXT," +
                    "target_uuid TEXT," +
                    "target_name TEXT," +
                    "timestamp INTEGER NOT NULL," +
                    "extra_info TEXT" +
                    ")";
            stmt.execute(createLogTable);

            plugin.getLogger().info("数据表创建成功");

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "创建数据表失败", e);
        }
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("数据库连接已关闭");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "关闭数据库连接失败", e);
        }
    }

    // ========== 隐私设置操作 ==========

    /**
     * 获取玩家的隐私设置
     *
     * @param playerUuid 玩家 UUID
     * @return 隐私设置，不存在则返回 null
     */
    public PrivacySetting getPrivacySetting(UUID playerUuid) {
        String sql = "SELECT * FROM privacy_settings WHERE player_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String playerName = rs.getString("player_name");
                String consentStatusStr = rs.getString("consent_status");
                boolean invisible = rs.getBoolean("invisible");

                PrivacySetting.ConsentStatus consentStatus =
                        PrivacySetting.ConsentStatus.valueOf(consentStatusStr);

                PrivacySetting setting = new PrivacySetting(playerUuid, playerName, consentStatus);
                setting.setInvisible(invisible);

                return setting;
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取隐私设置失败", e);
        }

        return null;
    }

    /**
     * 保存或更新隐私设置
     *
     * @param setting 隐私设置
     * @return 是否成功
     */
    public boolean savePrivacySetting(PrivacySetting setting) {
        String sql = "INSERT OR REPLACE INTO privacy_settings " +
                "(player_uuid, player_name, consent_status, invisible, last_updated) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, setting.getPlayerUuid().toString());
            stmt.setString(2, setting.getPlayerName());
            stmt.setString(3, setting.getConsentStatus().name());
            stmt.setBoolean(4, setting.isInvisible());
            stmt.setLong(5, setting.getLastUpdated());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "保存隐私设置失败", e);
            return false;
        }
    }

    /**
     * 删除隐私设置
     *
     * @param playerUuid 玩家 UUID
     * @return 是否成功
     */
    public boolean deletePrivacySetting(UUID playerUuid) {
        String sql = "DELETE FROM privacy_settings WHERE player_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "删除隐私设置失败", e);
            return false;
        }
    }

    /**
     * 获取所有同意直播的玩家 UUID
     *
     * @return UUID 列表
     */
    public List<UUID> getAcceptedPlayers() {
        List<UUID> players = new ArrayList<>();
        String sql = "SELECT player_uuid FROM privacy_settings WHERE consent_status = 'ACCEPTED'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                players.add(UUID.fromString(rs.getString("player_uuid")));
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取同意直播的玩家列表失败", e);
        }

        return players;
    }

    /**
     * 获取所有拒绝直播的玩家 UUID
     *
     * @return UUID 列表
     */
    public List<UUID> getDeclinedPlayers() {
        List<UUID> players = new ArrayList<>();
        String sql = "SELECT player_uuid FROM privacy_settings WHERE consent_status = 'DECLINED'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                players.add(UUID.fromString(rs.getString("player_uuid")));
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取拒绝直播的玩家列表失败", e);
        }

        return players;
    }

    // ========== 直播日志操作 ==========

    /**
     * 添加直播日志
     *
     * @param log 日志对象
     * @return 是否成功
     */
    public boolean addLiveLog(LiveLog log) {
        String sql = "INSERT INTO live_logs " +
                "(log_type, recorder_uuid, recorder_name, target_uuid, target_name, timestamp, extra_info) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, log.getLogType().name());
            stmt.setString(2, log.getRecorderUuid() != null ? log.getRecorderUuid().toString() : null);
            stmt.setString(3, log.getRecorderName());
            stmt.setString(4, log.getTargetUuid() != null ? log.getTargetUuid().toString() : null);
            stmt.setString(5, log.getTargetName());
            stmt.setLong(6, log.getTimestamp());
            stmt.setString(7, log.getExtraInfo());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "添加直播日志失败", e);
            return false;
        }
    }

    /**
     * 获取直播日志
     *
     * @param limit 最多返回多少条
     * @return 日志列表
     */
    public List<LiveLog> getLiveLogs(int limit) {
        List<LiveLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM live_logs ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String logTypeStr = rs.getString("log_type");
                LiveLog.LogType logType = LiveLog.LogType.valueOf(logTypeStr);

                UUID recorderUuid = null;
                String recorderUuidStr = rs.getString("recorder_uuid");
                if (recorderUuidStr != null) {
                    recorderUuid = UUID.fromString(recorderUuidStr);
                }

                String recorderName = rs.getString("recorder_name");

                UUID targetUuid = null;
                String targetUuidStr = rs.getString("target_uuid");
                if (targetUuidStr != null) {
                    targetUuid = UUID.fromString(targetUuidStr);
                }

                String targetName = rs.getString("target_name");
                long timestamp = rs.getLong("timestamp");
                String extraInfo = rs.getString("extra_info");

                LiveLog log = new LiveLog(id, logType, recorderUuid, recorderName,
                        targetUuid, targetName, timestamp, extraInfo);
                logs.add(log);
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取直播日志失败", e);
        }

        return logs;
    }

    /**
     * 清理旧的日志（保留最近 N 条）
     *
     * @param keepCount 保留数量
     * @return 删除的记录数
     */
    public int cleanupOldLogs(int keepCount) {
        String sql = "DELETE FROM live_logs WHERE id NOT IN " +
                "(SELECT id FROM live_logs ORDER BY timestamp DESC LIMIT ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, keepCount);
            int deleted = stmt.executeUpdate();
            plugin.getLogger().info("清理了 " + deleted + " 条旧日志");
            return deleted;

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "清理旧日志失败", e);
            return 0;
        }
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public Connection getConnection() {
        return connection;
    }
}