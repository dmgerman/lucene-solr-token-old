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
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|DoubleBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|FloatBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|CodecUtil
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
name|FloatsRef
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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/**  * Exposes writer/reader for floating point values. You can specify 4 (java  * float) or 8 (java double) byte precision.  */
end_comment
begin_comment
comment|//nocommit - add mmap version
end_comment
begin_comment
comment|//nocommti - add bulk copy where possible
end_comment
begin_class
DECL|class|Floats
specifier|public
class|class
name|Floats
block|{
DECL|field|CODEC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"SimpleFloats"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|INT_ZERO
specifier|private
specifier|static
specifier|final
name|int
name|INT_ZERO
init|=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
DECL|field|LONG_ZERO
specifier|private
specifier|static
specifier|final
name|long
name|LONG_ZERO
init|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
literal|0.0
argument_list|)
decl_stmt|;
DECL|method|getWriter
specifier|public
specifier|static
name|Writer
name|getWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|precisionBytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|precisionBytes
operator|!=
literal|4
operator|&&
name|precisionBytes
operator|!=
literal|8
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionBytes must be 4 or 8; got "
operator|+
name|precisionBytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|precisionBytes
operator|==
literal|4
condition|)
block|{
return|return
operator|new
name|Float4Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Float8Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
DECL|method|getValues
specifier|public
specifier|static
name|DocValues
name|getValues
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FloatsReader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
DECL|class|FloatsWriter
specifier|abstract
specifier|static
class|class
name|FloatsWriter
extends|extends
name|Writer
block|{
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|floatsRef
specifier|private
name|FloatsRef
name|floatsRef
decl_stmt|;
DECL|field|lastDocId
specifier|protected
name|int
name|lastDocId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|datOut
specifier|protected
name|IndexOutput
name|datOut
decl_stmt|;
DECL|field|precision
specifier|private
specifier|final
name|byte
name|precision
decl_stmt|;
DECL|method|FloatsWriter
specifier|protected
name|FloatsWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|precision
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|precision
operator|=
operator|(
name|byte
operator|)
name|precision
expr_stmt|;
block|}
DECL|method|initDatOut
specifier|protected
name|void
name|initDatOut
parameter_list|()
throws|throws
name|IOException
block|{
name|datOut
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|CSF_DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|datOut
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
assert|assert
name|datOut
operator|.
name|getFilePointer
argument_list|()
operator|==
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
assert|;
name|datOut
operator|.
name|writeByte
argument_list|(
name|precision
argument_list|)
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|floatsRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|ValuesAttribute
name|attr
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FloatsRef
name|ref
decl_stmt|;
if|if
condition|(
operator|(
name|ref
operator|=
name|attr
operator|.
name|floats
argument_list|()
operator|)
operator|!=
literal|null
condition|)
name|add
argument_list|(
name|docID
argument_list|,
name|ref
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextAttribute
specifier|protected
name|void
name|setNextAttribute
parameter_list|(
name|ValuesAttribute
name|attr
parameter_list|)
block|{
name|floatsRef
operator|=
name|attr
operator|.
name|floats
argument_list|()
expr_stmt|;
block|}
DECL|method|fillDefault
specifier|protected
specifier|abstract
name|int
name|fillDefault
parameter_list|(
name|int
name|num
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|MergeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|.
name|bits
operator|==
literal|null
operator|&&
name|state
operator|.
name|reader
operator|instanceof
name|FloatsReader
condition|)
block|{
comment|// no deletes - bulk copy
comment|// nocommit - should be do bulks with deletes too?
specifier|final
name|FloatsReader
name|reader
init|=
operator|(
name|FloatsReader
operator|)
name|state
operator|.
name|reader
decl_stmt|;
assert|assert
name|reader
operator|.
name|precisionBytes
operator|==
operator|(
name|int
operator|)
name|precision
assert|;
if|if
condition|(
name|reader
operator|.
name|maxDoc
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
name|initDatOut
argument_list|()
expr_stmt|;
specifier|final
name|int
name|docBase
init|=
name|state
operator|.
name|docBase
decl_stmt|;
if|if
condition|(
name|docBase
operator|-
name|lastDocId
operator|>
literal|1
condition|)
block|{
comment|// fill with default values
name|lastDocId
operator|+=
name|fillDefault
argument_list|(
name|docBase
operator|-
name|lastDocId
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|lastDocId
operator|+=
name|reader
operator|.
name|transferTo
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
else|else
name|super
operator|.
name|merge
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|CSF_DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Writes 4 bytes (float) per value
DECL|class|Float4Writer
specifier|static
class|class
name|Float4Writer
extends|extends
name|FloatsWriter
block|{
DECL|method|Float4Writer
specifier|protected
name|Float4Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|int
name|docID
parameter_list|,
specifier|final
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>
name|lastDocId
operator|:
literal|"docID: "
operator|+
name|docID
operator|+
literal|" must be greater than the last added doc id: "
operator|+
name|lastDocId
assert|;
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
block|{
name|initDatOut
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docID
operator|-
name|lastDocId
operator|>
literal|1
condition|)
block|{
comment|// fill with default values
name|lastDocId
operator|+=
name|fillDefault
argument_list|(
name|docID
operator|-
name|lastDocId
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
assert|assert
name|datOut
operator|!=
literal|null
assert|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
operator|(
name|float
operator|)
name|v
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|lastDocId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|synchronized
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
return|return;
comment|// no data added - don't create file!
if|if
condition|(
name|docCount
operator|>
name|lastDocId
operator|+
literal|1
condition|)
for|for
control|(
name|int
name|i
init|=
name|lastDocId
init|;
name|i
operator|<
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeInt
argument_list|(
name|INT_ZERO
argument_list|)
expr_stmt|;
comment|// default value
block|}
name|datOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillDefault
specifier|protected
name|int
name|fillDefault
parameter_list|(
name|int
name|numValues
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeInt
argument_list|(
name|INT_ZERO
argument_list|)
expr_stmt|;
block|}
return|return
name|numValues
return|;
block|}
block|}
comment|// Writes 8 bytes (double) per value
DECL|class|Float8Writer
specifier|static
class|class
name|Float8Writer
extends|extends
name|FloatsWriter
block|{
DECL|method|Float8Writer
specifier|protected
name|Float8Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>
name|lastDocId
operator|:
literal|"docID: "
operator|+
name|docID
operator|+
literal|" must be greater than the last added doc id: "
operator|+
name|lastDocId
assert|;
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
block|{
name|initDatOut
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docID
operator|-
name|lastDocId
operator|>
literal|1
condition|)
block|{
comment|// fill with default values
name|lastDocId
operator|+=
name|fillDefault
argument_list|(
name|docID
operator|-
name|lastDocId
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
assert|assert
name|datOut
operator|!=
literal|null
assert|;
name|datOut
operator|.
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|lastDocId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|synchronized
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
return|return;
comment|// no data added - don't create file!
if|if
condition|(
name|docCount
operator|>
name|lastDocId
operator|+
literal|1
condition|)
for|for
control|(
name|int
name|i
init|=
name|lastDocId
init|;
name|i
operator|<
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeLong
argument_list|(
name|LONG_ZERO
argument_list|)
expr_stmt|;
comment|// default value
block|}
name|datOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillDefault
specifier|protected
name|int
name|fillDefault
parameter_list|(
name|int
name|numValues
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeLong
argument_list|(
name|LONG_ZERO
argument_list|)
expr_stmt|;
block|}
return|return
name|numValues
return|;
block|}
block|}
comment|/**    * Opens all necessary files, but does not read any data in until you call    * {@link #load}.    */
DECL|class|FloatsReader
specifier|static
class|class
name|FloatsReader
extends|extends
name|DocValues
block|{
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|precisionBytes
specifier|private
specifier|final
name|int
name|precisionBytes
decl_stmt|;
comment|// TODO(simonw) is ByteBuffer the way to go here?
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|FloatsReader
specifier|protected
name|FloatsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|datIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|CSF_DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|datIn
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
name|precisionBytes
operator|=
name|datIn
operator|.
name|readByte
argument_list|()
expr_stmt|;
assert|assert
name|precisionBytes
operator|==
literal|4
operator|||
name|precisionBytes
operator|==
literal|8
assert|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
DECL|method|transferTo
name|int
name|transferTo
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|indexInput
init|=
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
decl_stmt|;
try|try
block|{
name|indexInput
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// skip precision:
name|indexInput
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|out
operator|.
name|copyBytes
argument_list|(
name|indexInput
argument_list|,
name|precisionBytes
operator|*
name|maxDoc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|maxDoc
return|;
block|}
comment|/**      * Loads the actual values. You may call this more than once, eg if you      * already previously loaded but then discarded the Source.      */
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|precisionBytes
operator|*
name|maxDoc
argument_list|)
decl_stmt|;
name|IndexInput
name|indexInput
init|=
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
decl_stmt|;
name|indexInput
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// skip precision:
name|indexInput
operator|.
name|readByte
argument_list|()
expr_stmt|;
assert|assert
name|buffer
operator|.
name|hasArray
argument_list|()
operator|:
literal|"Buffer must support Array"
assert|;
specifier|final
name|byte
index|[]
name|arr
init|=
name|buffer
operator|.
name|array
argument_list|()
decl_stmt|;
name|indexInput
operator|.
name|readBytes
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|precisionBytes
operator|==
literal|4
condition|?
operator|new
name|Source4
argument_list|(
name|buffer
argument_list|)
else|:
operator|new
name|Source8
argument_list|(
name|buffer
argument_list|)
return|;
block|}
DECL|class|Source4
specifier|private
class|class
name|Source4
extends|extends
name|Source
block|{
DECL|field|values
specifier|private
specifier|final
name|FloatBuffer
name|values
decl_stmt|;
DECL|method|Source4
name|Source4
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|values
operator|=
name|buffer
operator|.
name|asFloatBuffer
argument_list|()
expr_stmt|;
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
specifier|final
name|float
name|f
init|=
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// nocommit should we return NaN as default instead of 0.0?
return|return
name|Float
operator|.
name|isNaN
argument_list|(
name|f
argument_list|)
condition|?
literal|0.0f
else|:
name|f
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|values
operator|.
name|limit
argument_list|()
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_FLOAT
return|;
block|}
block|}
DECL|class|Source8
specifier|private
class|class
name|Source8
extends|extends
name|Source
block|{
DECL|field|values
specifier|private
specifier|final
name|DoubleBuffer
name|values
decl_stmt|;
DECL|method|Source8
name|Source8
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|values
operator|=
name|buffer
operator|.
name|asDoubleBuffer
argument_list|()
expr_stmt|;
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
specifier|final
name|double
name|d
init|=
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// TODO should we return NaN as default instead of 0.0?
return|return
name|Double
operator|.
name|isNaN
argument_list|(
name|d
argument_list|)
condition|?
literal|0.0d
else|:
name|d
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|values
operator|.
name|limit
argument_list|()
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_DOUBLE
return|;
block|}
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
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|indexInput
init|=
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
decl_stmt|;
name|indexInput
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// skip precision:
name|indexInput
operator|.
name|readByte
argument_list|()
expr_stmt|;
return|return
name|precisionBytes
operator|==
literal|4
condition|?
operator|new
name|Floats4Enum
argument_list|(
name|source
argument_list|,
name|indexInput
argument_list|,
name|maxDoc
argument_list|)
else|:
operator|new
name|Floats8EnumImpl
argument_list|(
name|source
argument_list|,
name|indexInput
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|precisionBytes
operator|==
literal|4
condition|?
name|Values
operator|.
name|SIMPLE_FLOAT_4BYTE
else|:
name|Values
operator|.
name|SIMPLE_FLOAT_8BYTE
return|;
block|}
block|}
DECL|class|Floats4Enum
specifier|static
specifier|final
class|class
name|Floats4Enum
extends|extends
name|FloatsEnumImpl
block|{
DECL|method|Floats4Enum
name|Floats4Enum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|dataIn
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|dataIn
argument_list|,
literal|4
argument_list|,
name|maxDoc
argument_list|,
name|Values
operator|.
name|SIMPLE_FLOAT_4BYTE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
name|dataIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
operator|(
name|target
operator|*
name|precision
operator|)
argument_list|)
expr_stmt|;
name|ref
operator|.
name|floats
index|[
literal|0
index|]
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|dataIn
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
comment|// nocommit -- can we igore this?
return|return
name|pos
operator|=
name|target
return|;
block|}
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
DECL|class|Floats8EnumImpl
specifier|private
specifier|static
specifier|final
class|class
name|Floats8EnumImpl
extends|extends
name|FloatsEnumImpl
block|{
DECL|method|Floats8EnumImpl
name|Floats8EnumImpl
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|dataIn
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|dataIn
argument_list|,
literal|8
argument_list|,
name|maxDoc
argument_list|,
name|Values
operator|.
name|SIMPLE_FLOAT_8BYTE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
name|dataIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
operator|(
name|target
operator|*
name|precision
operator|)
argument_list|)
expr_stmt|;
name|ref
operator|.
name|floats
index|[
literal|0
index|]
operator|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|dataIn
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
comment|// nocommit -- can we igore this?
return|return
name|pos
operator|=
name|target
return|;
block|}
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
DECL|class|FloatsEnumImpl
specifier|static
specifier|abstract
class|class
name|FloatsEnumImpl
extends|extends
name|ValuesEnum
block|{
DECL|field|dataIn
specifier|protected
specifier|final
name|IndexInput
name|dataIn
decl_stmt|;
DECL|field|pos
specifier|protected
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|precision
specifier|protected
specifier|final
name|int
name|precision
decl_stmt|;
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|fp
specifier|protected
specifier|final
name|long
name|fp
decl_stmt|;
DECL|field|ref
specifier|protected
specifier|final
name|FloatsRef
name|ref
decl_stmt|;
DECL|method|FloatsEnumImpl
name|FloatsEnumImpl
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|dataIn
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|Values
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|precision
operator|==
literal|4
condition|?
name|Values
operator|.
name|SIMPLE_FLOAT_4BYTE
else|:
name|Values
operator|.
name|SIMPLE_FLOAT_8BYTE
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataIn
operator|=
name|dataIn
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|fp
operator|=
name|dataIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|attr
operator|.
name|floats
argument_list|()
expr_stmt|;
name|this
operator|.
name|ref
operator|.
name|offset
operator|=
literal|0
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
block|{
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
