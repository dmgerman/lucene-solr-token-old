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
name|Source
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
name|IndexInput
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
begin_comment
comment|/**  * Base class for disk resident source implementations  * @lucene.internal  */
end_comment
begin_class
DECL|class|DirectSource
specifier|abstract
class|class
name|DirectSource
extends|extends
name|Source
block|{
DECL|field|data
specifier|protected
specifier|final
name|IndexInput
name|data
decl_stmt|;
DECL|field|toNumeric
specifier|private
specifier|final
name|ToNumeric
name|toNumeric
decl_stmt|;
DECL|field|baseOffset
specifier|protected
specifier|final
name|long
name|baseOffset
decl_stmt|;
DECL|method|DirectSource
specifier|public
name|DirectSource
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|input
expr_stmt|;
name|baseOffset
operator|=
name|input
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
name|toNumeric
operator|=
operator|new
name|ShortToLong
argument_list|()
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|toNumeric
operator|=
operator|new
name|BytesToFloat
argument_list|()
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|toNumeric
operator|=
operator|new
name|BytesToDouble
argument_list|()
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|toNumeric
operator|=
operator|new
name|IntToLong
argument_list|()
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|toNumeric
operator|=
operator|new
name|ByteToLong
argument_list|()
expr_stmt|;
break|break;
default|default:
name|toNumeric
operator|=
operator|new
name|LongToLong
argument_list|()
expr_stmt|;
block|}
block|}
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
name|ref
parameter_list|)
block|{
try|try
block|{
specifier|final
name|int
name|sizeToRead
init|=
name|position
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|ref
operator|.
name|grow
argument_list|(
name|sizeToRead
argument_list|)
expr_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|sizeToRead
argument_list|)
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|sizeToRead
expr_stmt|;
return|return
name|ref
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to get value for docID: "
operator|+
name|docID
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
try|try
block|{
name|position
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|toNumeric
operator|.
name|toLong
argument_list|(
name|data
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to get value for docID: "
operator|+
name|docID
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
try|try
block|{
name|position
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|toNumeric
operator|.
name|toDouble
argument_list|(
name|data
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to get value for docID: "
operator|+
name|docID
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|position
specifier|protected
specifier|abstract
name|int
name|position
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|class|ToNumeric
specifier|private
specifier|abstract
specifier|static
class|class
name|ToNumeric
block|{
DECL|method|toLong
specifier|abstract
name|long
name|toLong
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|toDouble
name|double
name|toDouble
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toLong
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
DECL|class|ByteToLong
specifier|private
specifier|static
specifier|final
class|class
name|ByteToLong
extends|extends
name|ToNumeric
block|{
annotation|@
name|Override
DECL|method|toLong
name|long
name|toLong
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|input
operator|.
name|readByte
argument_list|()
return|;
block|}
block|}
DECL|class|ShortToLong
specifier|private
specifier|static
specifier|final
class|class
name|ShortToLong
extends|extends
name|ToNumeric
block|{
annotation|@
name|Override
DECL|method|toLong
name|long
name|toLong
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|input
operator|.
name|readShort
argument_list|()
return|;
block|}
block|}
DECL|class|IntToLong
specifier|private
specifier|static
specifier|final
class|class
name|IntToLong
extends|extends
name|ToNumeric
block|{
annotation|@
name|Override
DECL|method|toLong
name|long
name|toLong
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|input
operator|.
name|readInt
argument_list|()
return|;
block|}
block|}
DECL|class|BytesToFloat
specifier|private
specifier|static
specifier|final
class|class
name|BytesToFloat
extends|extends
name|ToNumeric
block|{
annotation|@
name|Override
DECL|method|toLong
name|long
name|toLong
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ints are not supported"
argument_list|)
throw|;
block|}
DECL|method|toDouble
name|double
name|toDouble
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|BytesToDouble
specifier|private
specifier|static
specifier|final
class|class
name|BytesToDouble
extends|extends
name|ToNumeric
block|{
annotation|@
name|Override
DECL|method|toLong
name|long
name|toLong
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ints are not supported"
argument_list|)
throw|;
block|}
DECL|method|toDouble
name|double
name|toDouble
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|input
operator|.
name|readLong
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|LongToLong
specifier|private
specifier|static
specifier|final
class|class
name|LongToLong
extends|extends
name|ToNumeric
block|{
annotation|@
name|Override
DECL|method|toLong
name|long
name|toLong
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|input
operator|.
name|readLong
argument_list|()
return|;
block|}
DECL|method|toDouble
name|double
name|toDouble
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"doubles are not supported"
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
