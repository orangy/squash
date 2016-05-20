package org.jetbrains.squash.tests;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.squash.Transaction;
import org.jetbrains.squash.definition.Column;
import org.jetbrains.squash.definition.Columns;
import org.jetbrains.squash.definition.TableDefinition;
import org.jetbrains.squash.dialect.SQLStatement;
import org.jetbrains.squash.query.Queries;
import org.jetbrains.squash.results.Response;
import org.jetbrains.squash.statements.QueryStatement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public abstract class JavaTests implements DatabaseTests {

    public void assertSQL(@NotNull List<SQLStatement> $receiver, @NotNull Function0<String> text) {
        DefaultImpls.assertSQL(this, $receiver, text);
    }

    public void assertSQL(@NotNull List<SQLStatement> $receiver, @NotNull String text) {
        DefaultImpls.assertSQL(this, $receiver, text);

    }

    public void assertSQL(@NotNull SQLStatement $receiver, @NotNull Function0<String> text) {
        DefaultImpls.assertSQL(this, $receiver, text);

    }

    @Test
    public void simpleTableAndQuery() throws Exception {
        final TestTable testTable = new TestTable();
        withTables(new TableDefinition[]{testTable}, new Function1<Transaction, Unit>() {
            public Unit invoke(Transaction transaction) {
                QueryStatement q1 = Queries.query(testTable);
                QueryStatement q2 = Queries.<QueryStatement>select(q1, testTable.name);

                Response response = transaction.execute(q2);
                Assert.assertEquals("Eugene", SequencesKt.single(response).get(0));
                return Unit.INSTANCE;
            }
        });

    }

    private static class TestTable extends TableDefinition {
        Column<Integer> id = Columns.integer(this, "id");
        Column<String> name = Columns.varchar(this, "name", 50, null);
    }
}
