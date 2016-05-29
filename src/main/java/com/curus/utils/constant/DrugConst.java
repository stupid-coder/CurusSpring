package com.curus.utils.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stupid-coder on 5/29/16.
 */
public class DrugConst {

    public static Map<Integer,String> COMP_TYPE = new HashMap<Integer,String>() {{
        put(1,"胰岛素");
        put(2,"GLP-1受体激动剂");
        put(3,"促泌剂");
        put(4,"双胍类");
        put(5,"噻唑烷二酮类（TZDs）");
        put(6,"α-糖苷酶抑制剂");
        put(7,"复方糖尿病药物");
    }};

    public static Map<Integer,String> DRUG_FORM = new HashMap<Integer,String>() {{
        put(0,"片");
        put(1,"粒");
        put(2,"胶囊");
        put(3,"袋");
        put(4,"丸");
        put(5,"单位（注射剂）");
        put(6,"ml（注射剂）");
    }};

    public static Map<Integer,String> DRUG_TECH = new HashMap<Integer,String>() {{
        put(0,"普通");
        put(1,"控释");
        put(2,"缓释");
    }};

    public static Map<Integer,String> DRUG_USE = new HashMap<Integer,String>() {{
        put(0,"口服");
        put(1,"注射");
        put(2,"吸入");
        put(3,"外用");
        put(4,"含服");
        put(5,"嚼服");
    }};

    public static Map<Double,String> DRUG_PROCESS = new HashMap<Double,String>() {{
        put(0.1,"速效（急速起效）");
        put(0.3,"短效（针对某一餐或每天至少使用3次）");
        put(0.5,"中效（每天1-2次，可兼顾某一餐）");
        put(1.0,"长效（每天只需1次）");
        put(2.0,"超长效（数天1次）");
    }};

    public static Map<Integer,String> DRUG4ILLNESS = new HashMap<Integer,String>() {{
        put(0,"高血压");
        put(1,"高血糖");
        put(2,"冠心病");
        put(3,"脑卒中");
    }};
}
