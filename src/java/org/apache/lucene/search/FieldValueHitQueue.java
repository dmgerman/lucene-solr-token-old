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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * Expert: A hit queue for sorting by hits by terms in more than one field.  * Uses<code>FieldCache.DEFAULT</code> for maintaining  * internal term lookup tables.  *   * This class will not resolve SortField.AUTO types, and expects the type  * of all SortFields used for construction to already have been resolved.   * {@link SortField#detectFieldType(IndexReader, String)} is a utility method which  * may be used for field type detection.  *  *<b>NOTE:</b> This API is experimental and might change in  * incompatible ways in the next release.  *  * @since 2.9  * @version $Id:  * @see Searcher#search(Query,Filter,int,Sort)  * @see FieldCache  */
end_comment
begin_class
DECL|class|FieldValueHitQueue
specifier|public
specifier|abstract
class|class
name|FieldValueHitQueue
extends|extends
name|PriorityQueue
block|{
DECL|class|Entry
specifier|final
specifier|static
class|class
name|Entry
block|{
DECL|field|slot
name|int
name|slot
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|method|Entry
name|Entry
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|docID
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|this
operator|.
name|slot
operator|=
name|slot
expr_stmt|;
name|this
operator|.
name|docID
operator|=
name|docID
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"slot:"
operator|+
name|slot
operator|+
literal|" docID:"
operator|+
name|docID
operator|+
literal|" score="
operator|+
name|score
return|;
block|}
block|}
comment|/**    * An implementation of {@link FieldValueHitQueue} which is optimized in case    * there is just one comparator.    */
DECL|class|OneComparatorFieldValueHitQueue
specifier|private
specifier|static
specifier|final
class|class
name|OneComparatorFieldValueHitQueue
extends|extends
name|FieldValueHitQueue
block|{
DECL|field|comparator
specifier|private
specifier|final
name|FieldComparator
name|comparator
decl_stmt|;
DECL|field|oneReverseMul
specifier|private
specifier|final
name|int
name|oneReverseMul
decl_stmt|;
DECL|method|OneComparatorFieldValueHitQueue
specifier|public
name|OneComparatorFieldValueHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fields
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Sort must contain at least one field"
argument_list|)
throw|;
block|}
name|SortField
name|field
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
comment|// AUTO is resolved before we are called
assert|assert
name|field
operator|.
name|getType
argument_list|()
operator|!=
name|SortField
operator|.
name|AUTO
assert|;
name|comparator
operator|=
name|field
operator|.
name|getComparator
argument_list|(
name|size
argument_list|,
literal|0
argument_list|,
name|field
operator|.
name|reverse
argument_list|)
expr_stmt|;
name|oneReverseMul
operator|=
name|field
operator|.
name|reverse
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
name|comparators
index|[
literal|0
index|]
operator|=
name|comparator
expr_stmt|;
name|reverseMul
index|[
literal|0
index|]
operator|=
name|oneReverseMul
expr_stmt|;
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns whether<code>a</code> is less relevant than<code>b</code>.      * @param a ScoreDoc      * @param b ScoreDoc      * @return<code>true</code> if document<code>a</code> should be sorted after document<code>b</code>.      */
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|Object
name|a
parameter_list|,
specifier|final
name|Object
name|b
parameter_list|)
block|{
specifier|final
name|Entry
name|hitA
init|=
operator|(
name|Entry
operator|)
name|a
decl_stmt|;
specifier|final
name|Entry
name|hitB
init|=
operator|(
name|Entry
operator|)
name|b
decl_stmt|;
assert|assert
name|hitA
operator|!=
name|hitB
assert|;
assert|assert
name|hitA
operator|.
name|slot
operator|!=
name|hitB
operator|.
name|slot
assert|;
specifier|final
name|int
name|c
init|=
name|oneReverseMul
operator|*
name|comparator
operator|.
name|compare
argument_list|(
name|hitA
operator|.
name|slot
argument_list|,
name|hitB
operator|.
name|slot
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
operator|>
literal|0
return|;
block|}
comment|// avoid random sort order that could lead to duplicates (bug #31241):
return|return
name|hitA
operator|.
name|docID
operator|>
name|hitB
operator|.
name|docID
return|;
block|}
block|}
comment|/**    * An implementation of {@link FieldValueHitQueue} which is optimized in case    * there is more than one comparator.    */
DECL|class|MultiComparatorsFieldValueHitQueue
specifier|private
specifier|static
specifier|final
class|class
name|MultiComparatorsFieldValueHitQueue
extends|extends
name|FieldValueHitQueue
block|{
DECL|method|MultiComparatorsFieldValueHitQueue
specifier|public
name|MultiComparatorsFieldValueHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|int
name|numComparators
init|=
name|comparators
operator|.
name|length
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
name|numComparators
condition|;
operator|++
name|i
control|)
block|{
name|SortField
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
comment|// AUTO is resolved before we are called
assert|assert
name|field
operator|.
name|getType
argument_list|()
operator|!=
name|SortField
operator|.
name|AUTO
assert|;
name|reverseMul
index|[
name|i
index|]
operator|=
name|field
operator|.
name|reverse
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|field
operator|.
name|getComparator
argument_list|(
name|size
argument_list|,
name|i
argument_list|,
name|field
operator|.
name|reverse
argument_list|)
expr_stmt|;
block|}
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|Object
name|a
parameter_list|,
specifier|final
name|Object
name|b
parameter_list|)
block|{
specifier|final
name|Entry
name|hitA
init|=
operator|(
name|Entry
operator|)
name|a
decl_stmt|;
specifier|final
name|Entry
name|hitB
init|=
operator|(
name|Entry
operator|)
name|b
decl_stmt|;
assert|assert
name|hitA
operator|!=
name|hitB
assert|;
assert|assert
name|hitA
operator|.
name|slot
operator|!=
name|hitB
operator|.
name|slot
assert|;
name|int
name|numComparators
init|=
name|comparators
operator|.
name|length
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
name|numComparators
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reverseMul
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
index|]
operator|.
name|compare
argument_list|(
name|hitA
operator|.
name|slot
argument_list|,
name|hitB
operator|.
name|slot
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
comment|// Short circuit
return|return
name|c
operator|>
literal|0
return|;
block|}
block|}
comment|// avoid random sort order that could lead to duplicates (bug #31241):
return|return
name|hitA
operator|.
name|docID
operator|>
name|hitB
operator|.
name|docID
return|;
block|}
block|}
comment|// prevent instantiation and extension.
DECL|method|FieldValueHitQueue
specifier|private
name|FieldValueHitQueue
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|)
block|{
comment|// When we get here, fields.length is guaranteed to be> 0, therefore no
comment|// need to check it again.
comment|// All these are required by this class's API - need to return arrays.
comment|// Therefore even in the case of a single comparator, create an array
comment|// anyway.
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|int
name|numComparators
init|=
name|fields
operator|.
name|length
decl_stmt|;
name|comparators
operator|=
operator|new
name|FieldComparator
index|[
name|numComparators
index|]
expr_stmt|;
name|reverseMul
operator|=
operator|new
name|int
index|[
name|numComparators
index|]
expr_stmt|;
block|}
comment|/**    * Creates a hit queue sorted by the given list of fields.    *     *<p><b>NOTE</b>: The instances returned by this method    * pre-allocate a full array of length<code>numHits</code>.    *     * @param fields    *          SortField array we are sorting by in priority order (highest    *          priority first); cannot be<code>null</code> or empty    * @param size    *          The number of hits to retain. Must be greater than zero.    * @throws IOException    */
DECL|method|create
specifier|public
specifier|static
name|FieldValueHitQueue
name|create
parameter_list|(
name|SortField
index|[]
name|fields
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Sort must contain at least one field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|OneComparatorFieldValueHitQueue
argument_list|(
name|fields
argument_list|,
name|size
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MultiComparatorsFieldValueHitQueue
argument_list|(
name|fields
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
DECL|method|getComparators
name|FieldComparator
index|[]
name|getComparators
parameter_list|()
block|{
return|return
name|comparators
return|;
block|}
DECL|method|getReverseMul
name|int
index|[]
name|getReverseMul
parameter_list|()
block|{
return|return
name|reverseMul
return|;
block|}
comment|/** Stores the sort criteria being used. */
DECL|field|fields
specifier|protected
specifier|final
name|SortField
index|[]
name|fields
decl_stmt|;
DECL|field|comparators
specifier|protected
specifier|final
name|FieldComparator
index|[]
name|comparators
decl_stmt|;
DECL|field|reverseMul
specifier|protected
specifier|final
name|int
index|[]
name|reverseMul
decl_stmt|;
DECL|method|lessThan
specifier|protected
specifier|abstract
name|boolean
name|lessThan
parameter_list|(
specifier|final
name|Object
name|a
parameter_list|,
specifier|final
name|Object
name|b
parameter_list|)
function_decl|;
comment|/**    * Given a FieldDoc object, stores the values used to sort the given document.    * These values are not the raw values out of the index, but the internal    * representation of them. This is so the given search hit can be collated by    * a MultiSearcher with other search hits.    *     * @param doc    *          The FieldDoc to store sort values into.    * @return The same FieldDoc passed in.    * @see Searchable#search(Weight,Filter,int,Sort)    */
DECL|method|fillFields
name|FieldDoc
name|fillFields
parameter_list|(
specifier|final
name|Entry
name|entry
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|comparators
operator|.
name|length
decl_stmt|;
specifier|final
name|Comparable
index|[]
name|fields
init|=
operator|new
name|Comparable
index|[
name|n
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
name|n
condition|;
operator|++
name|i
control|)
block|{
name|fields
index|[
name|i
index|]
operator|=
name|comparators
index|[
name|i
index|]
operator|.
name|value
argument_list|(
name|entry
operator|.
name|slot
argument_list|)
expr_stmt|;
block|}
comment|//if (maxscore> 1.0f) doc.score /= maxscore;   // normalize scores
return|return
operator|new
name|FieldDoc
argument_list|(
name|entry
operator|.
name|docID
argument_list|,
name|entry
operator|.
name|score
argument_list|,
name|fields
argument_list|)
return|;
block|}
comment|/** Returns the SortFields being used by this hit queue. */
DECL|method|getFields
name|SortField
index|[]
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
block|}
end_class
end_unit
