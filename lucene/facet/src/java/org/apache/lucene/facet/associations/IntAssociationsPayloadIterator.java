begin_unit
begin_package
DECL|package|org.apache.lucene.facet.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
package|;
end_package
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
name|util
operator|.
name|collections
operator|.
name|IntToIntMap
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An {@link AssociationsPayloadIterator} over integer association values.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|IntAssociationsPayloadIterator
specifier|public
class|class
name|IntAssociationsPayloadIterator
extends|extends
name|AssociationsPayloadIterator
argument_list|<
name|CategoryIntAssociation
argument_list|>
block|{
DECL|field|ordinalAssociations
specifier|private
specifier|final
name|IntToIntMap
name|ordinalAssociations
init|=
operator|new
name|IntToIntMap
argument_list|()
decl_stmt|;
DECL|method|IntAssociationsPayloadIterator
specifier|public
name|IntAssociationsPayloadIterator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|CategoryIntAssociation
name|association
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|association
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleAssociation
specifier|protected
name|void
name|handleAssociation
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|CategoryIntAssociation
name|association
parameter_list|)
block|{
name|ordinalAssociations
operator|.
name|put
argument_list|(
name|ordinal
argument_list|,
name|association
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the integer association values of the categories that are    * associated with the given document, or {@code null} if the document has no    * associations.    *<p>    *<b>NOTE:</b> you are not expected to modify the returned map.    */
DECL|method|getAssociations
specifier|public
name|IntToIntMap
name|getAssociations
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|ordinalAssociations
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|setNextDoc
argument_list|(
name|docID
argument_list|)
condition|?
name|ordinalAssociations
else|:
literal|null
return|;
block|}
block|}
end_class
end_unit
