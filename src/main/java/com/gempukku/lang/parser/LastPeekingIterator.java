// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.parser;

import com.google.common.collect.PeekingIterator;

public class LastPeekingIterator<E> implements PeekingIterator<E> {
    private E last;
    private PeekingIterator<E> peekingIterator;

    public LastPeekingIterator(PeekingIterator<E> peekingIterator) {
        this.peekingIterator = peekingIterator;
    }

    @Override
    public boolean hasNext() {
        return peekingIterator.hasNext();
    }

    @Override
    public E next() {
        final E next = peekingIterator.next();
        last = next;
        return next;
    }

    @Override
    public E peek() {
        return peekingIterator.peek();
    }

    @Override
    public void remove() {
        peekingIterator.remove();
    }

    public E getLast() {
        return last;
    }
}
