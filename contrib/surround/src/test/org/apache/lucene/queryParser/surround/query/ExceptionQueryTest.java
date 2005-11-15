begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|parser
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|parser
operator|.
name|QueryParser
import|;
end_import
begin_class
DECL|class|ExceptionQueryTest
specifier|public
class|class
name|ExceptionQueryTest
block|{
DECL|field|queryText
specifier|private
name|String
name|queryText
decl_stmt|;
DECL|field|verbose
specifier|private
name|boolean
name|verbose
decl_stmt|;
DECL|method|ExceptionQueryTest
specifier|public
name|ExceptionQueryTest
parameter_list|(
name|String
name|queryText
parameter_list|,
name|boolean
name|verbose
parameter_list|)
block|{
name|this
operator|.
name|queryText
operator|=
name|queryText
expr_stmt|;
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|(
name|StringBuffer
name|failQueries
parameter_list|)
block|{
name|boolean
name|pass
init|=
literal|false
decl_stmt|;
name|SrndQuery
name|lq
init|=
literal|null
decl_stmt|;
try|try
block|{
name|lq
operator|=
name|QueryParser
operator|.
name|parse
argument_list|(
name|queryText
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|queryText
operator|+
literal|"\nParsed as: "
operator|+
name|lq
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Parse exception for query:\n"
operator|+
name|queryText
operator|+
literal|"\n"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pass
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|pass
condition|)
block|{
name|failQueries
operator|.
name|append
argument_list|(
name|queryText
argument_list|)
expr_stmt|;
name|failQueries
operator|.
name|append
argument_list|(
literal|"\nParsed as: "
argument_list|)
expr_stmt|;
name|failQueries
operator|.
name|append
argument_list|(
name|lq
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|failQueries
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFailQueries
specifier|public
specifier|static
name|String
name|getFailQueries
parameter_list|(
name|String
index|[]
name|exceptionQueries
parameter_list|,
name|boolean
name|verbose
parameter_list|)
block|{
name|StringBuffer
name|failQueries
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exceptionQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
operator|new
name|ExceptionQueryTest
argument_list|(
name|exceptionQueries
index|[
name|i
index|]
argument_list|,
name|verbose
argument_list|)
operator|.
name|doTest
argument_list|(
name|failQueries
argument_list|)
expr_stmt|;
block|}
return|return
name|failQueries
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
