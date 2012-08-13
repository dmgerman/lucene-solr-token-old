begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|Closeable
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
name|Fields
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
name|MergeState
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
name|SegmentWriteState
import|;
end_import
begin_comment
comment|// javadocs
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
name|Terms
import|;
end_import
begin_comment
comment|/**   * Abstract API that consumes terms, doc, freq, prox, offset and  * payloads postings.  Concrete implementations of this  * actually do "something" with the postings (write it into  * the index in a specific format).  *<p>  * The lifecycle is:  *<ol>  *<li>FieldsConsumer is created by   *       {@link PostingsFormat#fieldsConsumer(SegmentWriteState)}.  *<li>For each field, {@link #addField(FieldInfo)} is called,  *       returning a {@link TermsConsumer} for the field.  *<li>After all fields are added, the consumer is {@link #close}d.  *</ol>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FieldsConsumer
specifier|public
specifier|abstract
class|class
name|FieldsConsumer
implements|implements
name|Closeable
block|{
comment|/** Add a new field */
DECL|method|addField
specifier|public
specifier|abstract
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when we are done adding everything. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|mergeState
operator|.
name|fieldInfo
operator|=
name|mergeState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
expr_stmt|;
assert|assert
name|mergeState
operator|.
name|fieldInfo
operator|!=
literal|null
operator|:
literal|"FieldInfo for field is null: "
operator|+
name|field
assert|;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsConsumer
name|termsConsumer
init|=
name|addField
argument_list|(
name|mergeState
operator|.
name|fieldInfo
argument_list|)
decl_stmt|;
name|termsConsumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
