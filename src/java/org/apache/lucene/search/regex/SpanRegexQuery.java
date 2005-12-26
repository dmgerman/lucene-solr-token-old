begin_unit
begin_package
DECL|package|org.apache.lucene.search.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|regex
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanOrQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|search
operator|.
name|spans
operator|.
name|Spans
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
name|util
operator|.
name|ToStringUtils
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_class
DECL|class|SpanRegexQuery
specifier|public
class|class
name|SpanRegexQuery
extends|extends
name|SpanQuery
block|{
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
DECL|method|SpanRegexQuery
specifier|public
name|SpanRegexQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|orig
init|=
operator|new
name|RegexQuery
argument_list|(
name|term
argument_list|)
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// RegexQuery (via MultiTermQuery).rewrite always returns a BooleanQuery
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|orig
decl_stmt|;
name|BooleanClause
index|[]
name|clauses
init|=
name|bq
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|sqs
init|=
operator|new
name|SpanQuery
index|[
name|clauses
operator|.
name|length
index|]
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|clause
init|=
name|clauses
index|[
name|i
index|]
decl_stmt|;
comment|// Clauses from RegexQuery.rewrite are always TermQuery's
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|sqs
index|[
name|i
index|]
operator|=
operator|new
name|SpanTermQuery
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
name|sqs
index|[
name|i
index|]
operator|.
name|setBoost
argument_list|(
name|tq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SpanOrQuery
name|query
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|sqs
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|orig
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Query should have been rewritten"
argument_list|)
throw|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|term
operator|.
name|field
argument_list|()
return|;
block|}
DECL|method|getTerms
specifier|public
name|Collection
name|getTerms
parameter_list|()
block|{
name|Collection
name|terms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|terms
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|TermQuery
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|SpanRegexQuery
name|that
init|=
operator|(
name|SpanRegexQuery
operator|)
name|o
decl_stmt|;
return|return
name|term
operator|.
name|equals
argument_list|(
name|that
operator|.
name|term
argument_list|)
operator|&&
name|getBoost
argument_list|()
operator|==
name|that
operator|.
name|getBoost
argument_list|()
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|term
operator|.
name|hashCode
argument_list|()
operator|^
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
literal|0x4BCEF3A9
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanRegexQuery("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
