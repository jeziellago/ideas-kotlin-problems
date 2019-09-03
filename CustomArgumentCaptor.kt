import org.mockito.Mockito
import org.mockito.internal.matchers.CapturingMatcher
import org.mockito.internal.util.Primitives


inline fun <reified T> argumentCaptor() = Captor.forClass(T::class.java)

class Captor<T> private constructor() {

    private val capturingMatcher = CapturingMatcher<T>()
    private var clazz: Class<T>? = null

    companion object {
        fun <T> forClass(clazz: Class<T>) = Captor<T>().apply {
            this.clazz = clazz
        }
    }

    fun capture(): T {
        Mockito.argThat(capturingMatcher)
        return Primitives.defaultValue(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    fun captureLambda(): T {
        Mockito.argThat(capturingMatcher)
        val cap: T.() -> Unit = {}
        return cap as T
    }
}
