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
begin_comment
comment|/** A Directory is a flat list of files.  Files may be written once, when they  * are created.  Once a file is created it may only be opened for read, or  * deleted.  Random access is permitted both when reading and writing.  *  *<p> Java's i/o APIs not used directly, but rather all i/o is  * through this API.  This permits things such as:<ul>  *<li> implementation of RAM-based indices;  *<li> implementation indices stored in a database, via JDBC;  *<li> implementation of an index as a single file;  *</ul>  *  * Directory locking is implemented by an instance of {@link  * LockFactory}, and can be changed for each Directory  * instance using {@link #setLockFactory}.  *  * @author Doug Cutting  */
end_comment
begin_class
DECL|class|Directory
specifier|public
specifier|abstract
class|class
name|Directory
block|{
comment|/** Holds the LockFactory instance (implements locking for    * this Directory instance). */
DECL|field|lockFactory
specifier|protected
name|LockFactory
name|lockFactory
decl_stmt|;
comment|/** Returns an array of strings, one for each file in the    * directory.  This method may return null (for example for    * {@link FSDirectory} if the underlying directory doesn't    * exist in the filesystem or there are permissions    * problems).*/
DECL|method|list
specifier|public
specifier|abstract
name|String
index|[]
name|list
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns true iff a file with the given name exists. */
DECL|method|fileExists
specifier|public
specifier|abstract
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the time the named file was last modified. */
DECL|method|fileModified
specifier|public
specifier|abstract
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Set the modified time of an existing file to now. */
DECL|method|touchFile
specifier|public
specifier|abstract
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
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
comment|/** Renames an existing file in the directory.    * If a file already exists with the new name, then it is replaced.    * This replacement is not guaranteed to be atomic.    * @deprecated     */
DECL|method|renameFile
specifier|public
specifier|abstract
name|void
name|renameFile
parameter_list|(
name|String
name|from
parameter_list|,
name|String
name|to
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the length of a file in the directory. */
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
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a stream reading an existing file. */
DECL|method|openInput
specifier|public
specifier|abstract
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Construct a {@link Lock}.    * @param name the name of the lock file    */
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|lockFactory
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Attempt to clear (forcefully unlock and remove) the    * specified lock.  Only call this at a time when you are    * certain this lock is no longer in use.    * @param name name of the lock to be cleared.    */
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lockFactory
operator|!=
literal|null
condition|)
block|{
name|lockFactory
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Closes the store. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the LockFactory that this Directory instance should    * use for its locking implementation.  Each * instance of    * LockFactory should only be used for one directory (ie,    * do not share a single instance across multiple    * Directories).    *    * @param lockFactory instance of {@link LockFactory}.    */
DECL|method|setLockFactory
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
block|{
name|this
operator|.
name|lockFactory
operator|=
name|lockFactory
expr_stmt|;
name|lockFactory
operator|.
name|setLockPrefix
argument_list|(
name|this
operator|.
name|getLockID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the LockFactory that this Directory instance is    * using for its locking implementation.  Note that this    * may be null for Directory implementations that provide    * their own locking implementation.    */
DECL|method|getLockFactory
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|lockFactory
return|;
block|}
comment|/**    * Return a string identifier that uniquely differentiates    * this Directory instance from other Directory instances.    * This ID should be the same if two Directory instances    * (even in different JVMs and/or on different machines)    * are considered "the same index".  This is how locking    * "scopes" to the right index.    */
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
return|return
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Copy contents of a directory src to a directory dest.    * If a file in src already exists in dest then the    * one in dest will be blindly overwritten.    *    * @param src source directory    * @param dest destination directory    * @param closeDirSrc if<code>true</code>, call {@link #close()} method on source directory    * @throws IOException    */
DECL|method|copy
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|Directory
name|src
parameter_list|,
name|Directory
name|dest
parameter_list|,
name|boolean
name|closeDirSrc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|src
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
name|src
operator|+
literal|": list() returned null"
argument_list|)
throw|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|BufferedIndexOutput
operator|.
name|BUFFER_SIZE
index|]
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
name|IndexOutput
name|os
init|=
literal|null
decl_stmt|;
name|IndexInput
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// create file in dest directory
name|os
operator|=
name|dest
operator|.
name|createOutput
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// read current file
name|is
operator|=
name|src
operator|.
name|openInput
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// and copy to dest directory
name|long
name|len
init|=
name|is
operator|.
name|length
argument_list|()
decl_stmt|;
name|long
name|readCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|readCount
operator|<
name|len
condition|)
block|{
name|int
name|toRead
init|=
name|readCount
operator|+
name|BufferedIndexOutput
operator|.
name|BUFFER_SIZE
operator|>
name|len
condition|?
call|(
name|int
call|)
argument_list|(
name|len
operator|-
name|readCount
argument_list|)
else|:
name|BufferedIndexOutput
operator|.
name|BUFFER_SIZE
decl_stmt|;
name|is
operator|.
name|readBytes
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|buf
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
name|readCount
operator|+=
name|toRead
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// graceful cleanup
try|try
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|closeDirSrc
condition|)
name|src
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
