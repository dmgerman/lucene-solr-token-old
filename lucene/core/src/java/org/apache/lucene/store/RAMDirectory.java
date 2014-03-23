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
name|IOException
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
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import
begin_comment
comment|/**  * A memory-resident {@link Directory} implementation.  Locking  * implementation is by default the {@link SingleInstanceLockFactory}  * but can be changed with {@link #setLockFactory}.  *   *<p><b>Warning:</b> This class is not intended to work with huge  * indexes. Everything beyond several hundred megabytes will waste  * resources (GC cycles), because it uses an internal buffer size  * of 1024 bytes, producing millions of {@code byte[1024]} arrays.  * This class is optimized for small memory-resident indexes.  * It also has bad concurrency on multithreaded environments.  *   *<p>It is recommended to materialize large indexes on disk and use  * {@link MMapDirectory}, which is a high-performance directory  * implementation working directly on the file system cache of the  * operating system, so copying data to Java heap space is not useful.  */
end_comment
begin_class
DECL|class|RAMDirectory
specifier|public
class|class
name|RAMDirectory
extends|extends
name|BaseDirectory
block|{
DECL|field|fileMap
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RAMFile
argument_list|>
name|fileMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|sizeInBytes
specifier|protected
specifier|final
name|AtomicLong
name|sizeInBytes
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// *****
comment|// Lock acquisition sequence:  RAMDirectory, then RAMFile
comment|// *****
comment|/** Constructs an empty {@link Directory}. */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|()
block|{
try|try
block|{
name|setLockFactory
argument_list|(
operator|new
name|SingleInstanceLockFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Cannot happen
block|}
block|}
comment|/**    * Creates a new<code>RAMDirectory</code> instance from a different    *<code>Directory</code> implementation.  This can be used to load    * a disk-based index into memory.    *     *<p><b>Warning:</b> This class is not intended to work with huge    * indexes. Everything beyond several hundred megabytes will waste    * resources (GC cycles), because it uses an internal buffer size    * of 1024 bytes, producing millions of {@code byte[1024]} arrays.    * This class is optimized for small memory-resident indexes.    * It also has bad concurrency on multithreaded environments.    *     *<p>For disk-based indexes it is recommended to use    * {@link MMapDirectory}, which is a high-performance directory    * implementation working directly on the file system cache of the    * operating system, so copying data to Java heap space is not useful.    *     *<p>Note that the resulting<code>RAMDirectory</code> instance is fully    * independent from the original<code>Directory</code> (it is a    * complete copy).  Any subsequent changes to the    * original<code>Directory</code> will not be visible in the    *<code>RAMDirectory</code> instance.    *    * @param dir a<code>Directory</code> value    * @exception IOException if an error occurs    */
DECL|method|RAMDirectory
specifier|public
name|RAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|RAMDirectory
specifier|private
name|RAMDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|boolean
name|closeDir
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|dir
operator|.
name|copy
argument_list|(
name|this
argument_list|,
name|file
argument_list|,
name|file
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|closeDir
condition|)
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
return|return
literal|"lucene-"
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
annotation|@
name|Override
DECL|method|listAll
specifier|public
specifier|final
name|String
index|[]
name|listAll
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// NOTE: fileMap.keySet().toArray(new String[0]) is broken in non Sun JDKs,
comment|// and the code below is resilient to map changes during the array population.
name|Set
argument_list|<
name|String
argument_list|>
name|fileNames
init|=
name|fileMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fileNames
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|fileNames
control|)
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|names
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|names
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|fileNameExists
specifier|public
specifier|final
name|boolean
name|fileNameExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fileMap
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Returns the length in bytes of a file in the directory.    * @throws IOException if the file does not exist    */
annotation|@
name|Override
DECL|method|fileLength
specifier|public
specifier|final
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
name|file
operator|.
name|getLength
argument_list|()
return|;
block|}
comment|/**    * Return total size in bytes of all files in this directory. This is    * currently quantized to RAMOutputStream.BUFFER_SIZE.    */
DECL|method|sizeInBytes
specifier|public
specifier|final
name|long
name|sizeInBytes
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|sizeInBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Removes an existing file in the directory.    * @throws IOException if the file does not exist    */
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
init|=
name|fileMap
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|file
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
name|sizeInBytes
operator|.
name|addAndGet
argument_list|(
operator|-
name|file
operator|.
name|sizeInBytes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
block|}
comment|/** Creates a new, empty file in the directory with the given name. Returns a stream writing this file. */
annotation|@
name|Override
DECL|method|createOutput
specifier|public
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
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
init|=
name|newRAMFile
argument_list|()
decl_stmt|;
name|RAMFile
name|existing
init|=
name|fileMap
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|sizeInBytes
operator|.
name|addAndGet
argument_list|(
operator|-
name|existing
operator|.
name|sizeInBytes
argument_list|)
expr_stmt|;
name|existing
operator|.
name|directory
operator|=
literal|null
expr_stmt|;
block|}
name|fileMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
operator|new
name|RAMOutputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/**    * Returns a new {@link RAMFile} for storing data. This method can be    * overridden to return different {@link RAMFile} impls, that e.g. override    * {@link RAMFile#newBuffer(int)}.    */
DECL|method|newRAMFile
specifier|protected
name|RAMFile
name|newRAMFile
parameter_list|()
block|{
return|return
operator|new
name|RAMFile
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
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
block|{   }
comment|/** Returns a stream reading an existing file. */
annotation|@
name|Override
DECL|method|openInput
specifier|public
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
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|RAMFile
name|file
init|=
name|fileMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
operator|new
name|RAMInputStream
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
return|;
block|}
comment|/** Closes the store to future operations, releasing associated memory. */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|fileMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
