package com.gempukku.lang;

public interface ExecutionCostConfiguration {
    public int getGetContextValue();

    public int getSetContextValue();

    public int getGetReturnValue();

    public int getSetReturnValue();

    public int getBreakBlock();

    public int getDefineVariable();

    public int getSetVariable();

    public int getStackExecution();

    public int getStackGroupExecution();

    public int getSumValues();

    public int getOtherMathOperation();

    public int getCompareValues();

    public int getResolveMember();
}
