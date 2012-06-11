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
begin_comment
comment|/**  * Abstract class for enumerating a subset of all terms.   *   *<p>Term enumerations are always ordered by  * {@link #getComparator}.  Each term in the enumeration is  * greater than all that precede it.</p>  *<p><em>Please note:</em> Consumers of this enum cannot  * call {@code seek()}, it is forward only; it throws  * {@link UnsupportedOperationException} when a seeking method  * is called.  */
end_comment
begin_class
DECL|class|FilteredTermsEnum
specifier|public
specifier|abstract
class|class
name|FilteredTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|initialSeekTerm
specifier|private
name|BytesRef
name|initialSeekTerm
init|=
literal|null
decl_stmt|;
DECL|field|doSeek
specifier|private
name|boolean
name|doSeek
decl_stmt|;
DECL|field|actualTerm
specifier|private
name|BytesRef
name|actualTerm
init|=
literal|null
decl_stmt|;
DECL|field|tenum
specifier|private
specifier|final
name|TermsEnum
name|tenum
decl_stmt|;
comment|/** Return value, if term should be accepted or the iteration should    * {@code END}. The {@code *_SEEK} values denote, that after handling the current term    * the enum should call {@link #nextSeekTerm} and step forward.    * @see #accept(BytesRef)    */
DECL|enum|AcceptStatus
DECL|enum constant|YES
DECL|enum constant|YES_AND_SEEK
DECL|enum constant|NO
DECL|enum constant|NO_AND_SEEK
DECL|enum constant|END
specifier|protected
specifier|static
enum|enum
name|AcceptStatus
block|{
name|YES
block|,
name|YES_AND_SEEK
block|,
name|NO
block|,
name|NO_AND_SEEK
block|,
name|END
block|}
empty_stmt|;
comment|/** Return if term is accepted, not accepted or the iteration should ended    * (and possibly seek).    */
DECL|method|accept
specifier|protected
specifier|abstract
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a filtered {@link TermsEnum} on a terms enum.    * @param tenum the terms enumeration to filter.    */
DECL|method|FilteredTermsEnum
specifier|public
name|FilteredTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|)
block|{
name|this
argument_list|(
name|tenum
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a filtered {@link TermsEnum} on a terms enum.    * @param tenum the terms enumeration to filter.    */
DECL|method|FilteredTermsEnum
specifier|public
name|FilteredTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|,
specifier|final
name|boolean
name|startWithSeek
parameter_list|)
block|{
assert|assert
name|tenum
operator|!=
literal|null
assert|;
name|this
operator|.
name|tenum
operator|=
name|tenum
expr_stmt|;
name|doSeek
operator|=
name|startWithSeek
expr_stmt|;
block|}
comment|/**    * Use this method to set the initial {@link BytesRef}    * to seek before iterating. This is a convenience method for    * subclasses that do not override {@link #nextSeekTerm}.    * If the initial seek term is {@code null} (default),    * the enum is empty.    *<P>You can only use this method, if you keep the default    * implementation of {@link #nextSeekTerm}.    */
DECL|method|setInitialSeekTerm
specifier|protected
specifier|final
name|void
name|setInitialSeekTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|initialSeekTerm
operator|=
name|term
expr_stmt|;
block|}
comment|/** On the first call to {@link #next} or if {@link #accept} returns    * {@link AcceptStatus#YES_AND_SEEK} or {@link AcceptStatus#NO_AND_SEEK},    * this method will be called to eventually seek the underlying TermsEnum    * to a new position.    * On the first call, {@code currentTerm} will be {@code null}, later    * calls will provide the term the underlying enum is positioned at.    * This method returns per default only one time the initial seek term    * and then {@code null}, so no repositioning is ever done.    *<p>Override this method, if you want a more sophisticated TermsEnum,    * that repositions the iterator during enumeration.    * If this method always returns {@code null} the enum is empty.    *<p><em>Please note:</em> This method should always provide a greater term    * than the last enumerated term, else the behaviour of this enum    * violates the contract for TermsEnums.    */
DECL|method|nextSeekTerm
specifier|protected
name|BytesRef
name|nextSeekTerm
parameter_list|(
specifier|final
name|BytesRef
name|currentTerm
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|t
init|=
name|initialSeekTerm
decl_stmt|;
name|initialSeekTerm
operator|=
literal|null
expr_stmt|;
return|return
name|t
return|;
block|}
comment|/**    * Returns the related attributes, the returned {@link AttributeSource}    * is shared with the delegate {@code TermsEnum}.    */
annotation|@
name|Override
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
return|return
name|tenum
operator|.
name|attributes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|tenum
operator|.
name|term
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|tenum
operator|.
name|getComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|tenum
operator|.
name|docFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|tenum
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
comment|/** This enum does not support seeking!    * @throws UnsupportedOperationException    */
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" does not support seeking"
argument_list|)
throw|;
block|}
comment|/** This enum does not support seeking!    * @throws UnsupportedOperationException    */
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" does not support seeking"
argument_list|)
throw|;
block|}
comment|/** This enum does not support seeking!    * @throws UnsupportedOperationException    */
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" does not support seeking"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|tenum
operator|.
name|ord
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|boolean
name|needsFreqs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|tenum
operator|.
name|docs
argument_list|(
name|bits
argument_list|,
name|reuse
argument_list|,
name|needsFreqs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|boolean
name|needsOffsets
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|tenum
operator|.
name|docsAndPositions
argument_list|(
name|bits
argument_list|,
name|reuse
argument_list|,
name|needsOffsets
argument_list|)
return|;
block|}
comment|/** This enum does not support seeking!    * @throws UnsupportedOperationException    */
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
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
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" does not support seeking"
argument_list|)
throw|;
block|}
comment|/**    * Returns the filtered enums term state     */
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|tenum
operator|!=
literal|null
assert|;
return|return
name|tenum
operator|.
name|termState
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("FTE.next doSeek=" + doSeek);
comment|//new Throwable().printStackTrace(System.out);
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// Seek or forward the iterator
if|if
condition|(
name|doSeek
condition|)
block|{
name|doSeek
operator|=
literal|false
expr_stmt|;
specifier|final
name|BytesRef
name|t
init|=
name|nextSeekTerm
argument_list|(
name|actualTerm
argument_list|)
decl_stmt|;
comment|//System.out.println("  seek to t=" + (t == null ? "null" : t.utf8ToString()) + " tenum=" + tenum);
comment|// Make sure we always seek forward:
assert|assert
name|actualTerm
operator|==
literal|null
operator|||
name|t
operator|==
literal|null
operator|||
name|getComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|t
argument_list|,
name|actualTerm
argument_list|)
operator|>
literal|0
operator|:
literal|"curTerm="
operator|+
name|actualTerm
operator|+
literal|" seekTerm="
operator|+
name|t
assert|;
if|if
condition|(
name|t
operator|==
literal|null
operator|||
name|tenum
operator|.
name|seekCeil
argument_list|(
name|t
argument_list|,
literal|false
argument_list|)
operator|==
name|SeekStatus
operator|.
name|END
condition|)
block|{
comment|// no more terms to seek to or enum exhausted
comment|//System.out.println("  return null");
return|return
literal|null
return|;
block|}
name|actualTerm
operator|=
name|tenum
operator|.
name|term
argument_list|()
expr_stmt|;
comment|//System.out.println("  got term=" + actualTerm.utf8ToString());
block|}
else|else
block|{
name|actualTerm
operator|=
name|tenum
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|actualTerm
operator|==
literal|null
condition|)
block|{
comment|// enum exhausted
return|return
literal|null
return|;
block|}
block|}
comment|// check if term is accepted
switch|switch
condition|(
name|accept
argument_list|(
name|actualTerm
argument_list|)
condition|)
block|{
case|case
name|YES_AND_SEEK
case|:
name|doSeek
operator|=
literal|true
expr_stmt|;
comment|// term accepted, but we need to seek so fall-through
case|case
name|YES
case|:
comment|// term accepted
return|return
name|actualTerm
return|;
case|case
name|NO_AND_SEEK
case|:
comment|// invalid term, seek next time
name|doSeek
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|END
case|:
comment|// we are supposed to end the enum
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
