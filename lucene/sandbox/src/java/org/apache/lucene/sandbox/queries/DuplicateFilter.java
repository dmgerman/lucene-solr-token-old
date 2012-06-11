begin_unit
begin_package
DECL|package|org.apache.lucene.sandbox.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|search
operator|.
name|DocIdSet
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
name|DocIdSetIterator
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
name|Filter
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Filter to remove duplicate values from search results.  *<p>  * WARNING: for this to work correctly, you may have to wrap  * your reader as it cannot current deduplicate across different  * index segments.  *   * @see SlowCompositeReaderWrapper  */
end_comment
begin_class
DECL|class|DuplicateFilter
specifier|public
class|class
name|DuplicateFilter
extends|extends
name|Filter
block|{
comment|// TODO: make duplicate filter aware of ReaderContext such that we can
comment|// filter duplicates across segments
comment|/**    * KeepMode determines which document id to consider as the master, all others being    * identified as duplicates. Selecting the "first occurrence" can potentially save on IO.    */
DECL|enum|KeepMode
specifier|public
enum|enum
name|KeepMode
block|{
DECL|enum constant|KM_USE_FIRST_OCCURRENCE
DECL|enum constant|KM_USE_LAST_OCCURRENCE
name|KM_USE_FIRST_OCCURRENCE
block|,
name|KM_USE_LAST_OCCURRENCE
block|}
DECL|field|keepMode
specifier|private
name|KeepMode
name|keepMode
decl_stmt|;
comment|/**    * "Full" processing mode starts by setting all bits to false and only setting bits    * for documents that contain the given field and are identified as none-duplicates.    *<p/>    * "Fast" processing sets all bits to true then unsets all duplicate docs found for the    * given field. This approach avoids the need to read DocsEnum for terms that are seen    * to have a document frequency of exactly "1" (i.e. no duplicates). While a potentially    * faster approach , the downside is that bitsets produced will include bits set for    * documents that do not actually contain the field given.    */
DECL|enum|ProcessingMode
specifier|public
enum|enum
name|ProcessingMode
block|{
DECL|enum constant|PM_FULL_VALIDATION
DECL|enum constant|PM_FAST_INVALIDATION
name|PM_FULL_VALIDATION
block|,
name|PM_FAST_INVALIDATION
block|}
DECL|field|processingMode
specifier|private
name|ProcessingMode
name|processingMode
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|method|DuplicateFilter
specifier|public
name|DuplicateFilter
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|KeepMode
operator|.
name|KM_USE_LAST_OCCURRENCE
argument_list|,
name|ProcessingMode
operator|.
name|PM_FULL_VALIDATION
argument_list|)
expr_stmt|;
block|}
DECL|method|DuplicateFilter
specifier|public
name|DuplicateFilter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|KeepMode
name|keepMode
parameter_list|,
name|ProcessingMode
name|processingMode
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|keepMode
operator|=
name|keepMode
expr_stmt|;
name|this
operator|.
name|processingMode
operator|=
name|processingMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|processingMode
operator|==
name|ProcessingMode
operator|.
name|PM_FAST_INVALIDATION
condition|)
block|{
return|return
name|fastBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|correctBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
block|}
DECL|method|correctBits
specifier|private
name|FixedBitSet
name|correctBits
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|//assume all are INvalid
name|Terms
name|terms
init|=
name|reader
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
name|bits
return|;
block|}
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|currTerm
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|currTerm
operator|==
literal|null
condition|)
block|{
break|break;
block|}
else|else
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|keepMode
operator|==
name|KeepMode
operator|.
name|KM_USE_FIRST_OCCURRENCE
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|lastDoc
init|=
name|doc
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|lastDoc
operator|=
name|doc
expr_stmt|;
name|doc
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
block|}
name|bits
operator|.
name|set
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|bits
return|;
block|}
DECL|method|fastBits
specifier|private
name|FixedBitSet
name|fastBits
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|//assume all are valid
name|Terms
name|terms
init|=
name|reader
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
name|bits
return|;
block|}
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|currTerm
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|currTerm
operator|==
literal|null
condition|)
block|{
break|break;
block|}
else|else
block|{
if|if
condition|(
name|termsEnum
operator|.
name|docFreq
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// unset potential duplicates
name|docs
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|acceptDocs
argument_list|,
name|docs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|keepMode
operator|==
name|KeepMode
operator|.
name|KM_USE_FIRST_OCCURRENCE
condition|)
block|{
name|doc
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
name|int
name|lastDoc
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|lastDoc
operator|=
name|doc
expr_stmt|;
name|bits
operator|.
name|clear
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
name|doc
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|keepMode
operator|==
name|KeepMode
operator|.
name|KM_USE_LAST_OCCURRENCE
condition|)
block|{
comment|// restore the last bit
name|bits
operator|.
name|set
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|bits
return|;
block|}
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|setFieldName
specifier|public
name|void
name|setFieldName
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
DECL|method|getKeepMode
specifier|public
name|KeepMode
name|getKeepMode
parameter_list|()
block|{
return|return
name|keepMode
return|;
block|}
DECL|method|setKeepMode
specifier|public
name|void
name|setKeepMode
parameter_list|(
name|KeepMode
name|keepMode
parameter_list|)
block|{
name|this
operator|.
name|keepMode
operator|=
name|keepMode
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
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DuplicateFilter
name|other
init|=
operator|(
name|DuplicateFilter
operator|)
name|obj
decl_stmt|;
return|return
name|keepMode
operator|==
name|other
operator|.
name|keepMode
operator|&&
name|processingMode
operator|==
name|other
operator|.
name|processingMode
operator|&&
name|fieldName
operator|!=
literal|null
operator|&&
name|fieldName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldName
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
name|int
name|hash
init|=
literal|217
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|keepMode
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|processingMode
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|fieldName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
DECL|method|getProcessingMode
specifier|public
name|ProcessingMode
name|getProcessingMode
parameter_list|()
block|{
return|return
name|processingMode
return|;
block|}
DECL|method|setProcessingMode
specifier|public
name|void
name|setProcessingMode
parameter_list|(
name|ProcessingMode
name|processingMode
parameter_list|)
block|{
name|this
operator|.
name|processingMode
operator|=
name|processingMode
expr_stmt|;
block|}
block|}
end_class
end_unit
