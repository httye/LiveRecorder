package com.liverecorder.util;

import com.liverecorder.LiveRecorder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
    private double positionSmooth;        // 位置平滑系数（0.0~1.0），值越小越平滑
    private double rotationSmooth;        // 视角平滑系数（0.0~1.0），值越小越平滑

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
        positionSmooth = plugin.getConfig().getDouble("camera.position-smooth", 0.12);
        rotationSmooth = plugin.getConfig().getDouble("camera.rotation-smooth", 0.1);
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
     * 计算平滑后的镜头状态（位置 + 视角）
     * 使用指数衰减平滑（Exponential Smoothing），让镜头移动更流畅自然
     *
     * 位置平滑：newPos = currentPos + (targetPos - currentPos) * positionSmooth
     * 视角平滑：从当前平滑位置计算看向目标的方向，再对 yaw/pitch 做角度插值
     *
     * @param current      录制者当前位置
     * @param cameraTarget 镜头目标位置（包含目标朝向）
     * @param target       目标玩家（用于计算精确的视角方向）
     * @return 平滑后的镜头位置（包含平滑后的朝向）
     */
    public Location calculateSmoothedState(Location current, Location cameraTarget, Player target) {
        return calculateSmoothedState(current, cameraTarget, target, positionSmooth, rotationSmooth);
    }

    /**
     * 计算平滑后的镜头状态（可自定义平滑系数）
     */
    public Location calculateSmoothedState(Location current, Location cameraTarget, Player target,
                                           double positionSmoothFactor, double rotationSmoothFactor) {
        // ===== 1. 位置平滑插值 =====
        double curX = current.getX();
        double curY = current.getY();
        double curZ = current.getZ();

        double tgtX = cameraTarget.getX();
        double tgtY = cameraTarget.getY();
        double tgtZ = cameraTarget.getZ();

        double smoothX = curX + (tgtX - curX) * positionSmoothFactor;
        double smoothY = curY + (tgtY - curY) * positionSmoothFactor;
        double smoothZ = curZ + (tgtZ - curZ) * positionSmoothFactor;

        // ===== 2. 计算从平滑位置看向目标的方向 =====
        Location targetLoc = target.getLocation();
        Vector toTarget = targetLoc.toVector().subtract(new Vector(smoothX, smoothY, smoothZ));

        double horizontalDist = Math.sqrt(toTarget.getX() * toTarget.getX() + toTarget.getZ() * toTarget.getZ());

        // 计算目标 yaw 和 pitch
        float targetYaw = (float) Math.toDegrees(Math.atan2(-toTarget.getX(), toTarget.getZ()));
        float targetPitch = (float) Math.toDegrees(Math.atan2(-toTarget.getY(), horizontalDist));

        // ===== 3. 视角平滑插值（处理 360° 环绕） =====
        float smoothYaw = interpolateAngle(current.getYaw(), targetYaw, (float) rotationSmoothFactor);
        float smoothPitch = interpolateAngle(current.getPitch(), targetPitch, (float) rotationSmoothFactor);

        // ===== 4. 构建结果 =====
        return new Location(
                cameraTarget.getWorld(),
                smoothX, smoothY, smoothZ,
                smoothYaw, smoothPitch
        );
    }

    /**
     * 角度平滑插值（处理 360° 环绕）
     * 始终选择最短旋转路径，避免视角突然翻转
     *
     * @param current  当前角度
     * @param target   目标角度
     * @param smooth   平滑系数 (0.0~1.0)
     * @return 插值后的角度
     */
    public float interpolateAngle(float current, float target, float smooth) {
        float diff = target - current;
        // 归一化到 [-180, 180]，选择最短旋转路径
        while (diff > 180f) diff -= 360f;
        while (diff < -180f) diff += 360f;
        return current + diff * smooth;
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

    public double getPositionSmooth() {
        return positionSmooth;
    }

    public double getRotationSmooth() {
        return rotationSmooth;
    }
}
