begin_unit
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package
begin_class
DECL|class|CombineSuggestion
specifier|public
class|class
name|CombineSuggestion
block|{
comment|/**    *<p>The indexes from the passed-in array of terms used to make this word combination</p>    */
DECL|field|originalTermIndexes
specifier|public
specifier|final
name|int
index|[]
name|originalTermIndexes
decl_stmt|;
comment|/**    *<p>The word combination suggestion</p>    */
DECL|field|suggestion
specifier|public
specifier|final
name|SuggestWord
name|suggestion
decl_stmt|;
DECL|method|CombineSuggestion
specifier|public
name|CombineSuggestion
parameter_list|(
name|SuggestWord
name|suggestion
parameter_list|,
name|int
index|[]
name|originalTermIndexes
parameter_list|)
block|{
name|this
operator|.
name|suggestion
operator|=
name|suggestion
expr_stmt|;
name|this
operator|.
name|originalTermIndexes
operator|=
name|originalTermIndexes
expr_stmt|;
block|}
block|}
end_class
end_unit
