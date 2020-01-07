package com.smalsus.redhorizonvbr;

import androidx.annotation.Nullable;

public class RedHorizonValidator {


    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public static  boolean isEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public  static boolean validateConfirmPassword(String pwd, String cnfPwd){
        if((pwd == null && pwd.length()<=0) || (cnfPwd == null && cnfPwd.length()<=0)){
            return true;
        }
        pwd = pwd.trim();
        cnfPwd = cnfPwd.trim();
        if(pwd.length() > 0  && cnfPwd.length() > 0){
            if(pwd.equals(cnfPwd)){
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }
}

