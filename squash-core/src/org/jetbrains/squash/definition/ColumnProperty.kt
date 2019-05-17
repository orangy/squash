package org.jetbrains.squash.definition

interface ColumnProperty

fun <V, C : ColumnDefinition<V>> C.addProperty(property: ColumnProperty): C = apply {
    properties.add(property)
}

/**
 * Modifies this [Int] column to represent an automatically incrementing [Column]
 */
fun ColumnDefinition<Int>.autoIncrement(): ColumnDefinition<Int> = addProperty(AutoIncrementProperty)

/**
 * Modifies this [Long] column to represent an automatically incrementing [Column]
 */
@JvmName("autoIncrementLong")
fun ColumnDefinition<Long>.autoIncrement(): ColumnDefinition<Long> = addProperty(AutoIncrementProperty)

/**
 * Modifies this column to represent a nullable [Column]
 */
fun <V> ColumnDefinition<V>.nullable(): ColumnDefinition<V?> = addProperty(NullableProperty)
fun <V> ReferenceColumn<V>.nullable(): ReferenceColumn<V?> = addProperty(NullableProperty)

/**
 * Modifies this column to represent a [Column] with a default [value]
 */
fun <V, C : ColumnDefinition<V>> C.default(value: V): C = addProperty(DefaultValueProperty(value))

fun <V, C : ColumnDefinition<V>> C.default(generate: () -> V?):C = addProperty(DefaultValueProperty(null, generate))

object AutoIncrementProperty : ColumnProperty {
    override fun toString(): String = "++"
}

object NullableProperty : ColumnProperty {
    override fun toString(): String = "?"
}

class DefaultValueProperty<out V>(defaultValue: V?, private val generate:(() -> V)? = null) : ColumnProperty {

	val value:V? = defaultValue
		get():V? {
			return if (generate != null) {
				val result = generate.invoke()
				println("Generated Value 1: $result")
				result ?: field
			} else {
				field
			}
		}

	override fun toString(): String = "= $value"
}


