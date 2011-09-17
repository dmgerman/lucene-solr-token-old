begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|Collector
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
name|Scorer
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
name|OpenBitSet
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
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|DocSetDelegateCollector
specifier|public
class|class
name|DocSetDelegateCollector
extends|extends
name|DocSetCollector
block|{
DECL|field|collector
specifier|final
name|Collector
name|collector
decl_stmt|;
DECL|method|DocSetDelegateCollector
specifier|public
name|DocSetDelegateCollector
parameter_list|(
name|int
name|smallSetSize
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|Collector
name|collector
parameter_list|)
block|{
name|super
argument_list|(
name|smallSetSize
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|+=
name|base
expr_stmt|;
comment|// optimistically collect the first docs in an array
comment|// in case the total number will be small enough to represent
comment|// as a small set like SortedIntDocSet instead...
comment|// Storing in this array will be quicker to convert
comment|// than scanning through a potentially huge bit vector.
comment|// FUTURE: when search methods all start returning docs in order, maybe
comment|// we could have a ListDocSet() and use the collected array directly.
if|if
condition|(
name|pos
operator|<
name|scratch
operator|.
name|length
condition|)
block|{
name|scratch
index|[
name|pos
index|]
operator|=
name|doc
expr_stmt|;
block|}
else|else
block|{
comment|// this conditional could be removed if BitSet was preallocated, but that
comment|// would take up more memory, and add more GC time...
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
name|bits
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|bits
operator|.
name|fastSet
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocSet
specifier|public
name|DocSet
name|getDocSet
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|<=
name|scratch
operator|.
name|length
condition|)
block|{
comment|// assumes docs were collected in sorted order!
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|scratch
argument_list|,
name|pos
argument_list|)
return|;
block|}
else|else
block|{
comment|// set the bits for ids that were collected in the array
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scratch
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|bits
operator|.
name|fastSet
argument_list|(
name|scratch
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
operator|new
name|BitDocSet
argument_list|(
name|bits
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
operator|.
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
block|}
end_class
end_unit
