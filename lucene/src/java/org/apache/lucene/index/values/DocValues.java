begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|BytesRef
import|;
end_import
begin_class
DECL|class|DocValues
specifier|public
specifier|abstract
class|class
name|DocValues
implements|implements
name|Closeable
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|DocValues
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|DocValues
index|[
literal|0
index|]
decl_stmt|;
DECL|field|cache
specifier|private
name|SourceCache
name|cache
init|=
operator|new
name|SourceCache
operator|.
name|DirectSourceCache
argument_list|()
decl_stmt|;
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getEnum
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|getEnum
specifier|public
specifier|abstract
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|load
specifier|public
specifier|abstract
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getSource
specifier|public
name|Source
name|getSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|getSortedSorted
specifier|public
name|SortedSource
name|getSortedSorted
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|cache
operator|.
name|laodSorted
argument_list|(
name|this
argument_list|,
name|comparator
argument_list|)
return|;
block|}
DECL|method|loadSorted
specifier|public
name|SortedSource
name|loadSorted
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|type
specifier|public
specifier|abstract
name|Values
name|type
parameter_list|()
function_decl|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|cache
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|SourceCache
name|cache
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|cache
init|)
block|{
name|this
operator|.
name|cache
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
block|}
comment|/**    * Source of integer (returned as java long), per document. The underlying    * implementation may use different numbers of bits per value; long is only    * used since it can handle all precisions.    */
DECL|class|Source
specifier|public
specifier|static
specifier|abstract
class|class
name|Source
block|{
DECL|field|missingValues
specifier|protected
specifier|final
name|MissingValues
name|missingValues
init|=
operator|new
name|MissingValues
argument_list|()
decl_stmt|;
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ints are not supported"
argument_list|)
throw|;
block|}
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"floats are not supported"
argument_list|)
throw|;
block|}
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"bytes are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns number of unique values. Some impls may throw      * UnsupportedOperationException.      */
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getEnum
argument_list|(
operator|new
name|AttributeSource
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getMissing
specifier|public
name|MissingValues
name|getMissing
parameter_list|()
block|{
return|return
name|missingValues
return|;
block|}
DECL|method|type
specifier|public
specifier|abstract
name|Values
name|type
parameter_list|()
function_decl|;
DECL|method|getEnum
specifier|public
specifier|abstract
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|ramBytesUsed
specifier|public
specifier|abstract
name|long
name|ramBytesUsed
parameter_list|()
function_decl|;
block|}
DECL|class|SourceEnum
specifier|abstract
specifier|static
class|class
name|SourceEnum
extends|extends
name|ValuesEnum
block|{
DECL|field|source
specifier|protected
specifier|final
name|Source
name|source
decl_stmt|;
DECL|field|numDocs
specifier|protected
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|pos
specifier|protected
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SourceEnum
name|SourceEnum
parameter_list|(
name|AttributeSource
name|attrs
parameter_list|,
name|Values
name|type
parameter_list|,
name|Source
name|source
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|super
argument_list|(
name|attrs
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|NO_MORE_DOCS
return|;
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
DECL|class|SortedSource
specifier|public
specifier|static
specifier|abstract
class|class
name|SortedSource
extends|extends
name|Source
block|{
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
return|return
name|getByOrd
argument_list|(
name|ord
argument_list|(
name|docID
argument_list|)
argument_list|,
name|bytesRef
argument_list|)
return|;
block|}
comment|/**      * Returns ord for specified docID. If this docID had not been added to the      * Writer, the ord is 0. Ord is dense, ie, starts at 0, then increments by 1      * for the next (as defined by {@link Comparator} value.      */
DECL|method|ord
specifier|public
specifier|abstract
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Returns value for specified ord. */
DECL|method|getByOrd
specifier|public
specifier|abstract
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
function_decl|;
DECL|class|LookupResult
specifier|public
specifier|static
class|class
name|LookupResult
block|{
DECL|field|found
specifier|public
name|boolean
name|found
decl_stmt|;
DECL|field|ord
specifier|public
name|int
name|ord
decl_stmt|;
block|}
comment|/**      * Finds the largest ord whose value is<= the requested value. If      * {@link LookupResult#found} is true, then ord is an exact match. The      * returned {@link LookupResult} may be reused across calls.      */
DECL|method|getByValue
specifier|public
specifier|final
name|LookupResult
name|getByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
return|return
name|getByValue
argument_list|(
name|value
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getByValue
specifier|public
specifier|abstract
name|LookupResult
name|getByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|BytesRef
name|tmpRef
parameter_list|)
function_decl|;
block|}
DECL|class|MissingValues
specifier|public
specifier|final
specifier|static
class|class
name|MissingValues
block|{
DECL|field|longValue
specifier|public
name|long
name|longValue
decl_stmt|;
DECL|field|doubleValue
specifier|public
name|double
name|doubleValue
decl_stmt|;
DECL|field|bytesValue
specifier|public
name|BytesRef
name|bytesValue
decl_stmt|;
DECL|method|copy
specifier|public
specifier|final
name|void
name|copy
parameter_list|(
name|MissingValues
name|values
parameter_list|)
block|{
name|longValue
operator|=
name|values
operator|.
name|longValue
expr_stmt|;
name|doubleValue
operator|=
name|values
operator|.
name|doubleValue
expr_stmt|;
name|bytesValue
operator|=
name|values
operator|.
name|bytesValue
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
