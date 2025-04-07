package org.code.utils;

import java.util.AbstractMap;
import java.util.Map;

public interface OpCodes {
    byte OpConstant = 1;

    byte OpTrue = 2;
    byte OpFalse = 3;

    byte OpNull = 4;

    byte OpAdd = 5;
    byte OpSub = 6;
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

    byte OpGetGlobal = 19;
    byte OpSetGlobal = 20;

    byte OpArray = 21;
    byte OpHash = 22;

    byte OpIndex = 23;

    byte OpCall = 24;
    byte OpReturnValue = 25;
    byte OpReturn = 26;

    byte OpGetLocal = 27;
    byte OpSetLocal = 28;

    byte OpGetBuiltin = 29;

    Map<Byte, Definition> Definitions =
            Map.ofEntries(
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpConstant, new Definition("OpConstant", new int[] {2})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpTrue, new Definition("OpTrue", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpFalse, new Definition("OpFalse", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpNull, new Definition("OpNull", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpAdd, new Definition("OpAdd", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpSub, new Definition("OpSub", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpMul, new Definition("OpMul", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpDiv, new Definition("OpDiv", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpPow, new Definition("OpPow", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpEqual, new Definition("OpEqual", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpNotEqual, new Definition("OpNotEqual", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpGreaterThan, new Definition("OpGreaterThan", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpGreaterThanEqualTo,
                            new Definition("OpGreaterThanEqualTo", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpMinus, new Definition("OpMinus", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpBang, new Definition("OpBang", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpPop, new Definition("OpPop", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpJump, new Definition("OpJump", new int[] {2})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpJumpNotTruthy,
                            new Definition("OpJumpNotTruthy", new int[] {2})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpGetGlobal, new Definition("OpGetGlobal", new int[] {2})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpSetGlobal, new Definition("OpSetGlobal", new int[] {2})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpHash, new Definition("OpHash", new int[] {2})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpArray, new Definition("OpArray", new int[] {2})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpIndex, new Definition("OpIndex", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpCall, new Definition("OpCall", new int[] {1})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpReturnValue, new Definition("OpReturnValue", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpReturn, new Definition("OpReturn", new int[] {})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpGetLocal, new Definition("OpGetLocal", new int[] {1})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpSetLocal, new Definition("OpSetLocal", new int[] {1})),
                    new AbstractMap.SimpleEntry<Byte, Definition>(
                            OpCodes.OpGetBuiltin, new Definition("OpGetBuiltin", new int[] {1})));
}
