// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

import java.util.Collection;

public interface CustomObject {
    Collection<String> getType();

    int sizeOf();
}
