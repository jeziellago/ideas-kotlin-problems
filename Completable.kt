class Completable<S, F> {

    lateinit var success: (S) -> Unit
    lateinit var failure: (F) -> Unit
    lateinit var callable: () -> DisposableTask

    var mapper: ((S) -> Any)? = null
    var nextCompletable: Completable<Any, F>? = null
    var headCompletable: Completable<Any, F>? = null
    var result: Result<S, F>? = null
        set(value) {
            field = value
            value?.let {
                flowResult(it)
            }
        }

    fun flow(success: (S) -> Unit, failure: (F) -> Unit) = apply {
        this.success = success
        this.failure = failure
    }

    fun complete(): DisposableTask {
        return headCompletable?.callable?.invoke()
                ?: callable.invoke()
    }

    private fun flowResult(result: Result<S, F>) {
        nextCompletable?.run { flowMap(result, this) }
                ?: headCompletable?.run { flowMap(result, this) }
                ?: result.flow({ success(it) }, { failure(it) })
    }

    @Suppress("UNCHECKED_CAST")
    private fun flowMap(result: Result<S, F>, completable: Completable<Any, F>) = completable
            .mapper?.run { completable.result = result.mapSuccess(this as (S) -> Any) }


}

@Suppress("UNCHECKED_CAST")
fun <S, F, T> Completable<S, F>.map(transform: (S) -> T): Completable<T, F> {
    val headCompletable = headCompletable ?: this
    val nextCompletable = Completable<T, F>().also {
        it.headCompletable = headCompletable as Completable<Any, F>
        it.mapper = transform as ((T) -> Any)?
    }
    this.nextCompletable = nextCompletable as Completable<Any, F>
    return nextCompletable
}
