package com.cland.accessstats.util;

public final class Utils {
    public static String toProperCase(String s)
    {
        return (s != null) 
            ? s.substring(0,1).toUpperCase()
                + s.substring(1, s.length()).toLowerCase()
            : null; 
    }
    
    
}
