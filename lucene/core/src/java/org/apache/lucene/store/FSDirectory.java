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
name|FilenameFilter
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
name|RandomAccessFile
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
name|HashSet
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
name|Future
import|;
end_import
begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|synchronizedSet
import|;
end_import
begin_comment
comment|/**  * Base class for Directory implementations that store index  * files in the file system.    *<a name="subclasses"/>  * There are currently three core  * subclasses:  *  *<ul>  *  *<li> {@link SimpleFSDirectory} is a straightforward  *       implementation using java.io.RandomAccessFile.  *       However, it has poor concurrent performance  *       (multiple threads will bottleneck) as it  *       synchronizes when multiple threads read from the  *       same file.  *  *<li> {@link NIOFSDirectory} uses java.nio's  *       FileChannel's positional io when reading to avoid  *       synchronization when reading from the same file.  *       Unfortunately, due to a Windows-only<a  *       href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6265734">Sun  *       JRE bug</a> this is a poor choice for Windows, but  *       on all other platforms this is the preferred  *       choice. Applications using {@link Thread#interrupt()} or  *       {@link Future#cancel(boolean)} should use  *       {@link SimpleFSDirectory} instead. See {@link NIOFSDirectory} java doc  *       for details.  *          *          *  *<li> {@link MMapDirectory} uses memory-mapped IO when  *       reading. This is a good choice if you have plenty  *       of virtual memory relative to your index size, eg  *       if you are running on a 64 bit JRE, or you are  *       running on a 32 bit JRE but your index sizes are  *       small enough to fit into the virtual memory space.  *       Java has currently the limitation of not being able to  *       unmap files from user code. The files are unmapped, when GC  *       releases the byte buffers. Due to  *<a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">  *       this bug</a> in Sun's JRE, MMapDirectory's {@link IndexInput#close}  *       is unable to close the underlying OS file handle. Only when  *       GC finally collects the underlying objects, which could be  *       quite some time later, will the file handle be closed.  *       This will consume additional transient disk usage: on Windows,  *       attempts to delete or overwrite the files will result in an  *       exception; on other platforms, which typically have a&quot;delete on  *       last close&quot; semantics, while such operations will succeed, the bytes  *       are still consuming space on disk.  For many applications this  *       limitation is not a problem (e.g. if you have plenty of disk space,  *       and you don't rely on overwriting files on Windows) but it's still  *       an important limitation to be aware of. This class supplies a  *       (possibly dangerous) workaround mentioned in the bug report,  *       which may fail on non-Sun JVMs.  *         *       Applications using {@link Thread#interrupt()} or  *       {@link Future#cancel(boolean)} should use  *       {@link SimpleFSDirectory} instead. See {@link MMapDirectory}  *       java doc for details.  *</ul>  *  * Unfortunately, because of system peculiarities, there is  * no single overall best implementation.  Therefore, we've  * added the {@link #open} method, to allow Lucene to choose  * the best FSDirectory implementation given your  * environment, and the known limitations of each  * implementation.  For users who have no reason to prefer a  * specific implementation, it's best to simply use {@link  * #open}.  For all others, you should instantiate the  * desired implementation directly.  *  *<p>The locking implementation is by default {@link  * NativeFSLockFactory}, but can be changed by  * passing in a custom {@link LockFactory} instance.  *  * @see Directory  */
end_comment
begin_class
DECL|class|FSDirectory
specifier|public
specifier|abstract
class|class
name|FSDirectory
extends|extends
name|BaseDirectory
block|{
DECL|field|directory
specifier|protected
specifier|final
name|File
name|directory
decl_stmt|;
comment|// The underlying filesystem directory
DECL|field|staleFiles
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|staleFiles
init|=
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Files written, but not yet sync'ed
comment|// returns the canonical version of the directory, creating it if it doesn't exist.
DECL|method|getCanonicalPath
specifier|private
specifier|static
name|File
name|getCanonicalPath
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|File
argument_list|(
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
return|;
block|}
comment|/** Create a new FSDirectory for the named location (ctor for subclasses).    * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException if there is a low-level I/O error    */
DECL|method|FSDirectory
specifier|protected
name|FSDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// new ctors use always NativeFSLockFactory as default:
if|if
condition|(
name|lockFactory
operator|==
literal|null
condition|)
block|{
name|lockFactory
operator|=
operator|new
name|NativeFSLockFactory
argument_list|()
expr_stmt|;
block|}
name|directory
operator|=
name|getCanonicalPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|directory
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|directory
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchDirectoryException
argument_list|(
literal|"file '"
operator|+
name|directory
operator|+
literal|"' exists but is not a directory"
argument_list|)
throw|;
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an FSDirectory instance, trying to pick the    *  best implementation given the current environment.    *  The directory returned uses the {@link NativeFSLockFactory}.    *    *<p>Currently this returns {@link MMapDirectory} for most Solaris    *  and Windows 64-bit JREs, {@link NIOFSDirectory} for other    *  non-Windows JREs, and {@link SimpleFSDirectory} for other    *  JREs on Windows. It is highly recommended that you consult the    *  implementation's documentation for your platform before    *  using this method.    *    *<p><b>NOTE</b>: this method may suddenly change which    * implementation is returned from release to release, in    * the event that higher performance defaults become    * possible; if the precise implementation is important to    * your application, please instantiate it directly,    * instead. For optimal performance you should consider using    * {@link MMapDirectory} on 64 bit JVMs.    *    *<p>See<a href="#subclasses">above</a> */
DECL|method|open
specifier|public
specifier|static
name|FSDirectory
name|open
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Just like {@link #open(File)}, but allows you to    *  also specify a custom {@link LockFactory}. */
DECL|method|open
specifier|public
specifier|static
name|FSDirectory
name|open
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|Constants
operator|.
name|WINDOWS
operator|||
name|Constants
operator|.
name|SUN_OS
operator|||
name|Constants
operator|.
name|LINUX
operator|)
operator|&&
name|Constants
operator|.
name|JRE_IS_64BIT
operator|&&
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
condition|)
block|{
return|return
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
return|return
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NIOFSDirectory
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setLockFactory
specifier|public
name|void
name|setLockFactory
parameter_list|(
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
comment|// for filesystem based LockFactory, delete the lockPrefix, if the locks are placed
comment|// in index dir. If no index dir is given, set ourselves
if|if
condition|(
name|lockFactory
operator|instanceof
name|FSLockFactory
condition|)
block|{
specifier|final
name|FSLockFactory
name|lf
init|=
operator|(
name|FSLockFactory
operator|)
name|lockFactory
decl_stmt|;
specifier|final
name|File
name|dir
init|=
name|lf
operator|.
name|getLockDir
argument_list|()
decl_stmt|;
comment|// if the lock factory has no lockDir set, use the this directory as lockDir
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|lf
operator|.
name|setLockDir
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|lf
operator|.
name|setLockPrefix
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dir
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|equals
argument_list|(
name|directory
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
condition|)
block|{
name|lf
operator|.
name|setLockPrefix
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Lists all files (not subdirectories) in the    *  directory.  This method never returns null (throws    *  {@link IOException} instead).    *    *  @throws NoSuchDirectoryException if the directory    *   does not exist, or does exist but is not a    *   directory.    *  @throws IOException if list() returns null */
DECL|method|listAll
specifier|public
specifier|static
name|String
index|[]
name|listAll
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchDirectoryException
argument_list|(
literal|"directory '"
operator|+
name|dir
operator|+
literal|"' does not exist"
argument_list|)
throw|;
elseif|else
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchDirectoryException
argument_list|(
literal|"file '"
operator|+
name|dir
operator|+
literal|"' exists but is not a directory"
argument_list|)
throw|;
comment|// Exclude subdirs
name|String
index|[]
name|result
init|=
name|dir
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|file
parameter_list|)
block|{
return|return
operator|!
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|file
argument_list|)
operator|.
name|isDirectory
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"directory '"
operator|+
name|dir
operator|+
literal|"' exists and is a directory, but cannot be listed: list() returned null"
argument_list|)
throw|;
return|return
name|result
return|;
block|}
comment|/** Lists all files (not subdirectories) in the    * directory.    * @see #listAll(File) */
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|listAll
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/** Returns the length in bytes of a file in the directory. */
annotation|@
name|Override
DECL|method|fileLength
specifier|public
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
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
specifier|final
name|long
name|len
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
operator|&&
operator|!
name|file
operator|.
name|exists
argument_list|()
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
else|else
block|{
return|return
name|len
return|;
block|}
block|}
comment|/** Removes an existing file in the directory. */
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
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot delete "
operator|+
name|file
argument_list|)
throw|;
name|staleFiles
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an IndexOutput for the file with the given name. */
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
name|ensureCanWrite
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|FSIndexOutput
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|ensureCanWrite
specifier|protected
name|void
name|ensureCanWrite
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|directory
operator|.
name|exists
argument_list|()
condition|)
if|if
condition|(
operator|!
name|directory
operator|.
name|mkdirs
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory: "
operator|+
name|directory
argument_list|)
throw|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
comment|// delete existing, if any
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot overwrite: "
operator|+
name|file
argument_list|)
throw|;
block|}
comment|/**    * Sub classes should call this method on closing an open {@link IndexOutput}, reporting the name of the file    * that was closed. {@code FSDirectory} needs this information to take care of syncing stale files.    */
DECL|method|onIndexOutputClosed
specifier|protected
name|void
name|onIndexOutputClosed
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|staleFiles
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|toSync
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|toSync
operator|.
name|retainAll
argument_list|(
name|staleFiles
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|toSync
control|)
name|fsync
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|staleFiles
operator|.
name|removeAll
argument_list|(
name|toSync
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
name|dirName
decl_stmt|;
comment|// name to be hashed
try|try
block|{
name|dirName
operator|=
name|directory
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|int
name|digest
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|charIDX
init|=
literal|0
init|;
name|charIDX
operator|<
name|dirName
operator|.
name|length
argument_list|()
condition|;
name|charIDX
operator|++
control|)
block|{
specifier|final
name|char
name|ch
init|=
name|dirName
operator|.
name|charAt
argument_list|(
name|charIDX
argument_list|)
decl_stmt|;
name|digest
operator|=
literal|31
operator|*
name|digest
operator|+
name|ch
expr_stmt|;
block|}
return|return
literal|"lucene-"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|digest
argument_list|)
return|;
block|}
comment|/** Closes the store to future operations. */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
block|}
comment|/** @return the underlying filesystem directory */
DECL|method|getDirectory
specifier|public
name|File
name|getDirectory
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|directory
return|;
block|}
comment|/** For debug output. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"@"
operator|+
name|directory
operator|+
literal|" lockFactory="
operator|+
name|getLockFactory
argument_list|()
return|;
block|}
comment|/**    * Writes output with {@link RandomAccessFile#write(byte[], int, int)}    */
DECL|class|FSIndexOutput
specifier|protected
specifier|static
class|class
name|FSIndexOutput
extends|extends
name|BufferedIndexOutput
block|{
comment|/**      * The maximum chunk size is 8192 bytes, because {@link RandomAccessFile} mallocs      * a native buffer outside of stack if the write buffer size is larger.      */
DECL|field|CHUNK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|CHUNK_SIZE
init|=
literal|8192
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|FSDirectory
name|parent
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
DECL|field|isOpen
specifier|private
specifier|volatile
name|boolean
name|isOpen
decl_stmt|;
comment|// remember if the file is open, so that we don't try to close it more than once
DECL|method|FSIndexOutput
specifier|public
name|FSIndexOutput
parameter_list|(
name|FSDirectory
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|CHUNK_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|parent
operator|.
name|directory
argument_list|,
name|name
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|isOpen
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flushBuffer
specifier|protected
name|void
name|flushBuffer
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|isOpen
assert|;
while|while
condition|(
name|size
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|toWrite
init|=
name|Math
operator|.
name|min
argument_list|(
name|CHUNK_SIZE
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|toWrite
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|toWrite
expr_stmt|;
name|size
operator|-=
name|toWrite
expr_stmt|;
block|}
assert|assert
name|size
operator|==
literal|0
assert|;
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
name|parent
operator|.
name|onIndexOutputClosed
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// only close the file if it has not been closed yet
if|if
condition|(
name|isOpen
condition|)
block|{
name|IOException
name|priorE
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|priorE
operator|=
name|ioe
expr_stmt|;
block|}
finally|finally
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|priorE
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setLength
specifier|public
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fsync
specifier|protected
name|void
name|fsync
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|fsync
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
