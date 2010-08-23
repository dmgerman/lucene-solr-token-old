begin_unit
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|spell
operator|.
name|Dictionary
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
name|PriorityQueue
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|TermFreqIterator
import|;
end_import
begin_class
DECL|class|Lookup
specifier|public
specifier|abstract
class|class
name|Lookup
block|{
comment|/**    * Result of a lookup.    */
DECL|class|LookupResult
specifier|public
specifier|static
specifier|final
class|class
name|LookupResult
block|{
DECL|field|key
name|String
name|key
decl_stmt|;
DECL|field|value
name|float
name|value
decl_stmt|;
DECL|method|LookupResult
specifier|public
name|LookupResult
parameter_list|(
name|String
name|key
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|key
operator|+
literal|"/"
operator|+
name|value
return|;
block|}
block|}
DECL|class|LookupPriorityQueue
specifier|public
specifier|static
specifier|final
class|class
name|LookupPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|LookupResult
argument_list|>
block|{
DECL|method|LookupPriorityQueue
specifier|public
name|LookupPriorityQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|LookupResult
name|a
parameter_list|,
name|LookupResult
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|value
operator|<
name|b
operator|.
name|value
return|;
block|}
DECL|method|getResults
specifier|public
name|LookupResult
index|[]
name|getResults
parameter_list|()
block|{
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
name|LookupResult
index|[]
name|res
init|=
operator|new
name|LookupResult
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
comment|/** Initialize the lookup. */
DECL|method|init
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
function_decl|;
comment|/** Build lookup from a dictionary. Some implementations may require sorted    * or unsorted keys from the dictionary's iterator - use    * {@link SortedTermFreqIteratorWrapper} or    * {@link UnsortedTermFreqIteratorWrapper} in such case.    */
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|Dictionary
name|dict
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|dict
operator|.
name|getWordsIterator
argument_list|()
decl_stmt|;
name|TermFreqIterator
name|tfit
decl_stmt|;
if|if
condition|(
name|it
operator|instanceof
name|TermFreqIterator
condition|)
block|{
name|tfit
operator|=
operator|(
name|TermFreqIterator
operator|)
name|it
expr_stmt|;
block|}
else|else
block|{
name|tfit
operator|=
operator|new
name|TermFreqIterator
operator|.
name|TermFreqIteratorWrapper
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
name|build
argument_list|(
name|tfit
argument_list|)
expr_stmt|;
block|}
DECL|method|build
specifier|protected
specifier|abstract
name|void
name|build
parameter_list|(
name|TermFreqIterator
name|tfit
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Persist the constructed lookup data to a directory. Optional operation.    * @param storeDir directory where data can be stored.    * @return true if successful, false if unsuccessful or not supported.    * @throws IOException when fatal IO error occurs.    */
DECL|method|store
specifier|public
specifier|abstract
name|boolean
name|store
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Discard current lookup data and load it from a previously saved copy.    * Optional operation.    * @param storeDir directory where lookup data was stored.    * @return true if completed successfully, false if unsuccessful or not supported.    * @throws IOException when fatal IO error occurs.    */
DECL|method|load
specifier|public
specifier|abstract
name|boolean
name|load
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Look up a key and return possible completion for this key.    * @param key lookup key. Depending on the implementation this may be    * a prefix, misspelling, or even infix.    * @param onlyMorePopular return only more popular results    * @param num maximum number of results to return    * @return a list of possible completions, with their relative weight (e.g. popularity)    */
DECL|method|lookup
specifier|public
specifier|abstract
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
function_decl|;
comment|/**    * Modify the lookup data by recording additional data. Optional operation.    * @param key new lookup key    * @param value value to associate with this key    * @return true if new key is added, false if it already exists or operation    * is not supported.    */
DECL|method|add
specifier|public
specifier|abstract
name|boolean
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**    * Get value associated with a specific key.    * @param key lookup key    * @return associated value    */
DECL|method|get
specifier|public
specifier|abstract
name|Object
name|get
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_class
end_unit
