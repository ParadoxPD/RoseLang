package org.code.utils;

import java.lang.reflect.Array;
import java.util.Vector;

public class Helper {

    public static byte[] vectorToByteArray(Vector<Byte> bytes) {
        byte[] outBytes = new byte[bytes.size()];
        int i = 0;
        for (byte val : bytes) {
            outBytes[i++] = val;
        }
        return outBytes;
    }

    public static Vector<Byte> byteArrayToVector(byte[] bytes) {
        Vector<Byte> outBytes = new Vector<>(bytes.length);
        for (byte val : bytes) {
            outBytes.add(val);
        }
        return outBytes;
    }

    public static <T> Vector<T> slice(Vector<T> list, int start, int end) {
        Vector<T> res = new Vector<>();
        for (int i = start; i < end; i++) {
            res.add(list.get(i));
        }
        return res;
    }

    public static <T> T[] slice(T[] list, int start, int end, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T[] res = (T[]) Array.newInstance(clazz, end - start);
        for (int i = start; i < end; i++) {
            res[i - start] = list[i];
        }
        return res;
    }
}
