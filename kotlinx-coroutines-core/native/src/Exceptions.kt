/*
 * Copyright 2016-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.coroutines

import kotlinx.coroutines.internal.*
import kotlinx.coroutines.internal.SuppressSupportingThrowableImpl
import kotlinx.atomicfu.*
import kotlin.native.ref.*

/**
 * Thrown by cancellable suspending functions if the [Job] of the coroutine is cancelled while it is suspending.
 * It indicates _normal_ cancellation of a coroutine.
 * **It is not printed to console/log by default uncaught exception handler**.
 * (see [CoroutineExceptionHandler]).
 */
public actual typealias CancellationException = kotlin.coroutines.cancellation.CancellationException

/**
 * Thrown by cancellable suspending functions if the [Job] of the coroutine is cancelled or completed
 * without cause, or with a cause or exception that is not [CancellationException]
 * (see [Job.getCancellationException]).
 */
internal actual class JobCancellationException public actual constructor(
    message: String,
    cause: Throwable?,
    job: Job
) : CancellationException(message, cause) {
    private val ref = WeakReference(job)
    internal actual val job: Job?
        get() = ref.get()

    override fun toString(): String = "${super.toString()}; job=$job"
    override fun equals(other: Any?): Boolean =
        other === this ||
            other is JobCancellationException && other.message == message && other.job == job && other.cause == cause
    override fun hashCode(): Int =
        (message!!.hashCode() * 31 + job.hashCode()) * 31 + (cause?.hashCode() ?: 0)
}

internal actual fun Throwable.addSuppressedThrowable(other: Throwable) {
    if (this is SuppressSupportingThrowableImpl) addSuppressed(other)
}

// For use in tests
internal actual val RECOVER_STACK_TRACES: Boolean = false
