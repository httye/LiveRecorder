package com.liverecorder.util;

import com.liverecorder.LiveRecorder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 镜头几何算法工具类
 * 使用 BigDecimal 实现高精度计算
 * 镜头位置 = 目标玩家位置 + 俯角/距离偏移
 */
public class CameraGeometry {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);
    private static final BigDecimal HALF_CIRCLE = new BigDecimal("180");
    private static final BigDecimal PI = new BigDecimal(Math.PI, MC);

    private BigDecimal cameraPitch;      // 俯角（度数）
    private BigDecimal cameraDistance;    // 水平距离（格）
    private BigDecimal heightOffset;      // 额外高度偏移
    private BigDecimal followSpeed;       // 跟随速度系数
    private BigDecimal arrivalThreshold;  // 到达阈值

    private final LiveRecorder plugin;

    public CameraGeometry(LiveRecorder plugin) {
        this.plugin = plugin;
        reload();
    }

    /**
     * 从配置文件重新加载参数
     */
    public void reload() {
        cameraPitch = new BigDecimal(String.valueOf(plugin.getConfig().getDouble("camera.pitch", 30.0)));
        cameraDistance = new BigDecimal(String.valueOf(plugin.getConfig().getDouble("camera.distance", 5.0)));
        heightOffset = new BigDecimal(String.valueOf(plugin.getConfig().getDouble("camera.height-offset", 0.0)));
        followSpeed = new BigDecimal(String.valueOf(plugin.getConfig().getDouble("camera.follow-speed", 0.35)));
        arrivalThreshold = new BigDecimal(String.valueOf(plugin.getConfig().getDouble("camera.arrival-threshold", 0.3)));
    }

    /**
     * 计算镜头目标位置
     * 基于目标玩家的位置和朝向，计算录制者应该处于的镜头位置
     *
     * @param target 目标玩家
     * @return 镜头目标位置
     */
    public Location calculateCameraLocation(Player target) {
        Location targetLoc = target.getLocation();

        // 获取目标玩家的朝向角度（yaw），转换为弧度
        BigDecimal yawRad = toRadians(new BigDecimal(String.valueOf(targetLoc.getYaw())));

        // 计算水平偏移
        // 镜头在玩家身后，所以偏移方向与朝向相反
        // dx = -distance * sin(yaw)
        // dz = distance * cos(yaw)
        BigDecimal sinYaw = sin(yawRad);
        BigDecimal cosYaw = cos(yawRad);

        BigDecimal dx = cameraDistance.negate().multiply(sinYaw, MC);
        BigDecimal dz = cameraDistance.multiply(cosYaw, MC);

        // 计算垂直偏移
        // 根据俯角计算高度：height = distance * tan(pitch) + heightOffset
        BigDecimal pitchRad = toRadians(cameraPitch);
        BigDecimal tanPitch = tan(pitchRad);
        BigDecimal dy = cameraDistance.multiply(tanPitch, MC).add(heightOffset, MC);

        // 构建镜头位置
        Location cameraLoc = targetLoc.clone();
        cameraLoc.add(dx.doubleValue(), dy.doubleValue(), dz.doubleValue());

        // 镜头朝向目标玩家
        cameraLoc.setDirection(targetLoc.toVector().subtract(cameraLoc.toVector()));

        return cameraLoc;
    }

    /**
     * 计算跟随速度向量
     * 使用 setVelocity 方式实时跟随
     *
     * @param recorder 录制者
     * @param cameraTarget 镜头目标位置
     * @return 速度向量（作为 Location 的方向向量表示）
     */
    public Location calculateFollowVelocity(Player recorder, Location cameraTarget) {
        Location recorderLoc = recorder.getLocation();

        // 计算当前位置与目标位置的差值
        BigDecimal rx = new BigDecimal(String.valueOf(recorderLoc.getX()));
        BigDecimal ry = new BigDecimal(String.valueOf(recorderLoc.getY()));
        BigDecimal rz = new BigDecimal(String.valueOf(recorderLoc.getZ()));

        BigDecimal tx = new BigDecimal(String.valueOf(cameraTarget.getX()));
        BigDecimal ty = new BigDecimal(String.valueOf(cameraTarget.getY()));
        BigDecimal tz = new BigDecimal(String.valueOf(cameraTarget.getZ()));

        BigDecimal diffX = tx.subtract(rx, MC);
        BigDecimal diffY = ty.subtract(ry, MC);
        BigDecimal diffZ = tz.subtract(rz, MC);

        // 计算距离
        BigDecimal distSq = diffX.multiply(diffX, MC)
                .add(diffY.multiply(diffY, MC), MC)
                .add(diffZ.multiply(diffZ, MC), MC);
        double distance = sqrt(distSq).doubleValue();

        // 如果距离小于到达阈值，不需要移动
        if (distance < arrivalThreshold.doubleValue()) {
            return null;
        }

        // 应用跟随速度系数
        // 速度 = 差值 * 跟随速度系数
        // 但要限制最大速度，避免瞬移感
        double speed = followSpeed.doubleValue();

        // 距离越大，速度越快（弹性跟随）
        double velocityX = diffX.doubleValue() * speed;
        double velocityY = diffY.doubleValue() * speed;
        double velocityZ = diffZ.doubleValue() * speed;

        // 限制最大速度
        double maxSpeed = 2.0;
        double magnitude = Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
        if (magnitude > maxSpeed) {
            double scale = maxSpeed / magnitude;
            velocityX *= scale;
            velocityY *= scale;
            velocityZ *= scale;
        }

        // 构建结果位置（使用 Location 存储速度向量信息）
        Location result = recorderLoc.clone();
        result.setDirection(cameraTarget.getDirection());
        result.setX(recorderLoc.getX() + velocityX);
        result.setY(recorderLoc.getY() + velocityY);
        result.setZ(recorderLoc.getZ() + velocityZ);

        return result;
    }

    /**
     * 检查录制者是否需要传送（距离过远时）
     *
     * @param recorder 录制者
     * @param cameraTarget 镜头目标位置
     * @param threshold 传送阈值距离
     * @return 是否需要传送
     */
    public boolean needsTeleport(Player recorder, Location cameraTarget, double threshold) {
        if (!recorder.getWorld().equals(cameraTarget.getWorld())) {
            return true;
        }
        BigDecimal rx = new BigDecimal(String.valueOf(recorder.getLocation().getX()));
        BigDecimal ry = new BigDecimal(String.valueOf(recorder.getLocation().getY()));
        BigDecimal rz = new BigDecimal(String.valueOf(recorder.getLocation().getZ()));

        BigDecimal tx = new BigDecimal(String.valueOf(cameraTarget.getX()));
        BigDecimal ty = new BigDecimal(String.valueOf(cameraTarget.getY()));
        BigDecimal tz = new BigDecimal(String.valueOf(cameraTarget.getZ()));

        BigDecimal diffX = tx.subtract(rx, MC);
        BigDecimal diffY = ty.subtract(ry, MC);
        BigDecimal diffZ = tz.subtract(rz, MC);

        BigDecimal distSq = diffX.multiply(diffX, MC)
                .add(diffY.multiply(diffY, MC), MC)
                .add(diffZ.multiply(diffZ, MC), MC);

        BigDecimal thresholdSq = new BigDecimal(String.valueOf(threshold * threshold));

        return distSq.compareTo(thresholdSq) > 0;
    }

    // ========== BigDecimal 数学工具方法 ==========

    /**
     * 角度转弧度
     */
    private BigDecimal toRadians(BigDecimal degrees) {
        return degrees.multiply(PI, MC).divide(HALF_CIRCLE, MC);
    }

    /**
     * BigDecimal 正弦函数（使用泰勒级数展开）
     */
    private BigDecimal sin(BigDecimal x) {
        // 将 x 归一化到 [-π, π]
        x = x.remainder(PI.multiply(new BigDecimal("2"), MC), MC);
        if (x.compareTo(PI) > 0) {
            x = x.subtract(PI.multiply(new BigDecimal("2"), MC), MC);
        }
        if (x.compareTo(PI.negate()) < 0) {
            x = x.add(PI.multiply(new BigDecimal("2"), MC), MC);
        }

        // 泰勒级数: sin(x) = x - x³/3! + x⁵/5! - x⁷/7! + ...
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        for (int i = 1; i <= 15; i += 2) {
            if (i % 4 == 1) {
                result = result.add(term, MC);
            } else {
                result = result.subtract(term, MC);
            }
            // term = term * x² / ((i+1)(i+2))
            term = term.multiply(x, MC).multiply(x, MC);
            term = term.divide(new BigDecimal((long) (i + 1) * (i + 2)), MC);
        }
        return result;
    }

    /**
     * BigDecimal 余弦函数
     */
    private BigDecimal cos(BigDecimal x) {
        // cos(x) = sin(x + π/2)
        return sin(x.add(PI.divide(new BigDecimal("2"), MC), MC));
    }

    /**
     * BigDecimal 正切函数
     */
    private BigDecimal tan(BigDecimal x) {
        BigDecimal cosVal = cos(x);
        if (cosVal.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal(Double.MAX_VALUE);
        }
        return sin(x).divide(cosVal, MC);
    }

    /**
     * BigDecimal 平方根（牛顿迭代法）
     */
    private BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal x0 = new BigDecimal(Math.sqrt(value.doubleValue()));
        BigDecimal two = new BigDecimal("2");
        for (int i = 0; i < 10; i++) {
            x0 = x0.add(value.divide(x0, MC), MC).divide(two, MC);
        }
        return x0;
    }

    // ========== Getter 方法 ==========

    public double getCameraPitch() {
        return cameraPitch.doubleValue();
    }

    public double getCameraDistance() {
        return cameraDistance.doubleValue();
    }

    public double getFollowSpeed() {
        return followSpeed.doubleValue();
    }

    public double getArrivalThreshold() {
        return arrivalThreshold.doubleValue();
    }
}