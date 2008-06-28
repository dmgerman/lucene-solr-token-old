begin_unit
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Term
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
name|TermDocs
import|;
end_import
begin_comment
comment|/**  * A {@link org.apache.lucene.index.TermDocs} navigating an {@link InstantiatedIndexReader}.  */
end_comment
begin_class
DECL|class|InstantiatedTermDocs
specifier|public
class|class
name|InstantiatedTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|reader
specifier|private
specifier|final
name|InstantiatedIndexReader
name|reader
decl_stmt|;
DECL|method|InstantiatedTermDocs
specifier|public
name|InstantiatedTermDocs
parameter_list|(
name|InstantiatedIndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
DECL|field|currentDocumentIndex
specifier|private
name|int
name|currentDocumentIndex
decl_stmt|;
DECL|field|currentDocumentInformation
specifier|protected
name|InstantiatedTermDocumentInformation
name|currentDocumentInformation
decl_stmt|;
DECL|field|currentTerm
specifier|protected
name|InstantiatedTerm
name|currentTerm
decl_stmt|;
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|currentTerm
operator|=
name|reader
operator|.
name|getIndex
argument_list|()
operator|.
name|findTerm
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|currentDocumentIndex
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermEnum
name|termEnum
parameter_list|)
block|{
name|seek
argument_list|(
name|termEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|currentDocumentInformation
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|currentDocumentInformation
operator|.
name|getTermPositions
argument_list|()
operator|.
name|length
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|currentTerm
operator|!=
literal|null
condition|)
block|{
name|currentDocumentIndex
operator|++
expr_stmt|;
if|if
condition|(
name|currentDocumentIndex
operator|<
name|currentTerm
operator|.
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
condition|)
block|{
name|currentDocumentInformation
operator|=
name|currentTerm
operator|.
name|getAssociatedDocuments
argument_list|()
index|[
name|currentDocumentIndex
index|]
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
operator|&&
name|reader
operator|.
name|isDeleted
argument_list|(
name|currentDocumentInformation
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|next
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|)
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
block|{
break|break;
block|}
name|docs
index|[
name|i
index|]
operator|=
name|doc
argument_list|()
expr_stmt|;
name|freqs
index|[
name|i
index|]
operator|=
name|freq
argument_list|()
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
comment|/**    * Skips entries to the first beyond the current whose document number is    * greater than or equal to<i>target</i>.<p>Returns true if there is such    * an entry.<p>Behaves as if written:<pre>    *   boolean skipTo(int target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> doc());    *     return true;    *   }    *</pre>    * This implementation is considerably more efficient than that.    *    */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
block|{
if|if
condition|(
name|currentTerm
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentDocumentIndex
operator|>=
name|target
condition|)
block|{
return|return
name|next
argument_list|()
return|;
block|}
name|int
name|startOffset
init|=
name|currentDocumentIndex
operator|>=
literal|0
condition|?
name|currentDocumentIndex
else|:
literal|0
decl_stmt|;
name|int
name|pos
init|=
name|currentTerm
operator|.
name|seekCeilingDocumentInformationIndex
argument_list|(
name|target
argument_list|,
name|startOffset
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|currentDocumentInformation
operator|=
name|currentTerm
operator|.
name|getAssociatedDocuments
argument_list|()
index|[
name|pos
index|]
expr_stmt|;
name|currentDocumentIndex
operator|=
name|pos
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
operator|&&
name|reader
operator|.
name|isDeleted
argument_list|(
name|currentDocumentInformation
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|next
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Does nothing    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{   }
block|}
end_class
end_unit
