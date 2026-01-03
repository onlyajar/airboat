package onlyajar.airboat.utils;

import java.lang.reflect.Field;

public final class FieldUtils {
    private FieldUtils(){ throw new UnsupportedOperationException(); }


    public static Object readField(final Field field, final Object target) {
        return readField(field, target, true);
    }

    public static Object readField(final Field field, final Object target, final boolean forceAccess) {
        try{
            if (forceAccess && !field.isAccessible()) {
                field.setAccessible(true);
            } else {
                MemberUtils.setAccessibleWorkaround(field);
            }
            return field.get(target);
        }catch (Exception e){
            return null;
        }
    }

    public static void writeField(final Field field, final Object target, final Object value){
        writeField(field, target, value, false);
    }


    public static void writeField(final Field field, final Object target, final Object value, final boolean forceAccess) {
        try {
            if (forceAccess && !field.isAccessible()) {
                field.setAccessible(true);
            } else {
                MemberUtils.setAccessibleWorkaround(field);
            }
            field.set(target, value);
        }catch (Exception e){

        }
    }
}
