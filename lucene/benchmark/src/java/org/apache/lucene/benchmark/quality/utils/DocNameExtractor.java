begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.quality.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
operator|.
name|utils
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|FieldInfo
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
name|StoredFieldVisitor
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
name|IndexSearcher
import|;
end_import
begin_comment
comment|/**  * Utility: extract doc names from an index  */
end_comment
begin_class
DECL|class|DocNameExtractor
specifier|public
class|class
name|DocNameExtractor
block|{
DECL|field|docNameField
specifier|private
specifier|final
name|String
name|docNameField
decl_stmt|;
comment|/**    * Constructor for DocNameExtractor.    * @param docNameField name of the stored field containing the doc name.     */
DECL|method|DocNameExtractor
specifier|public
name|DocNameExtractor
parameter_list|(
specifier|final
name|String
name|docNameField
parameter_list|)
block|{
name|this
operator|.
name|docNameField
operator|=
name|docNameField
expr_stmt|;
block|}
comment|/**    * Extract the name of the input doc from the index.    * @param searcher access to the index.    * @param docid ID of doc whose name is needed.    * @return the name of the input doc as extracted from the index.    * @throws IOException if cannot extract the doc name from the index.    */
DECL|method|docName
specifier|public
name|String
name|docName
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|int
name|docid
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|name
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|document
argument_list|(
name|docid
argument_list|,
operator|new
name|StoredFieldVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|String
name|value
init|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|name
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Status
operator|.
name|STOP
return|;
block|}
elseif|else
if|if
condition|(
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
name|docNameField
argument_list|)
condition|)
block|{
return|return
name|Status
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|Status
operator|.
name|NO
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
name|name
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
