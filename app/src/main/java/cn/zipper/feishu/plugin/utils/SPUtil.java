package cn.zipper.feishu.plugin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author Liudeli
 * @Describe：
 */
public class SPUtil {

    /**
     * 保存在手机里面的文件名
     */
     public static final String FILE_NAME = "sp_core";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    /*public static void put(Context context, String key, Object object) {
        put(context, FILE_NAME, key, object);
    }*/

    public static void put(Context context, String spFileName, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    public static void putString(Context context, String spFileName, String key, String stringData) {
        SharedPreferences sp = context.getSharedPreferences(spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, stringData);
        editor.commit();
        // SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    /*public static Object get(Context context, String key, Object defaultObject) {
        return get(context, FILE_NAME, key, defaultObject);
    }*/

    public static Object get(Context context, String spName, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(spName,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static String getString(Context context, String spName, String key, String stringDataDefault) {
        SharedPreferences sp = context.getSharedPreferences(spName,
                Context.MODE_PRIVATE);
        return sp.getString(key, stringDataDefault);
    }

    /**
     * 移除某个key值已经对应的值
     */
    /*public static void remove(Context context, String key) {
        remove(context, FILE_NAME, key);
    }*/

    public static void remove(Context context, String spFileName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    /*public static void clear(Context context) {
        clear(context, FILE_NAME);
    }*/

    public static void clear(Context context, String spFileName) {
        SharedPreferences sp = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     */
    /*public static boolean contains(Context context, String key) {
        return contains(context, FILE_NAME, key);
    }*/

    public static boolean contains(Context context, String spFileName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    /*public static Map<String, ?> getAll(Context context) {
        return getAll(context, FILE_NAME);
    }*/

    public static Map<String, ?> getAll(Context context, String spName) {
        SharedPreferences sp = context.getSharedPreferences(spName,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }
}