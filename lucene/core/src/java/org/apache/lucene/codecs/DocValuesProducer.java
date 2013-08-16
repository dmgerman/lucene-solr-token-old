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
name|BinaryDocValues
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
name|NumericDocValues
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
name|SortedDocValues
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
name|SortedSetDocValues
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
comment|/** Abstract API that produces numeric, binary and  * sorted docvalues.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValuesProducer
specifier|public
specifier|abstract
class|class
name|DocValuesProducer
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|DocValuesProducer
specifier|protected
name|DocValuesProducer
parameter_list|()
block|{}
comment|/** Returns {@link NumericDocValues} for this field.    *  The returned instance need not be thread-safe: it will only be    *  used by a single thread. */
DECL|method|getNumeric
specifier|public
specifier|abstract
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link BinaryDocValues} for this field.    *  The returned instance need not be thread-safe: it will only be    *  used by a single thread. */
DECL|method|getBinary
specifier|public
specifier|abstract
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link SortedDocValues} for this field.    *  The returned instance need not be thread-safe: it will only be    *  used by a single thread. */
DECL|method|getSorted
specifier|public
specifier|abstract
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link SortedSetDocValues} for this field.    *  The returned instance need not be thread-safe: it will only be    *  used by a single thread. */
DECL|method|getSortedSet
specifier|public
specifier|abstract
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a {@link Bits} at the size of<code>reader.maxDoc()</code>,     *  with turned on bits for each docid that does have a value for this field.    *  The returned instance need not be thread-safe: it will only be    *  used by a single thread. */
DECL|method|getDocsWithField
specifier|public
specifier|abstract
name|Bits
name|getDocsWithField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * A simple implementation of {@link DocValuesProducer#getDocsWithField} that     * returns {@code true} if a document has an ordinal&gt;= 0    *<p>    * Codecs can choose to use this (or implement it more efficiently another way), but    * in most cases a Bits is unnecessary anyway: users can check this as they go.    */
DECL|class|SortedDocsWithField
specifier|public
specifier|static
class|class
name|SortedDocsWithField
implements|implements
name|Bits
block|{
DECL|field|in
specifier|final
name|SortedDocValues
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|SortedDocsWithField
specifier|public
name|SortedDocsWithField
parameter_list|(
name|SortedDocValues
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|in
operator|.
name|getOrd
argument_list|(
name|index
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
comment|/**     * A simple implementation of {@link DocValuesProducer#getDocsWithField} that     * returns {@code true} if a document has any ordinals.    *<p>    * Codecs can choose to use this (or implement it more efficiently another way), but    * in most cases a Bits is unnecessary anyway: users can check this as they go.    */
DECL|class|SortedSetDocsWithField
specifier|public
specifier|static
class|class
name|SortedSetDocsWithField
implements|implements
name|Bits
block|{
DECL|field|in
specifier|final
name|SortedSetDocValues
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|SortedSetDocsWithField
specifier|public
name|SortedSetDocsWithField
parameter_list|(
name|SortedSetDocValues
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|nextOrd
argument_list|()
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
block|}
end_class
end_unit
