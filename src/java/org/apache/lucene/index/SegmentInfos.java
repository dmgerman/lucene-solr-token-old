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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
comment|/** This format adds details used for lockless commits.  It differs    * slightly from the previous format in that file names    * are never re-used (write once).  Instead, each file is    * written to the next generation.  For example,    * segments_1, segments_2, etc.  This allows us to not use    * a commit lock.  See<a    * href="http://lucene.apache.org/java/docs/fileformats.html">file    * formats</a> for details.    */
DECL|field|FORMAT_LOCKLESS
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_LOCKLESS
init|=
operator|-
literal|2
decl_stmt|;
comment|/** This is the current file format written.  It adds a    * "hasSingleNormFile" flag into each segment info.    * See<a href="http://issues.apache.org/jira/browse/LUCENE-756">LUCENE-756</a>    * for details.    */
DECL|field|FORMAT_SINGLE_NORM_FILE
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_SINGLE_NORM_FILE
init|=
operator|-
literal|3
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
DECL|field|generation
specifier|private
name|long
name|generation
init|=
literal|0
decl_stmt|;
comment|// generation of the "segments_N" for the next commit
DECL|field|lastGeneration
specifier|private
name|long
name|lastGeneration
init|=
literal|0
decl_stmt|;
comment|// generation of the "segments_N" file we last successfully read
comment|// or wrote; this is normally the same as generation except if
comment|// there was an IOException that had interrupted a commit
comment|/**    * If non-null, information about loading segments_N files    * will be printed here.  @see #setInfoStream.    */
DECL|field|infoStream
specifier|private
specifier|static
name|PrintStream
name|infoStream
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
comment|/**    * Get the generation (N) of the current segments_N file    * from a list of files.    *    * @param files -- array of file names to check    */
DECL|method|getCurrentSegmentGeneration
specifier|public
specifier|static
name|long
name|getCurrentSegmentGeneration
parameter_list|(
name|String
index|[]
name|files
parameter_list|)
block|{
if|if
condition|(
name|files
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|long
name|max
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|prefixLen
init|=
name|IndexFileNames
operator|.
name|SEGMENTS
operator|.
name|length
argument_list|()
operator|+
literal|1
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
operator|&&
operator|!
name|file
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
condition|)
block|{
comment|// Pre lock-less commits:
if|if
condition|(
name|max
operator|==
operator|-
literal|1
condition|)
block|{
name|max
operator|=
literal|0
expr_stmt|;
block|}
block|}
else|else
block|{
name|long
name|v
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|file
operator|.
name|substring
argument_list|(
name|prefixLen
argument_list|)
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|v
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|max
return|;
block|}
comment|/**    * Get the generation (N) of the current segments_N file    * in the directory.    *    * @param directory -- directory to search for the latest segments_N file    */
DECL|method|getCurrentSegmentGeneration
specifier|public
specifier|static
name|long
name|getCurrentSegmentGeneration
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"cannot read directory "
operator|+
name|directory
operator|+
literal|": list() returned null"
argument_list|)
throw|;
return|return
name|getCurrentSegmentGeneration
argument_list|(
name|files
argument_list|)
return|;
block|}
comment|/**    * Get the filename of the current segments_N file    * from a list of files.    *    * @param files -- array of file names to check    */
DECL|method|getCurrentSegmentFileName
specifier|public
specifier|static
name|String
name|getCurrentSegmentFileName
parameter_list|(
name|String
index|[]
name|files
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|,
literal|""
argument_list|,
name|getCurrentSegmentGeneration
argument_list|(
name|files
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the filename of the current segments_N file    * in the directory.    *    * @param directory -- directory to search for the latest segments_N file    */
DECL|method|getCurrentSegmentFileName
specifier|public
specifier|static
name|String
name|getCurrentSegmentFileName
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|,
literal|""
argument_list|,
name|getCurrentSegmentGeneration
argument_list|(
name|directory
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the segments_N filename in use by this segment infos.    */
DECL|method|getCurrentSegmentFileName
specifier|public
name|String
name|getCurrentSegmentFileName
parameter_list|()
block|{
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|,
literal|""
argument_list|,
name|lastGeneration
argument_list|)
return|;
block|}
comment|/**    * Get the next segments_N filename that will be written.    */
DECL|method|getNextSegmentFileName
specifier|public
name|String
name|getNextSegmentFileName
parameter_list|()
block|{
name|long
name|nextGeneration
decl_stmt|;
if|if
condition|(
name|generation
operator|==
operator|-
literal|1
condition|)
block|{
name|nextGeneration
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|nextGeneration
operator|=
name|generation
operator|+
literal|1
expr_stmt|;
block|}
return|return
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|,
literal|""
argument_list|,
name|nextGeneration
argument_list|)
return|;
block|}
comment|/**    * Read a particular segmentFileName.  Note that this may    * throw an IOException if a commit is in process.    *    * @param directory -- directory containing the segments file    * @param segmentFileName -- segment file to load    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|read
specifier|public
specifier|final
name|void
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|segmentFileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentFileName
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
condition|)
block|{
name|generation
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|generation
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|segmentFileName
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|IndexFileNames
operator|.
name|SEGMENTS
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
expr_stmt|;
block|}
name|lastGeneration
operator|=
name|generation
expr_stmt|;
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
name|FORMAT_SINGLE_NORM_FILE
condition|)
throw|throw
operator|new
name|CorruptIndexException
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
name|addElement
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|directory
argument_list|,
name|format
argument_list|,
name|input
argument_list|)
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
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// Clear any segment infos we had loaded so we
comment|// have a clean slate on retry:
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * This version of read uses the retry logic (for lock-less    * commits) to find the right segments file to load.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
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
name|CorruptIndexException
throws|,
name|IOException
block|{
name|generation
operator|=
name|lastGeneration
operator|=
operator|-
literal|1
expr_stmt|;
operator|new
name|FindSegmentsFile
argument_list|(
name|directory
argument_list|)
block|{
specifier|protected
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|read
argument_list|(
name|directory
argument_list|,
name|segmentFileName
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
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
name|String
name|segmentFileName
init|=
name|getNextSegmentFileName
argument_list|()
decl_stmt|;
comment|// Always advance the generation on write:
if|if
condition|(
name|generation
operator|==
operator|-
literal|1
condition|)
block|{
name|generation
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|generation
operator|++
expr_stmt|;
block|}
name|IndexOutput
name|output
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|segmentFileName
argument_list|)
decl_stmt|;
try|try
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|FORMAT_SINGLE_NORM_FILE
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
comment|// every write changes
comment|// the index
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
name|info
argument_list|(
name|i
argument_list|)
operator|.
name|write
argument_list|(
name|output
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
try|try
block|{
name|output
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
expr_stmt|;
try|try
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|FORMAT_LOCKLESS
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|generation
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|generation
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// It's OK if we fail to write this file since it's
comment|// used only as one of the retry fallbacks.
block|}
name|lastGeneration
operator|=
name|generation
expr_stmt|;
block|}
comment|/**    * Returns a copy of this instance, also copying each    * SegmentInfo.    */
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|SegmentInfos
name|sis
init|=
operator|(
name|SegmentInfos
operator|)
name|super
operator|.
name|clone
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
name|sis
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sis
operator|.
name|setElementAt
argument_list|(
operator|(
operator|(
name|SegmentInfo
operator|)
name|sis
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|clone
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|sis
return|;
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
comment|/**    * Current version number from segments file.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
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
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
operator|(
operator|(
name|Long
operator|)
operator|new
name|FindSegmentsFile
argument_list|(
name|directory
argument_list|)
block|{
specifier|protected
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|segmentFileName
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
name|FORMAT_SINGLE_NORM_FILE
condition|)
throw|throw
operator|new
name|CorruptIndexException
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
operator|new
name|Long
argument_list|(
name|version
argument_list|)
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
argument_list|,
name|segmentFileName
argument_list|)
expr_stmt|;
return|return
operator|new
name|Long
argument_list|(
name|sis
operator|.
name|getVersion
argument_list|()
argument_list|)
return|;
block|}
block|}
operator|.
name|run
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/** If non-null, information about retries when loading    * the segments file will be printed to this.    */
DECL|method|setInfoStream
specifier|public
specifier|static
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
name|SegmentInfos
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
comment|/* Advanced configuration of retry logic in loading      segments_N file */
DECL|field|defaultGenFileRetryCount
specifier|private
specifier|static
name|int
name|defaultGenFileRetryCount
init|=
literal|10
decl_stmt|;
DECL|field|defaultGenFileRetryPauseMsec
specifier|private
specifier|static
name|int
name|defaultGenFileRetryPauseMsec
init|=
literal|50
decl_stmt|;
DECL|field|defaultGenLookaheadCount
specifier|private
specifier|static
name|int
name|defaultGenLookaheadCount
init|=
literal|10
decl_stmt|;
comment|/**    * Advanced: set how many times to try loading the    * segments.gen file contents to determine current segment    * generation.  This file is only referenced when the    * primary method (listing the directory) fails.    */
DECL|method|setDefaultGenFileRetryCount
specifier|public
specifier|static
name|void
name|setDefaultGenFileRetryCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|defaultGenFileRetryCount
operator|=
name|count
expr_stmt|;
block|}
comment|/**    * @see #setDefaultGenFileRetryCount    */
DECL|method|getDefaultGenFileRetryCount
specifier|public
specifier|static
name|int
name|getDefaultGenFileRetryCount
parameter_list|()
block|{
return|return
name|defaultGenFileRetryCount
return|;
block|}
comment|/**    * Advanced: set how many milliseconds to pause in between    * attempts to load the segments.gen file.    */
DECL|method|setDefaultGenFileRetryPauseMsec
specifier|public
specifier|static
name|void
name|setDefaultGenFileRetryPauseMsec
parameter_list|(
name|int
name|msec
parameter_list|)
block|{
name|defaultGenFileRetryPauseMsec
operator|=
name|msec
expr_stmt|;
block|}
comment|/**    * @see #setDefaultGenFileRetryPauseMsec    */
DECL|method|getDefaultGenFileRetryPauseMsec
specifier|public
specifier|static
name|int
name|getDefaultGenFileRetryPauseMsec
parameter_list|()
block|{
return|return
name|defaultGenFileRetryPauseMsec
return|;
block|}
comment|/**    * Advanced: set how many times to try incrementing the    * gen when loading the segments file.  This only runs if    * the primary (listing directory) and secondary (opening    * segments.gen file) methods fail to find the segments    * file.    */
DECL|method|setDefaultGenLookaheadCount
specifier|public
specifier|static
name|void
name|setDefaultGenLookaheadCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|defaultGenLookaheadCount
operator|=
name|count
expr_stmt|;
block|}
comment|/**    * @see #setDefaultGenLookaheadCount    */
DECL|method|getDefaultGenLookahedCount
specifier|public
specifier|static
name|int
name|getDefaultGenLookahedCount
parameter_list|()
block|{
return|return
name|defaultGenLookaheadCount
return|;
block|}
comment|/**    * @see #setInfoStream    */
DECL|method|getInfoStream
specifier|public
specifier|static
name|PrintStream
name|getInfoStream
parameter_list|()
block|{
return|return
name|infoStream
return|;
block|}
DECL|method|message
specifier|private
specifier|static
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Utility class for executing code that needs to do    * something with the current segments file.  This is    * necessary with lock-less commits because from the time    * you locate the current segments file name, until you    * actually open it, read its contents, or check modified    * time, etc., it could have been deleted due to a writer    * commit finishing.    */
DECL|class|FindSegmentsFile
specifier|public
specifier|abstract
specifier|static
class|class
name|FindSegmentsFile
block|{
DECL|field|fileDirectory
name|File
name|fileDirectory
decl_stmt|;
DECL|field|directory
name|Directory
name|directory
decl_stmt|;
DECL|method|FindSegmentsFile
specifier|public
name|FindSegmentsFile
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
name|this
operator|.
name|fileDirectory
operator|=
name|directory
expr_stmt|;
block|}
DECL|method|FindSegmentsFile
specifier|public
name|FindSegmentsFile
parameter_list|(
name|Directory
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|String
name|segmentFileName
init|=
literal|null
decl_stmt|;
name|long
name|lastGen
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|gen
init|=
literal|0
decl_stmt|;
name|int
name|genLookaheadCount
init|=
literal|0
decl_stmt|;
name|IOException
name|exc
init|=
literal|null
decl_stmt|;
name|boolean
name|retry
init|=
literal|false
decl_stmt|;
name|int
name|method
init|=
literal|0
decl_stmt|;
comment|// Loop until we succeed in calling doBody() without
comment|// hitting an IOException.  An IOException most likely
comment|// means a commit was in process and has finished, in
comment|// the time it took us to load the now-old infos files
comment|// (and segments files).  It's also possible it's a
comment|// true error (corrupt index).  To distinguish these,
comment|// on each retry we must see "forward progress" on
comment|// which generation we are trying to load.  If we
comment|// don't, then the original error is real and we throw
comment|// it.
comment|// We have three methods for determining the current
comment|// generation.  We try each in sequence.
while|while
condition|(
literal|true
condition|)
block|{
comment|// Method 1: list the directory and use the highest
comment|// segments_N file.  This method works well as long
comment|// as there is no stale caching on the directory
comment|// contents:
name|String
index|[]
name|files
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|method
condition|)
block|{
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
name|files
operator|=
name|directory
operator|.
name|list
argument_list|()
expr_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"cannot read directory "
operator|+
name|directory
operator|+
literal|": list() returned null"
argument_list|)
throw|;
block|}
else|else
block|{
name|files
operator|=
name|fileDirectory
operator|.
name|list
argument_list|()
expr_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"cannot read directory "
operator|+
name|fileDirectory
operator|+
literal|": list() returned null"
argument_list|)
throw|;
block|}
name|gen
operator|=
name|getCurrentSegmentGeneration
argument_list|(
name|files
argument_list|)
expr_stmt|;
if|if
condition|(
name|gen
operator|==
operator|-
literal|1
condition|)
block|{
name|String
name|s
init|=
literal|""
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|s
operator|+=
literal|" "
operator|+
name|files
index|[
name|i
index|]
expr_stmt|;
block|}
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"no segments* file found in "
operator|+
name|directory
operator|+
literal|": files:"
operator|+
name|s
argument_list|)
throw|;
block|}
block|}
comment|// Method 2 (fallback if Method 1 isn't reliable):
comment|// if the directory listing seems to be stale, then
comment|// try loading the "segments.gen" file.
if|if
condition|(
literal|1
operator|==
name|method
operator|||
operator|(
literal|0
operator|==
name|method
operator|&&
name|lastGen
operator|==
name|gen
operator|&&
name|retry
operator|)
condition|)
block|{
name|method
operator|=
literal|1
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|defaultGenFileRetryCount
condition|;
name|i
operator|++
control|)
block|{
name|IndexInput
name|genInput
init|=
literal|null
decl_stmt|;
try|try
block|{
name|genInput
operator|=
name|directory
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|message
argument_list|(
literal|"segments.gen open: IOException "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|genInput
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|int
name|version
init|=
name|genInput
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|==
name|FORMAT_LOCKLESS
condition|)
block|{
name|long
name|gen0
init|=
name|genInput
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|gen1
init|=
name|genInput
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|message
argument_list|(
literal|"fallback check: "
operator|+
name|gen0
operator|+
literal|"; "
operator|+
name|gen1
argument_list|)
expr_stmt|;
if|if
condition|(
name|gen0
operator|==
name|gen1
condition|)
block|{
comment|// The file is consistent.
if|if
condition|(
name|gen0
operator|>
name|gen
condition|)
block|{
name|message
argument_list|(
literal|"fallback to '"
operator|+
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
operator|+
literal|"' check: now try generation "
operator|+
name|gen0
operator|+
literal|"> "
operator|+
name|gen
argument_list|)
expr_stmt|;
name|gen
operator|=
name|gen0
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|err2
parameter_list|)
block|{
comment|// will retry
block|}
finally|finally
block|{
name|genInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|defaultGenFileRetryPauseMsec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// will retry
block|}
block|}
block|}
comment|// Method 3 (fallback if Methods 2& 3 are not
comment|// reliable): since both directory cache and file
comment|// contents cache seem to be stale, just advance the
comment|// generation.
if|if
condition|(
literal|2
operator|==
name|method
operator|||
operator|(
literal|1
operator|==
name|method
operator|&&
name|lastGen
operator|==
name|gen
operator|&&
name|retry
operator|)
condition|)
block|{
name|method
operator|=
literal|2
expr_stmt|;
if|if
condition|(
name|genLookaheadCount
operator|<
name|defaultGenLookaheadCount
condition|)
block|{
name|gen
operator|++
expr_stmt|;
name|genLookaheadCount
operator|++
expr_stmt|;
name|message
argument_list|(
literal|"look ahead increment gen to "
operator|+
name|gen
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastGen
operator|==
name|gen
condition|)
block|{
comment|// This means we're about to try the same
comment|// segments_N last tried.  This is allowed,
comment|// exactly once, because writer could have been in
comment|// the process of writing segments_N last time.
if|if
condition|(
name|retry
condition|)
block|{
comment|// OK, we've tried the same segments_N file
comment|// twice in a row, so this must be a real
comment|// error.  We throw the original exception we
comment|// got.
throw|throw
name|exc
throw|;
block|}
else|else
block|{
name|retry
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Segment file has advanced since our last loop, so
comment|// reset retry:
name|retry
operator|=
literal|false
expr_stmt|;
block|}
name|lastGen
operator|=
name|gen
expr_stmt|;
name|segmentFileName
operator|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|,
literal|""
argument_list|,
name|gen
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|v
init|=
name|doBody
argument_list|(
name|segmentFileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|exc
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"success on "
operator|+
name|segmentFileName
argument_list|)
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
comment|// Save the original root cause:
if|if
condition|(
name|exc
operator|==
literal|null
condition|)
block|{
name|exc
operator|=
name|err
expr_stmt|;
block|}
name|message
argument_list|(
literal|"primary Exception on '"
operator|+
name|segmentFileName
operator|+
literal|"': "
operator|+
name|err
operator|+
literal|"'; will retry: retry="
operator|+
name|retry
operator|+
literal|"; gen = "
operator|+
name|gen
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|retry
operator|&&
name|gen
operator|>
literal|1
condition|)
block|{
comment|// This is our first time trying this segments
comment|// file (because retry is false), and, there is
comment|// possibly a segments_(N-1) (because gen> 1).
comment|// So, check if the segments_(N-1) exists and
comment|// try it if so:
name|String
name|prevSegmentFileName
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|,
literal|""
argument_list|,
name|gen
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|prevSegmentFileName
argument_list|)
condition|)
block|{
name|message
argument_list|(
literal|"fallback to prior segment file '"
operator|+
name|prevSegmentFileName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|v
init|=
name|doBody
argument_list|(
name|prevSegmentFileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|exc
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"success on fallback "
operator|+
name|prevSegmentFileName
argument_list|)
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err2
parameter_list|)
block|{
name|message
argument_list|(
literal|"secondary Exception on '"
operator|+
name|prevSegmentFileName
operator|+
literal|"': "
operator|+
name|err2
operator|+
literal|"'; will retry"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**      * Subclass must implement this.  The assumption is an      * IOException will be thrown if something goes wrong      * during the processing that could have been caused by      * a writer committing.      */
DECL|method|doBody
specifier|protected
specifier|abstract
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
block|}
block|}
end_class
end_unit
