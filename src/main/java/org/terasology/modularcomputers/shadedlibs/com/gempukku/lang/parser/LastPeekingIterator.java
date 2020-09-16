// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.parser;

import com.google.common.collect.PeekingIterator;

public class LastPeekingIterator<E> implements PeekingIterator<E> {
    private E _last;
    private final PeekingIterator<E> _peekingIterator;

    public LastPeekingIterator(PeekingIterator<E> peekingIterator) {
        _peekingIterator = peekingIterator;
    }

    @Override
    public boolean hasNext() {
        return _peekingIterator.hasNext();
    }

    @Override
    public E next() {
        final E next = _peekingIterator.next();
        _last = next;
        return next;
    }

    @Override
    public E peek() {
        return _peekingIterator.peek();
    }

    @Override
    public void remove() {
        _peekingIterator.remove();
    }

    public E getLast() {
        return _last;
    }
}
