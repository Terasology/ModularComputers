// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LangDefinition {
    private static final Set<String> RESERVED_WORDS = new HashSet<String>(
            Arrays.asList(
                    "for", "if", "while", "return", "break", "function", "var", "else", "this", "true", "false", "null"
            ));

    public static boolean isReservedWord(String word) {
        return RESERVED_WORDS.contains(word);
    }
}
