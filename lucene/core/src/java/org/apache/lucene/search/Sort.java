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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Encapsulates sort criteria for returned hits.  *  *<p>The fields used to determine sort order must be carefully chosen.  * Documents must contain a single term in such a field,  * and the value of the term should indicate the document's relative position in  * a given sort order.  The field must be indexed, but should not be tokenized,  * and does not need to be stored (unless you happen to want it back with the  * rest of your document data).  In other words:  *  *<p><code>document.add (new Field ("byNumber", Integer.toString(x), Field.Store.NO, Field.Index.NOT_ANALYZED));</code></p>  *   *  *<p><h3>Valid Types of Values</h3>  *  *<p>There are four possible kinds of term values which may be put into  * sorting fields: Integers, Longs, Floats, or Strings.  Unless  * {@link SortField SortField} objects are specified, the type of value  * in the field is determined by parsing the first term in the field.  *  *<p>Integer term values should contain only digits and an optional  * preceding negative sign.  Values must be base 10 and in the range  *<code>Integer.MIN_VALUE</code> and<code>Integer.MAX_VALUE</code> inclusive.  * Documents which should appear first in the sort  * should have low value integers, later documents high values  * (i.e. the documents should be numbered<code>1..n</code> where  *<code>1</code> is the first and<code>n</code> the last).  *  *<p>Long term values should contain only digits and an optional  * preceding negative sign.  Values must be base 10 and in the range  *<code>Long.MIN_VALUE</code> and<code>Long.MAX_VALUE</code> inclusive.  * Documents which should appear first in the sort  * should have low value integers, later documents high values.  *   *<p>Float term values should conform to values accepted by  * {@link Float Float.valueOf(String)} (except that<code>NaN</code>  * and<code>Infinity</code> are not supported).  * Documents which should appear first in the sort  * should have low values, later documents high values.  *  *<p>String term values can contain any valid String, but should  * not be tokenized.  The values are sorted according to their  * {@link Comparable natural order}.  Note that using this type  * of term value has higher memory requirements than the other  * two types.  *  *<p><h3>Object Reuse</h3>  *  *<p>One of these objects can be  * used multiple times and the sort order changed between usages.  *  *<p>This class is thread safe.  *  *<p><h3>Memory Usage</h3>  *  *<p>Sorting uses of caches of term values maintained by the  * internal HitQueue(s).  The cache is static and contains an integer  * or float array of length<code>IndexReader.maxDoc()</code> for each field  * name for which a sort is performed.  In other words, the size of the  * cache in bytes is:  *  *<p><code>4 * IndexReader.maxDoc() * (# of different fields actually used to sort)</code>  *  *<p>For String fields, the cache is larger: in addition to the  * above array, the value of every term in the field is kept in memory.  * If there are many unique terms in the field, this could  * be quite large.  *  *<p>Note that the size of the cache is not affected by how many  * fields are in the index and<i>might</i> be used to sort - only by  * the ones actually used to sort a result set.  *  *<p>Created: Feb 12, 2004 10:53:57 AM  *  * @since   lucene 1.4  */
end_comment
begin_class
DECL|class|Sort
specifier|public
class|class
name|Sort
block|{
comment|/**    * Represents sorting by computed relevance. Using this sort criteria returns    * the same results as calling    * {@link IndexSearcher#search(Query,int) IndexSearcher#search()}without a sort criteria,    * only with slightly more overhead.    */
DECL|field|RELEVANCE
specifier|public
specifier|static
specifier|final
name|Sort
name|RELEVANCE
init|=
operator|new
name|Sort
argument_list|()
decl_stmt|;
comment|/** Represents sorting by index order. */
DECL|field|INDEXORDER
specifier|public
specifier|static
specifier|final
name|Sort
name|INDEXORDER
init|=
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
decl_stmt|;
comment|// internal representation of the sort criteria
DECL|field|fields
name|SortField
index|[]
name|fields
decl_stmt|;
comment|/**    * Sorts by computed relevance. This is the same sort criteria as calling    * {@link IndexSearcher#search(Query,int) IndexSearcher#search()}without a sort criteria,    * only with slightly more overhead.    */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|()
block|{
name|this
argument_list|(
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
expr_stmt|;
block|}
comment|/** Sorts by the criteria in the given SortField. */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|(
name|SortField
name|field
parameter_list|)
block|{
name|setSort
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
comment|/** Sorts in succession by the criteria in each SortField. */
DECL|method|Sort
specifier|public
name|Sort
parameter_list|(
name|SortField
modifier|...
name|fields
parameter_list|)
block|{
name|setSort
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the sort to the given criteria. */
DECL|method|setSort
specifier|public
name|void
name|setSort
parameter_list|(
name|SortField
name|field
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
operator|new
name|SortField
index|[]
block|{
name|field
block|}
expr_stmt|;
block|}
comment|/** Sets the sort to the given criteria in succession. */
DECL|method|setSort
specifier|public
name|void
name|setSort
parameter_list|(
name|SortField
modifier|...
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
comment|/**    * Representation of the sort criteria.    * @return Array of SortField objects used in this sort criteria    */
DECL|method|getSort
specifier|public
name|SortField
index|[]
name|getSort
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/**    * Rewrites the SortFields in this Sort, returning a new Sort if any of the fields    * changes during their rewriting.    *    * @param searcher IndexSearcher to use in the rewriting    * @return {@code this} if the Sort/Fields have not changed, or a new Sort if there    *        is a change    * @throws IOException Can be thrown by the rewriting    * @lucene.experimental    */
DECL|method|rewrite
specifier|public
name|Sort
name|rewrite
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|SortField
index|[]
name|rewrittenSortFields
init|=
operator|new
name|SortField
index|[
name|fields
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|rewrittenSortFields
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|rewrite
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
index|[
name|i
index|]
operator|!=
name|rewrittenSortFields
index|[
name|i
index|]
condition|)
block|{
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
operator|(
name|changed
operator|)
condition|?
operator|new
name|Sort
argument_list|(
name|rewrittenSortFields
argument_list|)
else|:
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|<
name|fields
operator|.
name|length
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
annotation|@
name|Override
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
name|Sort
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|Sort
name|other
init|=
operator|(
name|Sort
operator|)
name|o
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|fields
argument_list|,
name|other
operator|.
name|fields
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|0x45aaf665
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|fields
argument_list|)
return|;
block|}
block|}
end_class
end_unit
