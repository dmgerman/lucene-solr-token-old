begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|*
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
name|store
operator|.
name|Directory
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
name|*
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
name|TopDocs
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
name|BitSet
import|;
end_import
begin_comment
comment|/**  * Implements search over an IndexReader using the values of terms in  * a field as the primary sort order.  Secondary sort is by the order  * of documents in the index.  *  *<p>In this version (0.1) the field to sort by must contain strictly  * String representations of Integers (i.e. {@link Integer#toString Integer.toString()}).  *  * Each document is assumed to have a single term in the given field,  * and the value of the term is the document's relative position in  * the given sort order.  The field must be indexed, but should not be  * stored or tokenized:  *  *<p><code>document.add(new Field("byAlpha", Integer.toString(x), false, true, false));</code>  *  *<p>In other words, the desired order of documents must be encoded  * at the time they are entered into the index.  The first document  * should have a low value integer, the last document a high value  * (i.e. the documents should be numbered<code>1..n</code> where  *<code>1</code> is the first and<code>n</code> the last).  Values  * must be between<code>Integer.MIN_VALUE</code> and  *<code>Integer.MAX_VALUE</code> inclusive.  *  *<p>Then, at search time, the field is designated to be used to sort  * the returned hits:  *  *<p><code>IndexSearcher searcher = new IntegerSortedSearcher(indexReader, "byAlpha");</code>  *  *<p>or:  *  *<p><code>IntegerSortedSearcher searcher = new IntegerSortedSearcher(indexReader, "bySomething");  *<br>Hits hits = searcher.search(query, filter);  *<br>...  *<br>searcher.setOrderByField("bySomethingElse");  *<br>hits = searcher.search(query, filter);  *<br>...  *</code>  *  *<p>Note the above example shows that one of these objects can be  * used multiple times, and the sort order changed between usages.  *  *<p><h3>Memory Usage</h3>  *  *<p>This object is almost identical to the regular IndexSearcher and  * makes no additional memory requirements on its own.  Every time the  *<code>search()</code> method is called, however, a new  * {@link FieldSortedHitQueue FieldSortedHitQueue} object is created.  * That object is responsible for putting the hits in the correct order,  * and it maintains a cache of information based on the IndexReader  * given to it.  See its documentation for more information on its  * memory usage.  *  *<p><h3>Concurrency</h3>  *  *<p>This object has the same behavior during concurrent updates to  * the index as does IndexSearcher.  Namely, in the default  * implementation using  * {@link org.apache.lucene.store.FSDirectory FSDirectory}, the index  * can be updated (deletes, adds) without harm while this object  * exists, but this object will not see the changes.  Ultimately this  * behavior is a result of the  * {@link org.apache.lucene.index.SegmentReader SegmentReader} class  * internal to FSDirectory, which caches information about documents  * in memory.  *  *<p>So, in order for IntegerSortedSearcher to be kept up to date with  * changes to the index, new instances must be created instead of the  * same one used over and over again.  This will result in lower  * performance than if instances are reused.  *  *<p><h3>Updates</h3>  *  *<p>In order to be able to update the index without having to  * recalculate all the sort numbers, the numbers should be stored with  * "space" between them.  That is, sort the documents and number them  *<code>1..n</code>.  Then, as<code>i</code> goes between  *<code>1</code> and<code>n</code>:  *  *<p><code>document.add(new Field("byAlpha", Integer.toString(i*1000), false, true, false));</code>  *  *<p>Add a new document sorted between position 1 and 2 by:  *  *<p><code>document.add(new Field("byAlpha", Integer.toString(1500), false, true, false));</code>  *  *<p>Be careful not to overun<code>Integer.MAX_VALUE</code>  * (<code>2147483647</code>).  Periodically a complete reindex should  * be run so the sort orders can be "normalized".  *  *<p>Created: Dec 8, 2003 12:47:26 PM  *  * @author  "Tim Jones"&lt;tjluc@nacimiento.com&gt;  * @since   lucene 1.3  * @version 0.1  * @see IndexSearcher  */
end_comment
begin_class
DECL|class|IntegerSortedSearcher
specifier|public
class|class
name|IntegerSortedSearcher
extends|extends
name|IndexSearcher
block|{
comment|/** stores the field being used to sort by **/
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
comment|/**      * Searches the index in the named directory using the given      * field as the primary sort.      * The terms in the field must contain strictly integers in      * the range<code>Integer.MIN_VALUE</code> and<code>Integer.MAX_VALUE</code> inclusive.      * @see IndexSearcher(java.lang.String,java.lang.String)      */
DECL|method|IntegerSortedSearcher
specifier|public
name|IntegerSortedSearcher
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|integer_field
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|)
argument_list|,
name|integer_field
argument_list|)
expr_stmt|;
block|}
comment|/**      * Searches the index in the provided directory using the      * given field as the primary sort.      * The terms in the field must contain strictly integers in      * the range<code>Integer.MIN_VALUE</code> and<code>Integer.MAX_VALUE</code> inclusive.      * @see IndexSearcher(Directory,java.lang.String)      */
DECL|method|IntegerSortedSearcher
specifier|public
name|IntegerSortedSearcher
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|integer_field
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
argument_list|,
name|integer_field
argument_list|)
expr_stmt|;
block|}
comment|/**      * Searches the provided index using the given field as the      * primary sort.      * The terms in the field must contain strictly integers in      * the range<code>Integer.MIN_VALUE</code> and<code>Integer.MAX_VALUE</code> inclusive.      * @see IndexSearcher(IndexReader)      */
DECL|method|IntegerSortedSearcher
specifier|public
name|IntegerSortedSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|integer_field
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|integer_field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the field to order results by.  This can be called      * multiple times per instance of IntegerSortedSearcher.      * @param integer_field  The field to sort results by.      */
DECL|method|setOrderByField
specifier|public
name|void
name|setOrderByField
parameter_list|(
name|String
name|integer_field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|integer_field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the name of the field currently being used      * to sort results by.      * @return  Field name.      */
DECL|method|getOrderByField
specifier|public
name|String
name|getOrderByField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/**      * Finds the top<code>nDocs</code>      * hits for<code>query</code>, applying<code>filter</code> if non-null.      *      * Overrides IndexSearcher.search to use a FieldSortedHitQueue instead of the      * default HitQueue.      *      * @see IndexSearcher#search      */
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|scorer
init|=
name|query
operator|.
name|weight
argument_list|(
name|this
argument_list|)
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|TopDocs
argument_list|(
literal|0
argument_list|,
operator|new
name|ScoreDoc
index|[
literal|0
index|]
argument_list|)
return|;
block|}
specifier|final
name|BitSet
name|bits
init|=
name|filter
operator|!=
literal|null
condition|?
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|FieldSortedHitQueue
name|hq
init|=
operator|new
name|FieldSortedHitQueue
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|nDocs
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|totalHits
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|scorer
operator|.
name|score
argument_list|(
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|score
operator|>
literal|0.0f
operator|&&
comment|// ignore zeroed buckets
operator|(
name|bits
operator|==
literal|null
operator|||
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
condition|)
block|{
comment|// skip docs not in bits
name|totalHits
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|hq
operator|.
name|insert
argument_list|(
operator|new
name|ScoreDoc
argument_list|(
name|doc
argument_list|,
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|hq
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|hq
operator|.
name|size
argument_list|()
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
comment|// put docs in array
name|scoreDocs
index|[
name|i
index|]
operator|=
operator|(
name|ScoreDoc
operator|)
name|hq
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
index|[
literal|0
index|]
argument_list|,
name|scoreDocs
argument_list|)
return|;
block|}
block|}
end_class
end_unit
