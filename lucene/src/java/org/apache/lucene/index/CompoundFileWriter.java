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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|codecs
operator|.
name|MergeState
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Combines multiple files into a single compound file.  * The file format:<br>  *<ul>  *<li>VInt fileCount</li>  *<li>{Directory}  *         fileCount entries with the following structure:</li>  *<ul>  *<li>long dataOffset</li>  *<li>String fileName</li>  *</ul>  *<li>{File Data}  *         fileCount entries with the raw data of the corresponding file</li>  *</ul>  *  * The fileCount integer indicates how many files are contained in this compound  * file. The {directory} that follows has that many entries. Each directory entry  * contains a long pointer to the start of this file's data section, and a String  * with that file's name.  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|CompoundFileWriter
specifier|public
specifier|final
class|class
name|CompoundFileWriter
block|{
DECL|class|FileEntry
specifier|private
specifier|static
specifier|final
class|class
name|FileEntry
block|{
comment|/** source file */
DECL|field|file
name|String
name|file
decl_stmt|;
comment|/** temporary holder for the start of directory entry for this file */
DECL|field|directoryOffset
name|long
name|directoryOffset
decl_stmt|;
comment|/** temporary holder for the start of this file's data section */
DECL|field|dataOffset
name|long
name|dataOffset
decl_stmt|;
comment|/** the directory which contains the file. */
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
block|}
comment|// Before versioning started.
DECL|field|FORMAT_PRE_VERSION
specifier|static
specifier|final
name|int
name|FORMAT_PRE_VERSION
init|=
literal|0
decl_stmt|;
comment|// Segment name is not written in the file names.
DECL|field|FORMAT_NO_SEGMENT_PREFIX
specifier|static
specifier|final
name|int
name|FORMAT_NO_SEGMENT_PREFIX
init|=
operator|-
literal|1
decl_stmt|;
comment|// NOTE: if you introduce a new format, make it 1 lower
comment|// than the current one, and always change this if you
comment|// switch to a new format!
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_NO_SEGMENT_PREFIX
decl_stmt|;
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|field|ids
specifier|private
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|ids
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|FileEntry
argument_list|>
name|entries
decl_stmt|;
DECL|field|merged
specifier|private
name|boolean
name|merged
init|=
literal|false
decl_stmt|;
DECL|field|checkAbort
specifier|private
specifier|final
name|MergeState
operator|.
name|CheckAbort
name|checkAbort
decl_stmt|;
comment|/** Create the compound stream in the specified file. The file name is the      *  entire name (no extensions are added).      *  @throws NullPointerException if<code>dir</code> or<code>name</code> is null      */
DECL|method|CompoundFileWriter
specifier|public
name|CompoundFileWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|name
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundFileWriter
name|CompoundFileWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|MergeState
operator|.
name|CheckAbort
name|checkAbort
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"directory cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|checkAbort
operator|=
name|checkAbort
expr_stmt|;
name|directory
operator|=
name|dir
expr_stmt|;
name|fileName
operator|=
name|name
expr_stmt|;
name|ids
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|entries
operator|=
operator|new
name|LinkedList
argument_list|<
name|FileEntry
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/** Returns the directory of the compound file. */
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
comment|/** Returns the name of the compound file. */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
comment|/** Add a source stream.<code>file</code> is the string by which the       *  sub-stream will be known in the compound stream.      *       *  @throws IllegalStateException if this writer is closed      *  @throws NullPointerException if<code>file</code> is null      *  @throws IllegalArgumentException if a file with the same name      *   has been added already      */
DECL|method|addFile
specifier|public
name|void
name|addFile
parameter_list|(
name|String
name|file
parameter_list|)
block|{
name|addFile
argument_list|(
name|file
argument_list|,
name|directory
argument_list|)
expr_stmt|;
block|}
comment|/**      * Same as {@link #addFile(String)}, only for files that are found in an      * external {@link Directory}.      */
DECL|method|addFile
specifier|public
name|void
name|addFile
parameter_list|(
name|String
name|file
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
if|if
condition|(
name|merged
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't add extensions after merge has been called"
argument_list|)
throw|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"file cannot be null"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|ids
operator|.
name|add
argument_list|(
name|file
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File "
operator|+
name|file
operator|+
literal|" already added"
argument_list|)
throw|;
name|FileEntry
name|entry
init|=
operator|new
name|FileEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|entry
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
comment|/** Merge files with the extensions added up to now.      *  All files with these extensions are combined sequentially into the      *  compound stream.      *  @throws IllegalStateException if close() had been called before or      *   if no file has been added to this object      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|merged
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Merge already performed"
argument_list|)
throw|;
if|if
condition|(
name|entries
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No entries to merge have been defined"
argument_list|)
throw|;
name|merged
operator|=
literal|true
expr_stmt|;
comment|// open the compound stream
name|IndexOutput
name|os
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|IOException
name|priorException
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Write the Version info - must be a VInt because CFR reads a VInt
comment|// in older versions!
name|os
operator|.
name|writeVInt
argument_list|(
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
comment|// Write the number of entries
name|os
operator|.
name|writeVInt
argument_list|(
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Write the directory with all offsets at 0.
comment|// Remember the positions of directory entries so that we can
comment|// adjust the offsets later
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileEntry
name|fe
range|:
name|entries
control|)
block|{
name|fe
operator|.
name|directoryOffset
operator|=
name|os
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// for now
name|os
operator|.
name|writeString
argument_list|(
name|IndexFileNames
operator|.
name|stripSegmentName
argument_list|(
name|fe
operator|.
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|totalSize
operator|+=
name|fe
operator|.
name|dir
operator|.
name|fileLength
argument_list|(
name|fe
operator|.
name|file
argument_list|)
expr_stmt|;
block|}
comment|// Pre-allocate size of file as optimization --
comment|// this can potentially help IO performance as
comment|// we write the file and also later during
comment|// searching.  It also uncovers a disk-full
comment|// situation earlier and hopefully without
comment|// actually filling disk to 100%:
specifier|final
name|long
name|finalLength
init|=
name|totalSize
operator|+
name|os
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|os
operator|.
name|setLength
argument_list|(
name|finalLength
argument_list|)
expr_stmt|;
comment|// Open the files and copy their data into the stream.
comment|// Remember the locations of each file's data section.
for|for
control|(
name|FileEntry
name|fe
range|:
name|entries
control|)
block|{
name|fe
operator|.
name|dataOffset
operator|=
name|os
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|copyFile
argument_list|(
name|fe
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
comment|// Write the data offsets into the directory of the compound stream
for|for
control|(
name|FileEntry
name|fe
range|:
name|entries
control|)
block|{
name|os
operator|.
name|seek
argument_list|(
name|fe
operator|.
name|directoryOffset
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|fe
operator|.
name|dataOffset
argument_list|)
expr_stmt|;
block|}
assert|assert
name|finalLength
operator|==
name|os
operator|.
name|length
argument_list|()
assert|;
comment|// Close the output stream. Set the os to null before trying to
comment|// close so that if an exception occurs during the close, the
comment|// finally clause below will not attempt to close the stream
comment|// the second time.
name|IndexOutput
name|tmp
init|=
name|os
decl_stmt|;
name|os
operator|=
literal|null
expr_stmt|;
name|tmp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|priorException
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
name|priorException
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Copy the contents of the file with specified extension into the provided    * output stream.    */
DECL|method|copyFile
specifier|private
name|void
name|copyFile
parameter_list|(
name|FileEntry
name|source
parameter_list|,
name|IndexOutput
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|is
init|=
name|source
operator|.
name|dir
operator|.
name|openInput
argument_list|(
name|source
operator|.
name|file
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|startPtr
init|=
name|os
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|is
operator|.
name|length
argument_list|()
decl_stmt|;
name|os
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkAbort
operator|!=
literal|null
condition|)
block|{
name|checkAbort
operator|.
name|work
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Verify that the output length diff is equal to original file
name|long
name|endPtr
init|=
name|os
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|diff
init|=
name|endPtr
operator|-
name|startPtr
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Difference in the output file offsets "
operator|+
name|diff
operator|+
literal|" does not match the original file length "
operator|+
name|length
argument_list|)
throw|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
