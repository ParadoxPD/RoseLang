package org.code.utils;

public interface OpCodes {
    byte OpConstant = 1;

    byte OpTrue = 2;
    byte OpFalse = 3;

    byte OpAdd = 4;
    byte OpSub = 5;
    byte OpMul = 7;
    byte OpDiv = 8;
    byte OpPow = 9;

    byte OpEqual = 10;
    byte OpNotEqual = 11;
    byte OpGreaterThan = 12;
    byte OpGreaterThanEqualTo = 13;

    byte OpMinus = 14;
    byte OpBang = 15;

    byte OpPop = 16;

    byte OpJump = 17;
    byte OpJumpNotTruthy = 18;
}
