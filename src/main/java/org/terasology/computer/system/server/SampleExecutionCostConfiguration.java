/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.computer.system.server;

import com.gempukku.lang.ExecutionCostConfiguration;

public class SampleExecutionCostConfiguration implements ExecutionCostConfiguration {
    @Override
    public int getGetContextValue() {
        return 1;
    }

    @Override
    public int getSetContextValue() {
        return 1;
    }

    @Override
    public int getGetReturnValue() {
        return 1;
    }

    @Override
    public int getSetReturnValue() {
        return 1;
    }

    @Override
    public int getBreakBlock() {
        return 2;
    }

    @Override
    public int getDefineVariable() {
        return 2;
    }

    @Override
    public int getSetVariable() {
        return 2;
    }

    @Override
    public int getStackExecution() {
        return 5;
    }

    @Override
    public int getStackGroupExecution() {
        return 8;
    }

    @Override
    public int getSumValues() {
        return 10;
    }

    @Override
    public int getOtherMathOperation() {
        return 10;
    }

    @Override
    public int getCompareValues() {
        return 10;
    }

    @Override
    public int getResolveMember() {
        return 10;
    }
}

