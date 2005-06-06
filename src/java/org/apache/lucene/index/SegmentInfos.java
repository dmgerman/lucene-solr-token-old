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
name|util
operator|.
name|Vector
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
name|Constants
import|;
end_import
begin_class
DECL|class|SegmentInfos
specifier|final
class|class
name|SegmentInfos
extends|extends
name|Vector
block|{
comment|/** The file format version, a negative number. */
comment|/* Works since counter, the old 1st entry, is always>= 0 */
DECL|field|FORMAT
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|counter
specifier|public
name|int
name|counter
init|=
literal|0
decl_stmt|;
comment|// used to name new segments
comment|/**    * counts how often the index has been changed by adding or deleting docs.    * starting with the current time in milliseconds forces to create unique version numbers.    */
DECL|field|version
specifier|private
name|long
name|version
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|info
specifier|public
specifier|final
name|SegmentInfo
name|info
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|SegmentInfo
operator|)
name|elementAt
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
specifier|final
name|void
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|Constants
operator|.
name|INDEX_SEGMENTS_FILENAME
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|format
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|<
literal|0
condition|)
block|{
comment|// file contains explicit format info
comment|// check that it is a format we can understand
if|if
condition|(
name|format
operator|<
name|FORMAT
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown format version: "
operator|+
name|format
argument_list|)
throw|;
name|version
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// read version
name|counter
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read counter
block|}
else|else
block|{
comment|// file is in old format without explicit format info
name|counter
operator|=
name|format
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|input
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// read segmentInfos
name|SegmentInfo
name|si
init|=
operator|new
name|SegmentInfo
argument_list|(
name|input
operator|.
name|readString
argument_list|()
argument_list|,
name|input
operator|.
name|readInt
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|addElement
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|>=
literal|0
condition|)
block|{
comment|// in old format the version number may be at the end of the file
if|if
condition|(
name|input
operator|.
name|getFilePointer
argument_list|()
operator|>=
name|input
operator|.
name|length
argument_list|()
condition|)
name|version
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|// old file format without version number
else|else
name|version
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// read version
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|output
init|=
name|directory
operator|.
name|createOutput
argument_list|(
literal|"segments.new"
argument_list|)
decl_stmt|;
try|try
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|FORMAT
argument_list|)
expr_stmt|;
comment|// write FORMAT
name|output
operator|.
name|writeLong
argument_list|(
operator|++
name|version
argument_list|)
expr_stmt|;
comment|// every write changes the index
name|output
operator|.
name|writeInt
argument_list|(
name|counter
argument_list|)
expr_stmt|;
comment|// write counter
name|output
operator|.
name|writeInt
argument_list|(
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// write infos
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentInfo
name|si
init|=
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|si
operator|.
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// install new segment info
name|directory
operator|.
name|renameFile
argument_list|(
literal|"segments.new"
argument_list|,
name|Constants
operator|.
name|INDEX_SEGMENTS_FILENAME
argument_list|)
expr_stmt|;
block|}
comment|/**    * version number when this SegmentInfos was generated.    */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**    * Current version number from segments file.    */
DECL|method|readCurrentVersion
specifier|public
specifier|static
name|long
name|readCurrentVersion
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|Constants
operator|.
name|INDEX_SEGMENTS_FILENAME
argument_list|)
decl_stmt|;
name|int
name|format
init|=
literal|0
decl_stmt|;
name|long
name|version
init|=
literal|0
decl_stmt|;
try|try
block|{
name|format
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|format
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|format
operator|<
name|FORMAT
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown format version: "
operator|+
name|format
argument_list|)
throw|;
name|version
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// read version
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|format
operator|<
literal|0
condition|)
return|return
name|version
return|;
comment|// We cannot be sure about the format of the file.
comment|// Therefore we have to read the whole file and cannot simply seek to the version entry.
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|directory
argument_list|)
expr_stmt|;
return|return
name|sis
operator|.
name|getVersion
argument_list|()
return|;
block|}
block|}
end_class
end_unit
