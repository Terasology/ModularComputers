// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

import java.util.Collection;

public interface CustomObject {
    Collection<String> getType();

    int sizeOf();
}
