package com.rental.carshowroom.util;

public class ConstraintsUtil {
    public static String getConstraintFromException(Exception e) {
        String msg = e.getLocalizedMessage();
        return msg.substring(msg.lastIndexOf("[") + 1, msg.lastIndexOf("]"));
    }
}
