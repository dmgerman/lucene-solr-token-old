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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|zip
operator|.
name|DataFormatException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Inflater
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
name|Document
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
name|Field
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
name|IndexInput
import|;
end_import
begin_comment
comment|/**  * Class responsible for access to stored document fields.  *  * It uses&lt;segment&gt;.fdt and&lt;segment&gt;.fdx; files.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|FieldsReader
specifier|final
class|class
name|FieldsReader
block|{
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsStream
specifier|private
name|IndexInput
name|fieldsStream
decl_stmt|;
DECL|field|indexStream
specifier|private
name|IndexInput
name|indexStream
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|method|FieldsReader
name|FieldsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fn
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|fieldsStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|segment
operator|+
literal|".fdt"
argument_list|)
expr_stmt|;
name|indexStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|segment
operator|+
literal|".fdx"
argument_list|)
expr_stmt|;
name|size
operator|=
call|(
name|int
call|)
argument_list|(
name|indexStream
operator|.
name|length
argument_list|()
operator|/
literal|8
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|size
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|doc
specifier|final
name|Document
name|doc
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|seek
argument_list|(
name|n
operator|*
literal|8L
argument_list|)
expr_stmt|;
name|long
name|position
init|=
name|indexStream
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|numFields
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|int
name|fieldNumber
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
name|byte
name|bits
init|=
name|fieldsStream
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|compressed
init|=
operator|(
name|bits
operator|&
name|FieldsWriter
operator|.
name|FIELD_IS_COMPRESSED
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|tokenize
init|=
operator|(
name|bits
operator|&
name|FieldsWriter
operator|.
name|FIELD_IS_TOKENIZED
operator|)
operator|!=
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|bits
operator|&
name|FieldsWriter
operator|.
name|FIELD_IS_BINARY
operator|)
operator|!=
literal|0
condition|)
block|{
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|fieldsStream
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
name|fieldsStream
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|compressed
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|uncompress
argument_list|(
name|b
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|b
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Field
operator|.
name|Index
name|index
decl_stmt|;
name|Field
operator|.
name|Store
name|store
init|=
name|Field
operator|.
name|Store
operator|.
name|YES
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
name|tokenize
condition|)
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
expr_stmt|;
elseif|else
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
operator|!
name|tokenize
condition|)
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
expr_stmt|;
else|else
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|NO
expr_stmt|;
if|if
condition|(
name|compressed
condition|)
block|{
name|store
operator|=
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
expr_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|fieldsStream
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
name|fieldsStream
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
comment|// field name
operator|new
name|String
argument_list|(
name|uncompress
argument_list|(
name|b
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
comment|// uncompress the value and add as string
name|store
argument_list|,
name|index
argument_list|,
name|fi
operator|.
name|storeTermVector
condition|?
name|Field
operator|.
name|TermVector
operator|.
name|YES
else|:
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fi
operator|.
name|name
argument_list|,
comment|// name
name|fieldsStream
operator|.
name|readString
argument_list|()
argument_list|,
comment|// read value
name|store
argument_list|,
name|index
argument_list|,
name|fi
operator|.
name|storeTermVector
condition|?
name|Field
operator|.
name|TermVector
operator|.
name|YES
else|:
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
return|;
block|}
DECL|method|uncompress
specifier|private
specifier|final
name|byte
index|[]
name|uncompress
parameter_list|(
specifier|final
name|byte
index|[]
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|Inflater
name|decompressor
init|=
operator|new
name|Inflater
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|setInput
argument_list|(
name|input
argument_list|)
expr_stmt|;
comment|// Create an expandable byte array to hold the decompressed data
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|input
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// Decompress the data
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|decompressor
operator|.
name|finished
argument_list|()
condition|)
block|{
try|try
block|{
name|int
name|count
init|=
name|decompressor
operator|.
name|inflate
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataFormatException
name|e
parameter_list|)
block|{
comment|// this will happen if the field is not compressed
throw|throw
operator|new
name|IOException
argument_list|(
literal|"field data are in wrong format: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|decompressor
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// Get the decompressed data
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class
end_unit
