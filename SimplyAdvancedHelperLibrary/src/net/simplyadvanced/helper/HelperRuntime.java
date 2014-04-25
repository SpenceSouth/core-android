package net.simplyadvanced.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Helper methods to determine the Android runtime on device (Dalvik or ART). */
public class HelperRuntime {
    private static final String SELECT_RUNTIME_PROPERTY = "persist.sys.dalvik.vm.lib";
    private static final String LIB_DALVIK = "libdvm.so";
    private static final String LIB_ART = "libart.so";
    private static final String LIB_ART_D = "libartd.so";

    private static final String TAG_UNKNOWN = "Unknown";
    private static final String TAG_DALVIK = "Dalvik";
    private static final String TAG_ART = "ART";
    private static final String TAG_ART_DEBUG_BUILD = "ART debug build";

	/** Prevent instantiation of this class. */
	private HelperRuntime() {}
    
    
    /** Returns true is device has the Dalvik runtime. Returns false if ART or unknown. */
    public static final boolean isRuntimeDalvik() {
    	return getCurrentRuntimeValue().equalsIgnoreCase(TAG_DALVIK);
    }

    /** Returns true is device has the ART or ART_DEBUG_BUILD runtime. Returns false if Dalvik or unknown. */
    public static final boolean isRuntimeArt() {
    	return getCurrentRuntimeValue().equalsIgnoreCase(TAG_ART) ||
    			getCurrentRuntimeValue().equalsIgnoreCase(TAG_ART_DEBUG_BUILD);
    }
    
    /** Returns the name of the current runtime TAG_DALVIK, TAG_ART, TAG_ART_DEBUG_BUILD. Or, TAG_UNKNOWN if unknown.
     * 
     * Instead of returning exceptions this just returns TAG_UNKNOWN because YAGNI (You Ain't Gonna Need It)
     * 
     *  @return current runtime */
    private static final String getCurrentRuntimeValue() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            try {
                Method get = systemProperties.getMethod("get", String.class, String.class);
                if (get == null) {
                	// Hopefully, this never happens
                    return TAG_UNKNOWN;
                }
                try {
                    final String value = (String)get.invoke(systemProperties, SELECT_RUNTIME_PROPERTY, /* Assuming default is */"Dalvik");
                    if (LIB_DALVIK.equals(value)) {
                        return TAG_DALVIK;
                    } else if (LIB_ART.equals(value)) {
                        return TAG_ART;
                    } else if (LIB_ART_D.equals(value)) {
                        return TAG_ART_DEBUG_BUILD;
                    }

                    return value;
                } catch (IllegalAccessException e) {
//                    return "IllegalAccessException";
                } catch (IllegalArgumentException e) {
//                    return "IllegalArgumentException";
                } catch (InvocationTargetException e) {
//                    return "InvocationTargetException";
                }
            } catch (NoSuchMethodException e) {
//                return "SystemProperties.get(String key, String def) method is not found";
            }
        } catch (ClassNotFoundException e) {
//            return "SystemProperties class is not found";
        }
        
        return TAG_UNKNOWN;
    }

}