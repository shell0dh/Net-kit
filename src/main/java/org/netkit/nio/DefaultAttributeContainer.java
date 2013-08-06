package org.netkit.nio;

import java.util.Set;

/**
 * Date: 8/6/13
 * Time: 6:07 PM
 */
public final class DefaultAttributeContainer implements AttributeContainer{
    @Override
    public <T> T getAttribute(AttributeKey<T> key) {
        return null;
    }

    @Override
    public <T> T removeAttribute(AttributeKey<T> key) {
        return null;
    }

    @Override
    public <T> T setAttribute(AttributeKey<? extends T> key, T value) {
        return null;
    }

    @Override
    public Set<AttributeKey<?>> getAttributekeys() {
        return null;
    }
}
