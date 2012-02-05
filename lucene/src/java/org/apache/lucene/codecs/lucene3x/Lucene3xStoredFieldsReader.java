begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|codecs
operator|.
name|StoredFieldsReader
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
name|CorruptIndexException
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
name|FieldInfos
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
name|IndexFileNames
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
name|IndexFormatTooNewException
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
name|IndexFormatTooOldException
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
name|SegmentInfo
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
name|store
operator|.
name|AlreadyClosedException
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
name|CompoundFileDirectory
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
name|IOUtils
import|;
end_import
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
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Class responsible for access to stored document fields.  *<p/>  * It uses&lt;segment&gt;.fdt and&lt;segment&gt;.fdx; files.  *   * @deprecated  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene3xStoredFieldsReader
specifier|final
class|class
name|Lucene3xStoredFieldsReader
extends|extends
name|StoredFieldsReader
implements|implements
name|Cloneable
implements|,
name|Closeable
block|{
DECL|field|FORMAT_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|FORMAT_SIZE
init|=
literal|4
decl_stmt|;
comment|/** Extension of stored fields file */
DECL|field|FIELDS_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS_EXTENSION
init|=
literal|"fdt"
decl_stmt|;
comment|/** Extension of stored fields index file */
DECL|field|FIELDS_INDEX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS_INDEX_EXTENSION
init|=
literal|"fdx"
decl_stmt|;
comment|// Lucene 3.0: Removal of compressed fields
DECL|field|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
specifier|static
specifier|final
name|int
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
init|=
literal|2
decl_stmt|;
comment|// Lucene 3.2: NumericFields are stored in binary format
DECL|field|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
specifier|static
specifier|final
name|int
name|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
init|=
literal|3
decl_stmt|;
comment|// NOTE: if you introduce a new format, make it 1 higher
comment|// than the current one, and always change this if you
comment|// switch to a new format!
DECL|field|FORMAT_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
decl_stmt|;
comment|// when removing support for old versions, leave the last supported version here
DECL|field|FORMAT_MINIMUM
specifier|static
specifier|final
name|int
name|FORMAT_MINIMUM
init|=
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
decl_stmt|;
comment|// NOTE: bit 0 is free here!  You can steal it!
DECL|field|FIELD_IS_BINARY
specifier|public
specifier|static
specifier|final
name|int
name|FIELD_IS_BINARY
init|=
literal|1
operator|<<
literal|1
decl_stmt|;
comment|// the old bit 1<< 2 was compressed, is now left out
DECL|field|_NUMERIC_BIT_SHIFT
specifier|private
specifier|static
specifier|final
name|int
name|_NUMERIC_BIT_SHIFT
init|=
literal|3
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_MASK
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_MASK
init|=
literal|0x07
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_INT
specifier|public
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_INT
init|=
literal|1
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_LONG
specifier|public
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_LONG
init|=
literal|2
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_FLOAT
specifier|public
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_FLOAT
init|=
literal|3
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_DOUBLE
specifier|public
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_DOUBLE
init|=
literal|4
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsStream
specifier|private
specifier|final
name|IndexInput
name|fieldsStream
decl_stmt|;
DECL|field|indexStream
specifier|private
specifier|final
name|IndexInput
name|indexStream
decl_stmt|;
DECL|field|numTotalDocs
specifier|private
name|int
name|numTotalDocs
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|int
name|format
decl_stmt|;
comment|// The docID offset where our docs begin in the index
comment|// file.  This will be 0 if we have our own private file.
DECL|field|docStoreOffset
specifier|private
name|int
name|docStoreOffset
decl_stmt|;
comment|// when we are inside a compound share doc store (CFX),
comment|// (lucene 3.0 indexes only), we privately open our own fd.
DECL|field|storeCFSReader
specifier|private
specifier|final
name|CompoundFileDirectory
name|storeCFSReader
decl_stmt|;
comment|/** Returns a cloned FieldsReader that shares open    *  IndexInputs with the original one.  It is the caller's    *  job not to close the original FieldsReader until all    *  clones are called (eg, currently SegmentReader manages    *  this logic). */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Lucene3xStoredFieldsReader
name|clone
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|Lucene3xStoredFieldsReader
argument_list|(
name|fieldInfos
argument_list|,
name|numTotalDocs
argument_list|,
name|size
argument_list|,
name|format
argument_list|,
name|docStoreOffset
argument_list|,
operator|(
name|IndexInput
operator|)
name|fieldsStream
operator|.
name|clone
argument_list|()
argument_list|,
operator|(
name|IndexInput
operator|)
name|indexStream
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
comment|/** Verifies that the code version which wrote the segment is supported. */
DECL|method|checkCodeVersion
specifier|public
specifier|static
name|void
name|checkCodeVersion
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segment
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|indexStreamFN
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|idxStream
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|indexStreamFN
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|format
init|=
name|idxStream
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|<
name|FORMAT_MINIMUM
condition|)
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|idxStream
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|FORMAT_CURRENT
argument_list|)
throw|;
if|if
condition|(
name|format
operator|>
name|FORMAT_CURRENT
condition|)
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|idxStream
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|FORMAT_CURRENT
argument_list|)
throw|;
block|}
finally|finally
block|{
name|idxStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Used only by clone
DECL|method|Lucene3xStoredFieldsReader
specifier|private
name|Lucene3xStoredFieldsReader
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|numTotalDocs
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|format
parameter_list|,
name|int
name|docStoreOffset
parameter_list|,
name|IndexInput
name|fieldsStream
parameter_list|,
name|IndexInput
name|indexStream
parameter_list|)
block|{
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|numTotalDocs
operator|=
name|numTotalDocs
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|this
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|this
operator|.
name|fieldsStream
operator|=
name|fieldsStream
expr_stmt|;
name|this
operator|.
name|indexStream
operator|=
name|indexStream
expr_stmt|;
name|this
operator|.
name|storeCFSReader
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|Lucene3xStoredFieldsReader
specifier|public
name|Lucene3xStoredFieldsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|segment
init|=
name|si
operator|.
name|getDocStoreSegment
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docStoreOffset
init|=
name|si
operator|.
name|getDocStoreOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|si
operator|.
name|docCount
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|fieldInfos
operator|=
name|fn
expr_stmt|;
try|try
block|{
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
operator|&&
name|si
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
condition|)
block|{
name|d
operator|=
name|storeCFSReader
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|si
operator|.
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene3xCodec
operator|.
name|COMPOUND_FILE_STORE_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|storeCFSReader
operator|=
literal|null
expr_stmt|;
block|}
name|fieldsStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
specifier|final
name|String
name|indexStreamFN
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|indexStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|indexStreamFN
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|format
operator|=
name|indexStream
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|format
operator|<
name|FORMAT_MINIMUM
condition|)
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|indexStream
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|FORMAT_CURRENT
argument_list|)
throw|;
if|if
condition|(
name|format
operator|>
name|FORMAT_CURRENT
condition|)
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|indexStream
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|FORMAT_CURRENT
argument_list|)
throw|;
specifier|final
name|long
name|indexSize
init|=
name|indexStream
operator|.
name|length
argument_list|()
operator|-
name|FORMAT_SIZE
decl_stmt|;
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// We read only a slice out of this shared fields file
name|this
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
comment|// Verify the file is long enough to hold all of our
comment|// docs
assert|assert
operator|(
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|/
literal|8
argument_list|)
operator|)
operator|>=
name|size
operator|+
name|this
operator|.
name|docStoreOffset
operator|:
literal|"indexSize="
operator|+
name|indexSize
operator|+
literal|" size="
operator|+
name|size
operator|+
literal|" docStoreOffset="
operator|+
name|docStoreOffset
assert|;
block|}
else|else
block|{
name|this
operator|.
name|docStoreOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|size
operator|=
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|>>
literal|3
argument_list|)
expr_stmt|;
comment|// Verify two sources of "maxDoc" agree:
if|if
condition|(
name|this
operator|.
name|size
operator|!=
name|si
operator|.
name|docCount
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"doc counts differ for segment "
operator|+
name|segment
operator|+
literal|": fieldsReader shows "
operator|+
name|this
operator|.
name|size
operator|+
literal|" but segmentInfo shows "
operator|+
name|si
operator|.
name|docCount
argument_list|)
throw|;
block|}
block|}
name|numTotalDocs
operator|=
call|(
name|int
call|)
argument_list|(
name|indexSize
operator|>>
literal|3
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
comment|// With lock-less commits, it's entirely possible (and
comment|// fine) to hit a FileNotFound exception above. In
comment|// this case, we want to explicitly close any subset
comment|// of things that were opened so that we don't have to
comment|// wait for a GC to do so.
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @throws AlreadyClosedException if this FieldsReader is closed    */
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this FieldsReader is closed"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Closes the underlying {@link org.apache.lucene.store.IndexInput} streams.    * This means that the Fields values will not be accessible.    *    * @throws IOException    */
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fieldsStream
argument_list|,
name|indexStream
argument_list|,
name|storeCFSReader
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|seekIndex
specifier|private
name|void
name|seekIndex
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|seek
argument_list|(
name|FORMAT_SIZE
operator|+
operator|(
name|docID
operator|+
name|docStoreOffset
operator|)
operator|*
literal|8L
argument_list|)
expr_stmt|;
block|}
DECL|method|visitDocument
specifier|public
specifier|final
name|void
name|visitDocument
parameter_list|(
name|int
name|n
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|seekIndex
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|indexStream
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
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
name|fieldIDX
init|=
literal|0
init|;
name|fieldIDX
operator|<
name|numFields
condition|;
name|fieldIDX
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
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
name|int
name|bits
init|=
name|fieldsStream
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
decl_stmt|;
assert|assert
name|bits
operator|<=
operator|(
name|FIELD_IS_NUMERIC_MASK
operator||
name|FIELD_IS_BINARY
operator|)
operator|:
literal|"bits="
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
assert|;
switch|switch
condition|(
name|visitor
operator|.
name|needsField
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
name|readField
argument_list|(
name|visitor
argument_list|,
name|fieldInfo
argument_list|,
name|bits
argument_list|)
expr_stmt|;
break|break;
case|case
name|NO
case|:
name|skipField
argument_list|(
name|bits
argument_list|)
expr_stmt|;
break|break;
case|case
name|STOP
case|:
return|return;
block|}
block|}
block|}
DECL|method|readField
specifier|private
name|void
name|readField
parameter_list|(
name|StoredFieldVisitor
name|visitor
parameter_list|,
name|FieldInfo
name|info
parameter_list|,
name|int
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numeric
init|=
name|bits
operator|&
name|FIELD_IS_NUMERIC_MASK
decl_stmt|;
if|if
condition|(
name|numeric
operator|!=
literal|0
condition|)
block|{
switch|switch
condition|(
name|numeric
condition|)
block|{
case|case
name|FIELD_IS_NUMERIC_INT
case|:
name|visitor
operator|.
name|intField
argument_list|(
name|info
argument_list|,
name|fieldsStream
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
return|return;
case|case
name|FIELD_IS_NUMERIC_LONG
case|:
name|visitor
operator|.
name|longField
argument_list|(
name|info
argument_list|,
name|fieldsStream
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
return|return;
case|case
name|FIELD_IS_NUMERIC_FLOAT
case|:
name|visitor
operator|.
name|floatField
argument_list|(
name|info
argument_list|,
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|fieldsStream
operator|.
name|readInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
case|case
name|FIELD_IS_NUMERIC_DOUBLE
case|:
name|visitor
operator|.
name|doubleField
argument_list|(
name|info
argument_list|,
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|fieldsStream
operator|.
name|readLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid numeric type: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|numeric
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|length
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|fieldsStream
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|bits
operator|&
name|FIELD_IS_BINARY
operator|)
operator|!=
literal|0
condition|)
block|{
name|visitor
operator|.
name|binaryField
argument_list|(
name|info
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|visitor
operator|.
name|stringField
argument_list|(
name|info
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|skipField
specifier|private
name|void
name|skipField
parameter_list|(
name|int
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numeric
init|=
name|bits
operator|&
name|FIELD_IS_NUMERIC_MASK
decl_stmt|;
if|if
condition|(
name|numeric
operator|!=
literal|0
condition|)
block|{
switch|switch
condition|(
name|numeric
condition|)
block|{
case|case
name|FIELD_IS_NUMERIC_INT
case|:
case|case
name|FIELD_IS_NUMERIC_FLOAT
case|:
name|fieldsStream
operator|.
name|readInt
argument_list|()
expr_stmt|;
return|return;
case|case
name|FIELD_IS_NUMERIC_LONG
case|:
case|case
name|FIELD_IS_NUMERIC_DOUBLE
case|:
name|fieldsStream
operator|.
name|readLong
argument_list|()
expr_stmt|;
return|return;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid numeric type: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|numeric
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|length
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
operator|+
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|// note: if there are shared docstores, we are also called by Lucene3xCodec even in
comment|// the CFS case. so logic here must handle this.
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|info
operator|.
name|getDocStoreOffset
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
assert|assert
name|info
operator|.
name|getDocStoreSegment
argument_list|()
operator|!=
literal|null
assert|;
if|if
condition|(
name|info
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|Lucene3xCodec
operator|.
name|COMPOUND_FILE_STORE_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|info
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
