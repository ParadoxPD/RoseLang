package org.code.utils;

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
}
