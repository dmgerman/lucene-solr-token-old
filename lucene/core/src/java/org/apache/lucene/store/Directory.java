begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|NoSuchFileException
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
begin_comment
comment|// for javadocs
end_comment
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
comment|/** A Directory is a flat list of files.  Files may be written once, when they  * are created.  Once a file is created it may only be opened for read, or  * deleted.  Random access is permitted both when reading and writing.  *  *<p> Java's i/o APIs not used directly, but rather all i/o is  * through this API.  This permits things such as:<ul>  *<li> implementation of RAM-based indices;  *<li> implementation indices stored in a database, via JDBC;  *<li> implementation of an index as a single file;  *</ul>  *  * Directory locking is implemented by an instance of {@link  * LockFactory}.  *  */
end_comment
begin_class
DECL|class|Directory
specifier|public
specifier|abstract
class|class
name|Directory
implements|implements
name|Closeable
block|{
comment|/**    * Returns an array of strings, one for each file in the directory.    *     * @throws IOException in case of IO error    */
DECL|method|listAll
specifier|public
specifier|abstract
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Removes an existing file in the directory. */
DECL|method|deleteFile
specifier|public
specifier|abstract
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the length of a file in the directory. This method follows the    * following contract:    *<ul>    *<li>Throws {@link FileNotFoundException} or {@link NoSuchFileException}    * if the file does not exist.    *<li>Returns a value&ge;0 if the file exists, which specifies its length.    *</ul>    *     * @param name the name of the file for which to return the length.    * @throws IOException if there was an IO error while retrieving the file's    *         length.    */
DECL|method|fileLength
specifier|public
specifier|abstract
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Creates a new, empty file in the directory with the given name.       Returns a stream writing this file. */
DECL|method|createOutput
specifier|public
specifier|abstract
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ensure that any writes to these files are moved to    * stable storage.  Lucene uses this to properly commit    * changes to the index, to prevent a machine/OS crash    * from corrupting the index.<br/>    *<br/>    * NOTE: Clients may call this method for same files over    * and over again, so some impls might optimize for that.    * For other impls the operation can be a noop, for various    * reasons.    */
DECL|method|sync
specifier|public
specifier|abstract
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Renames {@code source} to {@code dest} as an atomic operation,    * where {@code dest} does not yet exist in the directory.    *<p>    * Notes: This method is used by IndexWriter to publish commits.    * It is ok if this operation is not truly atomic, for example    * both {@code source} and {@code dest} can be visible temporarily.    * It is just important that the contents of {@code dest} appear    * atomically, or an exception is thrown.    */
DECL|method|renameFile
specifier|public
specifier|abstract
name|void
name|renameFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a stream reading an existing file.    *<p>Throws {@link FileNotFoundException} or {@link NoSuchFileException}    * if the file does not exist.    */
DECL|method|openInput
specifier|public
specifier|abstract
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a stream reading an existing file, computing checksum as it reads */
DECL|method|openChecksumInput
specifier|public
name|ChecksumIndexInput
name|openChecksumInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BufferedChecksumIndexInput
argument_list|(
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
comment|/** Construct a {@link Lock}.    * @param name the name of the lock file    */
DECL|method|makeLock
specifier|public
specifier|abstract
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/** Closes the store. */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|'@'
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|hashCode
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Copies the file<i>src</i> in<i>from</i> to this directory under the new    * file name<i>dest</i>.    *<p>    * If you want to copy the entire source directory to the destination one, you    * can do so like this:    *     *<pre class="prettyprint">    * Directory to; // the directory to copy to    * for (String file : dir.listAll()) {    *   to.copyFrom(dir, file, newFile, IOContext.DEFAULT); // newFile can be either file, or a new name    * }    *</pre>    *<p>    *<b>NOTE:</b> this method does not check whether<i>dest</i> exist and will    * overwrite it if it does.    */
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|Directory
name|from
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|IndexInput
name|is
init|=
name|from
operator|.
name|openInput
argument_list|(
name|src
argument_list|,
name|context
argument_list|)
init|;
name|IndexOutput
name|os
operator|=
name|createOutput
argument_list|(
name|dest
argument_list|,
name|context
argument_list|)
init|)
block|{
name|os
operator|.
name|copyBytes
argument_list|(
name|is
argument_list|,
name|is
operator|.
name|length
argument_list|()
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
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|this
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @throws AlreadyClosedException if this Directory is closed    */
DECL|method|ensureOpen
specifier|protected
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{}
block|}
end_class
end_unit
