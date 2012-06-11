begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|values
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|DocValues
operator|.
name|Type
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
name|Directory
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
name|IOContext
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
name|Counter
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|/**  * Abstract API for per-document stored primitive values of type<tt>byte[]</tt>  * ,<tt>long</tt> or<tt>double</tt>. The API accepts a single value for each  * document. The underlying storage mechanism, file formats, data-structures and  * representations depend on the actual implementation.  *<p>  * Document IDs passed to this API must always be increasing unless stated  * otherwise.  *</p>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Writer
specifier|abstract
class|class
name|Writer
extends|extends
name|DocValuesConsumer
block|{
DECL|field|bytesUsed
specifier|protected
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|field|type
specifier|protected
name|Type
name|type
decl_stmt|;
comment|/**    * Creates a new {@link Writer}.    *     * @param bytesUsed    *          bytes-usage tracking reference used by implementation to track    *          internally allocated memory. All tracked bytes must be released    *          once {@link #finish(int)} has been called.    */
DECL|method|Writer
specifier|protected
name|Writer
parameter_list|(
name|Counter
name|bytesUsed
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|protected
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * Factory method to create a {@link Writer} instance for a given type. This    * method returns default implementations for each of the different types    * defined in the {@link Type} enumeration.    *     * @param type    *          the {@link Type} to create the {@link Writer} for    * @param id    *          the file name id used to create files within the writer.    * @param directory    *          the {@link Directory} to create the files from.    * @param bytesUsed    *          a byte-usage tracking reference    * @param acceptableOverheadRatio    *          how to trade space for speed. This option is only applicable for    *          docvalues of type {@link Type#BYTES_FIXED_SORTED} and    *          {@link Type#BYTES_VAR_SORTED}.    * @return a new {@link Writer} instance for the given {@link Type}    * @throws IOException    * @see PackedInts#getReader(org.apache.lucene.store.DataInput, float)    */
DECL|method|create
specifier|public
specifier|static
name|DocValuesConsumer
name|create
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|comp
operator|==
literal|null
condition|)
block|{
name|comp
operator|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
expr_stmt|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|VAR_INTS
case|:
return|return
name|Ints
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|type
argument_list|,
name|context
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
return|return
name|Floats
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
case|case
name|FLOAT_64
case|:
return|return
name|Floats
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|true
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|)
return|;
case|case
name|BYTES_FIXED_DEREF
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|true
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|)
return|;
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|true
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|)
return|;
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|false
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|)
return|;
case|case
name|BYTES_VAR_DEREF
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|false
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
return|return
name|Bytes
operator|.
name|getWriter
argument_list|(
name|directory
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|false
argument_list|,
name|comp
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|acceptableOverheadRatio
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown Values: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
