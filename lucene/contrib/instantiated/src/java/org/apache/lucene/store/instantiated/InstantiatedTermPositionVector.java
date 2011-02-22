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
name|TermPositionVector
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
name|TermVectorOffsetInfo
import|;
end_import
begin_comment
comment|/**  * Extended vector space view of a document in an {@link InstantiatedIndexReader}.  *  * @see org.apache.lucene.index.TermPositionVector  */
end_comment
begin_class
DECL|class|InstantiatedTermPositionVector
specifier|public
class|class
name|InstantiatedTermPositionVector
extends|extends
name|InstantiatedTermFreqVector
implements|implements
name|TermPositionVector
block|{
DECL|method|InstantiatedTermPositionVector
specifier|public
name|InstantiatedTermPositionVector
parameter_list|(
name|InstantiatedDocument
name|document
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|document
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|getTermPositions
specifier|public
name|int
index|[]
name|getTermPositions
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|getTermDocumentInformations
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getTermPositions
argument_list|()
return|;
block|}
DECL|method|getOffsets
specifier|public
name|TermVectorOffsetInfo
index|[]
name|getOffsets
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|getTermDocumentInformations
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getTermOffsets
argument_list|()
return|;
block|}
block|}
end_class
end_unit
