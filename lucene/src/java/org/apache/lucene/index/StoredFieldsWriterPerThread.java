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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|document
operator|.
name|Fieldable
import|;
end_import
begin_class
DECL|class|StoredFieldsWriterPerThread
specifier|final
class|class
name|StoredFieldsWriterPerThread
block|{
DECL|field|localFieldsWriter
specifier|final
name|FieldsWriter
name|localFieldsWriter
decl_stmt|;
DECL|field|storedFieldsWriter
specifier|final
name|StoredFieldsWriter
name|storedFieldsWriter
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|doc
name|StoredFieldsWriter
operator|.
name|PerDoc
name|doc
decl_stmt|;
DECL|method|StoredFieldsWriterPerThread
specifier|public
name|StoredFieldsWriterPerThread
parameter_list|(
name|DocumentsWriter
operator|.
name|DocState
name|docState
parameter_list|,
name|StoredFieldsWriter
name|storedFieldsWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|storedFieldsWriter
operator|=
name|storedFieldsWriter
expr_stmt|;
name|this
operator|.
name|docState
operator|=
name|docState
expr_stmt|;
name|localFieldsWriter
operator|=
operator|new
name|FieldsWriter
argument_list|(
operator|(
name|IndexOutput
operator|)
literal|null
argument_list|,
operator|(
name|IndexOutput
operator|)
literal|null
argument_list|,
name|storedFieldsWriter
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
comment|// Only happens if previous document hit non-aborting
comment|// exception while writing stored fields into
comment|// localFieldsWriter:
name|doc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|doc
operator|.
name|docID
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
block|}
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|Fieldable
name|field
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|doc
operator|=
name|storedFieldsWriter
operator|.
name|getPerDoc
argument_list|()
expr_stmt|;
name|doc
operator|.
name|docID
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
name|localFieldsWriter
operator|.
name|setFieldsStream
argument_list|(
name|doc
operator|.
name|fdt
argument_list|)
expr_stmt|;
assert|assert
name|doc
operator|.
name|numStoredFields
operator|==
literal|0
operator|:
literal|"doc.numStoredFields="
operator|+
name|doc
operator|.
name|numStoredFields
assert|;
assert|assert
literal|0
operator|==
name|doc
operator|.
name|fdt
operator|.
name|length
argument_list|()
assert|;
assert|assert
literal|0
operator|==
name|doc
operator|.
name|fdt
operator|.
name|getFilePointer
argument_list|()
assert|;
block|}
name|localFieldsWriter
operator|.
name|writeField
argument_list|(
name|fieldInfo
argument_list|,
name|field
argument_list|)
expr_stmt|;
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"StoredFieldsWriterPerThread.processFields.writeField"
argument_list|)
assert|;
name|doc
operator|.
name|numStoredFields
operator|++
expr_stmt|;
block|}
DECL|method|finishDocument
specifier|public
name|DocumentsWriter
operator|.
name|DocWriter
name|finishDocument
parameter_list|()
block|{
comment|// If there were any stored fields in this doc, doc will
comment|// be non-null; else it's null.
try|try
block|{
return|return
name|doc
return|;
block|}
finally|finally
block|{
name|doc
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|abort
argument_list|()
expr_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
