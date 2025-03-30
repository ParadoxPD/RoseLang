package org.code.utils;

import java.util.*;

public class Binary {
    public static void putUint16(byte[] ins, short op, int offset) {
        ins[offset] = (byte) ((op >> 8) & 0xff);
        ins[offset + 1] = (byte) ((op >> 0) & 0xff);
    }

    public static int readUint16(byte[] ins, int offset) {
        return (int) ((ins[0 + offset] << 8) + ins[1 + offset]);
    }
}
