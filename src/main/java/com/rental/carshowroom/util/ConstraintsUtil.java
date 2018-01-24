package com.rental.carshowroom.util;

public class ConstraintsUtil {
    public static String getConstraintMessageFromException(Throwable throwable) {
        String msg = throwable.getLocalizedMessage();
        StringBuilder builder;
        builder = new StringBuilder(msg.split("'")[1]);
        if(msg.toUpperCase().contains("UNIQUE")) {
            builder.append(" is not unique");
        }
        else {
            builder.append(" unknown constraint violation");
        }
        return builder.toString();
    }

    public static String getConstraintNameFromException(Throwable throwable) {
        String msg = throwable.getLocalizedMessage();
        return msg.split("'")[1];
    }
}
