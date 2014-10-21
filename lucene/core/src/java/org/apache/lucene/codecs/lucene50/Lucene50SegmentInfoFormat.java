begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|Map
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
name|codecs
operator|.
name|SegmentInfoFormat
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
name|IndexWriter
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
name|SegmentInfo
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
name|SegmentInfos
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
name|store
operator|.
name|ChecksumIndexInput
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
name|DataOutput
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
name|IOUtils
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
name|StringHelper
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Lucene 5.0 Segment info format.  *<p>  * Files:  *<ul>  *<li><tt>.si</tt>: Header, SegVersion, SegSize, IsCompoundFile, Diagnostics, Files, Footer  *</ul>  *</p>  * Data types:  *<p>  *<ul>  *<li>Header --&gt; {@link CodecUtil#writeSegmentHeader SegmentHeader}</li>  *<li>SegSize --&gt; {@link DataOutput#writeInt Int32}</li>  *<li>SegVersion --&gt; {@link DataOutput#writeString String}</li>  *<li>Files --&gt; {@link DataOutput#writeStringSet Set&lt;String&gt;}</li>  *<li>Diagnostics --&gt; {@link DataOutput#writeStringStringMap Map&lt;String,String&gt;}</li>  *<li>IsCompoundFile --&gt; {@link DataOutput#writeByte Int8}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *</p>  * Field Descriptions:  *<p>  *<ul>  *<li>SegVersion is the code version that created the segment.</li>  *<li>SegSize is the number of documents contained in the segment index.</li>  *<li>IsCompoundFile records whether the segment is written as a compound file or  *       not. If this is -1, the segment is not a compound file. If it is 1, the segment  *       is a compound file.</li>  *<li>The Diagnostics Map is privately written by {@link IndexWriter}, as a debugging aid,  *       for each segment it creates. It includes metadata like the current Lucene  *       version, OS, Java version, why the segment was created (merge, flush,  *       addIndexes), etc.</li>  *<li>Files is a list of files referred to by this segment.</li>  *</ul>  *</p>  *   * @see SegmentInfos  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene50SegmentInfoFormat
specifier|public
class|class
name|Lucene50SegmentInfoFormat
extends|extends
name|SegmentInfoFormat
block|{
comment|/** Sole constructor. */
DECL|method|Lucene50SegmentInfoFormat
specifier|public
name|Lucene50SegmentInfoFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|read
specifier|public
name|SegmentInfo
name|read
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene50SegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|input
init|=
name|dir
operator|.
name|openChecksumInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
name|SegmentInfo
name|si
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|Lucene50SegmentInfoFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene50SegmentInfoFormat
operator|.
name|VERSION_START
argument_list|,
name|Lucene50SegmentInfoFormat
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|byte
name|id
index|[]
init|=
operator|new
name|byte
index|[
name|StringHelper
operator|.
name|ID_LENGTH
index|]
decl_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|suffix
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|suffix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid codec header: got unexpected suffix: "
operator|+
name|suffix
argument_list|,
name|input
argument_list|)
throw|;
block|}
specifier|final
name|Version
name|version
init|=
name|Version
operator|.
name|fromBits
argument_list|(
name|input
operator|.
name|readInt
argument_list|()
argument_list|,
name|input
operator|.
name|readInt
argument_list|()
argument_list|,
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|docCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docCount: "
operator|+
name|docCount
argument_list|,
name|input
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|isCompoundFile
init|=
name|input
operator|.
name|readByte
argument_list|()
operator|==
name|SegmentInfo
operator|.
name|YES
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|diagnostics
init|=
name|input
operator|.
name|readStringStringMap
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
name|input
operator|.
name|readStringSet
argument_list|()
decl_stmt|;
name|si
operator|=
operator|new
name|SegmentInfo
argument_list|(
name|dir
argument_list|,
name|version
argument_list|,
name|segment
argument_list|,
name|docCount
argument_list|,
name|isCompoundFile
argument_list|,
literal|null
argument_list|,
name|diagnostics
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|si
operator|.
name|setFiles
argument_list|(
name|files
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
return|return
name|si
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|Lucene50SegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
name|si
operator|.
name|addFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|ioContext
argument_list|)
init|)
block|{
comment|// NOTE: we encode ID in the segment header, for format consistency with all other per-segment files
name|CodecUtil
operator|.
name|writeSegmentHeader
argument_list|(
name|output
argument_list|,
name|Lucene50SegmentInfoFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene50SegmentInfoFormat
operator|.
name|VERSION_CURRENT
argument_list|,
name|si
operator|.
name|getId
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Version
name|version
init|=
name|si
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|major
operator|<
literal|5
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid major version: should be>= 5 but got: "
operator|+
name|version
operator|.
name|major
operator|+
literal|" segment="
operator|+
name|si
argument_list|)
throw|;
block|}
comment|// Write the Lucene version that created this segment, since 3.1
name|output
operator|.
name|writeInt
argument_list|(
name|version
operator|.
name|major
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|version
operator|.
name|minor
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|version
operator|.
name|bugfix
argument_list|)
expr_stmt|;
assert|assert
name|version
operator|.
name|prerelease
operator|==
literal|0
assert|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|?
name|SegmentInfo
operator|.
name|YES
else|:
name|SegmentInfo
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringStringMap
argument_list|(
name|si
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|files
init|=
name|si
operator|.
name|files
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
operator|!
name|IndexFileNames
operator|.
name|parseSegmentName
argument_list|(
name|file
argument_list|)
operator|.
name|equals
argument_list|(
name|si
operator|.
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid files: expected segment="
operator|+
name|si
operator|.
name|name
operator|+
literal|", got="
operator|+
name|files
argument_list|)
throw|;
block|}
block|}
name|output
operator|.
name|writeStringSet
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// TODO: are we doing this outside of the tracking wrapper? why must SIWriter cleanup like this?
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|si
operator|.
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** File extension used to store {@link SegmentInfo}. */
DECL|field|SI_EXTENSION
specifier|public
specifier|final
specifier|static
name|String
name|SI_EXTENSION
init|=
literal|"si"
decl_stmt|;
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene50SegmentInfo"
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
block|}
end_class
end_unit
