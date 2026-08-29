/*
 * Decompiled with CFR 0.152.
 */
package melonslise.locks.common.util;

import java.util.function.Predicate;
import melonslise.locks.common.util.Lockable;

public final class LocksPredicates {
    public static final Predicate<Lockable> LOCKED = lockable -> lockable.lock.isLocked();
    public static final Predicate<Lockable> NOT_LOCKED = LOCKED.negate();

    private LocksPredicates() {
    }
}

