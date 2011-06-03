begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|values
operator|.
name|IndexDocValues
import|;
end_import
begin_comment
comment|/**  * Abstract API that consumes per document values. Concrete implementations of  * this convert field values into a Codec specific format during indexing.  *<p>  * The {@link PerDocConsumer} API is accessible through flexible indexing / the  * {@link Codec} - API providing per field consumers and producers for inverted  * data (terms, postings) as well as per-document data.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|PerDocConsumer
specifier|public
specifier|abstract
class|class
name|PerDocConsumer
implements|implements
name|Closeable
block|{
comment|/** Adds a new DocValuesField */
DECL|method|addValuesField
specifier|public
specifier|abstract
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Consumes and merges the given {@link PerDocValues} producer    * into this consumers format.       */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|PerDocValues
name|producer
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|producer
operator|.
name|fields
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
specifier|final
name|IndexDocValues
name|docValues
init|=
name|producer
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValues
operator|==
literal|null
condition|)
block|{
comment|/*            * It is actually possible that a fieldInfo has a values type but no            * values are actually available. this can happen if there are already            * segments without values around.            */
continue|continue;
block|}
specifier|final
name|DocValuesConsumer
name|docValuesConsumer
init|=
name|addValuesField
argument_list|(
name|mergeState
operator|.
name|fieldInfo
argument_list|)
decl_stmt|;
assert|assert
name|docValuesConsumer
operator|!=
literal|null
assert|;
name|docValuesConsumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|docValues
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
