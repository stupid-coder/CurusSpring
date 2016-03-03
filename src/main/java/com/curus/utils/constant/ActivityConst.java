package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class ActivityConst {
    public static Map<String,Double> ACTIVITY_ENERGY = new HashMap<String, Double>() {{
        put("MediumWalk ",10.0); // 中速步行
        put("FastWalk",9.1); // 较快步行
        put("BriskWalk",7.5); // 快速步行
        put("Scurry",6.0); // 疾走
        put("SlowRide",10.0); // 慢骑
        put("MediumRide",7.5); // 中速
        put("FastRide",5.0); // 快骑
        put("SweepFloor",9.0); // 清扫地毯地板
        put("MopFloor",8.0); // 拖地， 吸尘
        put("JogSmallStep",6.7); // 小步慢跑
        put("Jogging",4.3); // 慢跑
        put("MediumRun",3.8); // 跑步 8千米／小时
        put("FastRun",3.0); // 跑步 9.6千米／小时
        put("TableTennis",7.5); // 打乒乓球
        put("Badminton",6.7); // 打羽毛球或者高尔夫球
        put("Tennis",6.0); // 打网球
        put("Basketball",5.0); // 打篮球
        put("Football",4.3); // 踢足球
        put("BallGames",3.0); // 球类比赛
        put("SlowRopeSkip",3.8); // 跳绳慢
        put("MediumRopeSkip",3.0); // 跳绳中
        put("Dance",10.0);// 跳舞
        put("Exercise",8.6); // 做操
        put("TaijiBoxing",7.5); // 太极拳
        put("FastDance",6.7); // 舞厅快舞
        put("DownMountain",10.0); // 下山、下楼
        put("ClimbMountain",7.5); // 中慢速爬山或上楼
        put("GymExercise",5.5); // 一般健身房练习
        put("Swimming",4.3); // 游泳
        put("RollerSkat",4.3); // 轮滑旱冰
        put("Skating",3.8); // 滑冰
    }};
}
