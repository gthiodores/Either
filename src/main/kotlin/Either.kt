/**
 * A monad representing two possible data types.
 *
 * <L> is usually reserved for failure and <R> is for success
 */
sealed class Either<out L, out R> {
    /**
     *  A class representing the "Left" value, usually for error result
     */
    data class Left<L>(val value: L) : Either<L, Nothing>()

    /**
     *  A class representing the "Right" value, usually for success result
     */
    data class Right<R>(val value: R) : Either<Nothing, R>()

    /**
     * Take non-returning action against the monad
     */
    inline fun fold(
        onLeft: (L) -> Unit,
        onRight: (R) -> Unit,
    ) {
        return when (this) {
            is Left -> onLeft(this.value)
            is Right -> onRight(this.value)
        }
    }

    /**
     * Map the <R> value into a different value
     */
    inline fun <U> map(
        mapper: (R) -> U,
    ): Either<L, U> {
        return when (this) {
            is Right -> Right(mapper(value))
            is Left -> this
        }
    }

    /**
     * Map the <L> value into a different value
     */
    inline fun <U> mapLeft(
        mapper: (L) -> U,
    ): Either<U, R> {
        return when (this) {
            is Right -> this
            is Left -> Left(mapper(value))
        }
    }

    /**
     * Map both value into different values
     */
    inline fun <A, B> bimap(
        leftMapper: (L) -> A,
        rightMapper: (R) -> B
    ): Either<A, B> {
        return when (this) {
            is Left -> Left(leftMapper(value))
            is Right -> Right(rightMapper(value))
        }
    }

    /**
     * Map the current monad into a differently typed monad
     */
    @Suppress("Unchecked_Cast")
    inline fun <T, U> flatMap(
        mapper: (R) -> Either<T, U>
    ): Either<T, U> {
        return when (this) {
            is Left -> this as Either<T, U>
            is Right -> mapper(value)
        }
    }

    /**
     * Returns the <R> value if is right and null otherwise
     */
    fun takeRight(): R? {
        return when (this) {
            is Right -> value
            is Left -> null
        }
    }

    /**
     * Returns the <L> value if is left and null otherwise
     */
    fun takeLeft(): L? {
        return when (this) {
            is Right -> null
            is Left -> value
        }
    }

    /**
     * Returns the <R> value if right and matches predicate but null otherwise
     */
    inline fun findOrNull(
        predicate: (R) -> Boolean
    ): R? {
        return when (this) {
            is Left -> null
            is Right -> if (predicate(value)) value else null
        }
    }

    /**
     * Check if monad is <R> and value matches predicate
     */
    inline fun exists(
        predicate: (R) -> Boolean
    ): Boolean {
        return when (this) {
            is Left -> false
            is Right -> predicate(value)
        }
    }

    fun isLeft(): Boolean = this is Left

    fun isRight(): Boolean = this is Right

    override fun toString(): String {
        return when (this) {
            is Right -> value.toString()
            is Left -> value.toString()
        }
    }
}