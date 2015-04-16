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
name|AbstractList
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
name|Objects
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
name|DocValues
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
name|index
operator|.
name|LeafReaderContext
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
name|SortedSetDocValues
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
name|ArrayUtil
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
name|Bits
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
name|BytesRef
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
name|FixedBitSet
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
name|LongBitSet
import|;
end_import
begin_comment
comment|/**  * A {@link Query} that only accepts documents whose  * term value in the specified field is contained in the  * provided set of allowed terms.  *  *<p>  * This is the same functionality as TermsQuery (from  * queries/), but because of drastically different  * implementations, they also have different performance  * characteristics, as described below.  *  *<p>  * With each search, this query translates the specified  * set of Terms into a private {@link LongBitSet} keyed by  * term number per unique {@link IndexReader} (normally one  * reader per segment).  Then, during matching, the term  * number for each docID is retrieved from the cache and  * then checked for inclusion using the {@link LongBitSet}.  * Since all testing is done using RAM resident data  * structures, performance should be very fast, most likely  * fast enough to not require further caching of the  * DocIdSet for each possible combination of terms.  * However, because docIDs are simply scanned linearly, an  * index with a great many small documents may find this  * linear scan too costly.  *  *<p>  * In contrast, TermsQuery builds up an {@link FixedBitSet},  * keyed by docID, every time it's created, by enumerating  * through all matching docs using {@link org.apache.lucene.index.PostingsEnum} to seek  * and scan through each term's docID list.  While there is  * no linear scan of all docIDs, besides the allocation of  * the underlying array in the {@link FixedBitSet}, this  * approach requires a number of "disk seeks" in proportion  * to the number of terms, which can be exceptionally costly  * when there are cache misses in the OS's IO cache.  *  *<p>  * Generally, this filter will be slower on the first  * invocation for a given field, but subsequent invocations,  * even if you change the allowed set of Terms, should be  * faster than TermsQuery, especially as the number of  * Terms being matched increases.  If you are matching only  * a very small number of terms, and those terms in turn  * match a very small number of documents, TermsQuery may  * perform faster.  *  *<p>  * Which query is best is very application dependent.  */
end_comment
begin_class
DECL|class|DocValuesTermsQuery
specifier|public
class|class
name|DocValuesTermsQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|private
specifier|final
name|BytesRef
index|[]
name|terms
decl_stmt|;
DECL|method|DocValuesTermsQuery
specifier|public
name|DocValuesTermsQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Collection
argument_list|<
name|BytesRef
argument_list|>
name|terms
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|terms
argument_list|,
literal|"Collection of terms must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|this
operator|.
name|terms
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DocValuesTermsQuery
specifier|public
name|DocValuesTermsQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
modifier|...
name|terms
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|DocValuesTermsQuery
specifier|public
name|DocValuesTermsQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
modifier|...
name|terms
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
operator|new
name|AbstractList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|terms
index|[
name|index
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|terms
operator|.
name|length
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|DocValuesTermsQuery
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DocValuesTermsQuery
name|that
init|=
operator|(
name|DocValuesTermsQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|terms
argument_list|,
name|that
operator|.
name|terms
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|field
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|terms
argument_list|)
argument_list|,
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|defaultField
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|": ["
argument_list|)
expr_stmt|;
for|for
control|(
name|BytesRef
name|term
range|:
name|terms
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|term
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|float
name|score
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedSetDocValues
name|values
init|=
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|LongBitSet
name|bits
init|=
operator|new
name|LongBitSet
argument_list|(
name|values
operator|.
name|getValueCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
range|:
name|terms
control|)
block|{
specifier|final
name|long
name|ord
init|=
name|values
operator|.
name|lookupTerm
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|DocIdSetIterator
name|approximation
init|=
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TwoPhaseIterator
name|twoPhaseIterator
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|doc
init|=
name|approximation
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|acceptDocs
operator|!=
literal|null
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|ord
init|=
name|values
operator|.
name|nextOrd
argument_list|()
init|;
name|ord
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|;
name|ord
operator|=
name|values
operator|.
name|nextOrd
argument_list|()
control|)
block|{
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
name|ord
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|)
decl_stmt|;
return|return
operator|new
name|Scorer
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhaseIterator
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|disi
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|disi
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|disi
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
