package org.jetbrains.squash.expressions

class CaseExpression<T>(val target:Expression<*>? = null) : Expression<T> {
	val clauses = mutableListOf<WhenThenClause<T>>()
	var finalClause:Expression<T>? = null

	fun whenClause(expression:Expression<*>):WhenThenClause<T> = WhenThenClause(this, expression).apply {
		this@CaseExpression.clauses.add(this)
	}

	fun elseClause(expression:Expression<T>):CaseExpression<T> = this.apply {
		finalClause = expression
	}

	class WhenThenClause<T>(
			val caseExpression:CaseExpression<T>,
			val whenClause:Expression<*>
	) {
		lateinit var thenClause:Expression<T>
		
		fun thenClause(expression:Expression<T>):CaseExpression<T> {
			thenClause = expression
			return caseExpression
		}
	}
}

inline fun <T> case(target:Expression<*>? = null, clauses:CaseExpression<T>.() -> Unit) = CaseExpression<T>(target).apply(clauses)
