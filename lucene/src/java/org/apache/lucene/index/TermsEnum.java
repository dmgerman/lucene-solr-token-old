begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|AttributeSource
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
begin_comment
comment|/** Iterator to seek ({@link #seek}) or step through ({@link  * #next} terms, obtain frequency information ({@link  * #docFreq}), and obtain a {@link DocsEnum} or {@link  * DocsAndPositionsEnum} for the current term ({@link  * #docs}.  *   *<p>Term enumerations are always ordered by  * {@link #getComparator}.  Each term in the enumeration is  * greater than all that precede it.</p>  *  *<p>On obtaining a TermsEnum, you must first call  * {@link #next} or {@link #seek}.  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|TermsEnum
specifier|public
specifier|abstract
class|class
name|TermsEnum
block|{
DECL|field|atts
specifier|private
name|AttributeSource
name|atts
init|=
literal|null
decl_stmt|;
comment|/** Returns the related attributes. */
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
if|if
condition|(
name|atts
operator|==
literal|null
condition|)
name|atts
operator|=
operator|new
name|AttributeSource
argument_list|()
expr_stmt|;
return|return
name|atts
return|;
block|}
comment|/** Represents returned result from {@link #seek}.    *  If status is FOUND, then the precise term was found.    *  If status is NOT_FOUND, then a different term was    *  found.  If the status is END, the end of the iteration    *  was hit. */
DECL|enum|SeekStatus
DECL|enum constant|END
DECL|enum constant|FOUND
DECL|enum constant|NOT_FOUND
specifier|public
specifier|static
enum|enum
name|SeekStatus
block|{
name|END
block|,
name|FOUND
block|,
name|NOT_FOUND
block|}
empty_stmt|;
comment|/** Expert: just like {@link #seek(BytesRef)} but allows    *  you to control whether the implementation should    *  attempt to use its term cache (if it uses one). */
DECL|method|seek
specifier|public
specifier|abstract
name|SeekStatus
name|seek
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Seeks to the specified term.  Returns SeekStatus to    *  indicate whether exact term was found, a different    *  term was found, or EOF was hit.  The target term may    *  be before or after the current term. */
DECL|method|seek
specifier|public
specifier|final
name|SeekStatus
name|seek
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|seek
argument_list|(
name|text
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Seeks to the specified term by ordinal (position) as    *  previously returned by {@link #ord}.  The target ord    *  may be before or after the current ord.  See {@link    *  #seek(BytesRef)}. */
DECL|method|seek
specifier|public
specifier|abstract
name|SeekStatus
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: Seeks a specific position by {@link TermState} previously obtained    * from {@link #termState()}. Callers should maintain the {@link TermState} to    * use this method. Low-level implementations may position the TermsEnum    * without re-seeking the term dictionary.    *<p>    * Seeking by {@link TermState} should only be used iff the enum the state was    * obtained from and the enum the state is used for seeking are obtained from    * the same {@link IndexReader}, otherwise a {@link #seek(BytesRef, TermState)} call can    * leave the enum in undefined state.    *<p>    * NOTE: Using this method with an incompatible {@link TermState} might leave    * this {@link TermsEnum} in undefined state. On a segment level    * {@link TermState} instances are compatible only iff the source and the    * target {@link TermsEnum} operate on the same field. If operating on segment    * level, TermState instances must not be used across segments.    *<p>    * NOTE: A seek by {@link TermState} might not restore the    * {@link AttributeSource}'s state. {@link AttributeSource} states must be    * maintained separately if this method is used.    * @param term the term the TermState corresponds to    * @param state the {@link TermState}    * */
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|/** Increments the enumeration to the next element.    *  Returns the resulting term, or null if the end was    *  hit.  The returned BytesRef may be re-used across calls    *  to next. */
DECL|method|next
specifier|public
specifier|abstract
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns current term. Do not call this before calling    *  next() for the first time, after next() returns null    *  or after seek returns {@link SeekStatus#END}.*/
DECL|method|term
specifier|public
specifier|abstract
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns ordinal position for current term.  This is an    *  optional method (the codec may throw {@link    *  UnsupportedOperationException}).  Do not call this    *  before calling {@link #next} for the first time or after    *  {@link #next} returns null or {@link #seek} returns    *  END; */
DECL|method|ord
specifier|public
specifier|abstract
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of documents containing the current    *  term.  Do not call this before calling next() for the    *  first time, after next() returns null or seek returns    *  {@link SeekStatus#END}.*/
DECL|method|docFreq
specifier|public
specifier|abstract
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the total number of occurrences of this term    *  across all documents (the sum of the freq() for each    *  doc that has this term).  This will be -1 if the    *  codec doesn't support this measure.  Note that, like    *  other term measures, this measure does not take    *  deleted documents into account. */
DECL|method|totalTermFreq
specifier|public
specifier|abstract
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Get {@link DocsEnum} for the current term.  Do not    *  call this before calling {@link #next} or {@link    *  #seek} for the first time.  This method will not    *  return null.    *      * @param skipDocs set bits are documents that should not    * be returned    * @param reuse pass a prior DocsEnum for possible reuse */
DECL|method|docs
specifier|public
specifier|abstract
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Get {@link DocsAndPositionsEnum} for the current term.    *  Do not call this before calling {@link #next} or    *  {@link #seek} for the first time.  This method will    *  only return null if positions were not indexed into    *  the postings by this codec. */
DECL|method|docsAndPositions
specifier|public
specifier|abstract
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: Returns the TermsEnums internal state to position the TermsEnum    * without re-seeking the term dictionary.    *<p>    * NOTE: A seek by {@link TermState} might not capture the    * {@link AttributeSource}'s state. Callers must maintain the    * {@link AttributeSource} states separately    *     * @see TermState    * @see #seek(BytesRef, TermState)    */
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TermState
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|copyFrom
parameter_list|(
name|TermState
name|other
parameter_list|)
block|{       }
block|}
return|;
block|}
comment|/** Return the {@link BytesRef} Comparator used to sort    *  terms provided by the iterator.  This may return    *  null if there are no terms.  Callers may invoke this    *  method many times, so it's best to cache a single    *  instance& reuse it. */
DECL|method|getComparator
specifier|public
specifier|abstract
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** An empty TermsEnum for quickly returning an empty instance e.g.    * in {@link org.apache.lucene.search.MultiTermQuery}    *<p><em>Please note:</em> This enum should be unmodifiable,    * but it is currently possible to add Attributes to it.    * This should not be a problem, as the enum is always empty and    * the existence of unused Attributes does not matter.    */
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|TermsEnum
name|EMPTY
init|=
operator|new
name|TermsEnum
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
annotation|@
name|Override
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|totalTermFreq
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ord
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
comment|// make it synchronized here, to prevent double lazy init
specifier|public
specifier|synchronized
name|AttributeSource
name|attributes
parameter_list|()
block|{
return|return
name|super
operator|.
name|attributes
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
