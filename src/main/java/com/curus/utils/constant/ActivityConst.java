package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 28/2/16.
 */
public class ActivityConst {
    public static Map<String,Double> ACTIVITY_ENERGY = new HashMap<String, Double>() {{
        put("1",10.0); // 中速步行
        put("2",9.1); // 较快步行
        put("3",7.5); // 快速步行
        put("4",6.0); // 疾走
        put("5",10.0); // 慢骑
        put("6",7.5); // 中速
        put("7",5.0); // 快骑
        put("8",9.0); // 清扫地毯地板
        put("9",8.0); // 拖地， 吸尘
        put("10",6.7); // 小步慢跑
        put("11",4.3); // 慢跑
        put("12",3.8); // 跑步 8千米／小时
        put("13",3.0); // 跑步 9.6千米／小时
        put("14",7.5); // 打乒乓球
        put("15",6.7); // 打羽毛球或者高尔夫球
        put("16",6.0); // 打网球
        put("17",5.0); // 打篮球
        put("18",4.3); // 踢足球
        put("19",3.0); // 球类比赛
        put("20",3.8); // 跳绳慢
        put("21",3.0); // 跳绳中
        put("22",10.0);// 跳舞
        put("23",8.6); // 做操
        put("24",7.5); // 太极拳
        put("25",6.7); // 舞厅快舞
        put("26",10.0); // 下山、下楼
        put("27",7.5); // 中慢速爬山或上楼
        put("28",5.5); // 一般健身房练习
        put("29",4.3); // 游泳
        put("30",4.3); // 轮滑旱冰
        put("31",3.8); // 滑冰
    }};
}
