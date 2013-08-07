package org.netkit.nio;

import java.util.Set;

/**
 * Date: 8/6/13
 * Time: 5:09 PM
 */
public interface AttributeContainer {
    <T> T getAttribute(AttributeKey<T> key);

    <T> T removeAttribute(AttributeKey<T> key);

    <T> T setAttribute(AttributeKey<? extends T> key,T value);

    Set<AttributeKey<?>> getAttributekeys();

    public <T> T getAttribute(AttributeKey<T> key, T defaultValue);

}
