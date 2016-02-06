package com.curus.utils;

import com.curus.utils.constant.IssueConst;

/**
 * Created by stupid-coder on 27/1/16.
 */
public class IssueUtils {

    static public Integer getGroupPrefix(String issue) {
        if ( IssueConst.ISSUES_IDS.containsKey(issue) ) {
            return Integer.parseInt(IssueConst.ISSUES_IDS.get(issue).toString())/100;
        } return 0;
    }

    static public String getGroupKey(String issue) {
        switch ( getGroupPrefix(issue) ) {
            case 1:
                return IssueConst.GROUP_HABIT_KEY;
            case 2:
                return IssueConst.GROUP_PHYSIOLOGY_KEY;
            case 3:
                return IssueConst.GROUP_ILLNESS_KEY;
            default:
                return IssueConst.GROUP_UNKNOWN_KEY;
        }
    }

}
