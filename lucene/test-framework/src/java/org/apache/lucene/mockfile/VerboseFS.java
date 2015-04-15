begin_unit
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|AsynchronousFileChannel
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SeekableByteChannel
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
name|CopyOption
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
name|FileSystem
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
name|LinkOption
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
name|OpenOption
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
name|Path
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
name|StandardOpenOption
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
name|attribute
operator|.
name|FileAttribute
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|ExecutorService
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
name|InfoStream
import|;
end_import
begin_comment
comment|/**   * FileSystem that records all major destructive filesystem activities.  */
end_comment
begin_class
DECL|class|VerboseFS
specifier|public
class|class
name|VerboseFS
extends|extends
name|FilterFileSystemProvider
block|{
DECL|field|infoStream
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
DECL|field|root
specifier|final
name|Path
name|root
decl_stmt|;
comment|/**    * Create a new instance, recording major filesystem write activities    * (create, delete, etc) to the specified {@code InfoStream}.    * @param delegate delegate filesystem to wrap.    * @param infoStream infoStream to send messages to. The component for     * messages is named "FS".    */
DECL|method|VerboseFS
specifier|public
name|VerboseFS
parameter_list|(
name|FileSystem
name|delegate
parameter_list|,
name|InfoStream
name|infoStream
parameter_list|)
block|{
name|super
argument_list|(
literal|"verbose://"
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|this
operator|.
name|getFileSystem
argument_list|(
literal|null
argument_list|)
operator|.
name|getPath
argument_list|(
literal|"."
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|normalize
argument_list|()
expr_stmt|;
block|}
comment|/** Records message, and rethrows exception if not null */
DECL|method|sop
specifier|private
name|void
name|sop
parameter_list|(
name|String
name|text
parameter_list|,
name|Throwable
name|exception
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"FS"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"FS"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"FS"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"FS"
argument_list|,
name|text
operator|+
literal|" (FAILED: "
operator|+
name|exception
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|path
specifier|private
name|String
name|path
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|path
operator|=
name|root
operator|.
name|relativize
argument_list|(
name|path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|normalize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createDirectory
specifier|public
name|void
name|createDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|createDirectory
argument_list|(
name|dir
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"createDirectory: "
operator|+
name|path
argument_list|(
name|dir
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"delete: "
operator|+
name|path
argument_list|(
name|path
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|Path
name|source
parameter_list|,
name|Path
name|target
parameter_list|,
name|CopyOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|copy
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"copy"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|options
argument_list|)
operator|+
literal|": "
operator|+
name|path
argument_list|(
name|source
argument_list|)
operator|+
literal|" -> "
operator|+
name|path
argument_list|(
name|target
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|move
specifier|public
name|void
name|move
parameter_list|(
name|Path
name|source
parameter_list|,
name|Path
name|target
parameter_list|,
name|CopyOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|move
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"move"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|options
argument_list|)
operator|+
literal|": "
operator|+
name|path
argument_list|(
name|source
argument_list|)
operator|+
literal|" -> "
operator|+
name|path
argument_list|(
name|target
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setAttribute
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|attribute
parameter_list|,
name|Object
name|value
parameter_list|,
name|LinkOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|setAttribute
argument_list|(
name|path
argument_list|,
name|attribute
argument_list|,
name|value
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"setAttribute["
operator|+
name|attribute
operator|+
literal|"="
operator|+
name|value
operator|+
literal|"]: "
operator|+
name|path
argument_list|(
name|path
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newOutputStream
specifier|public
name|OutputStream
name|newOutputStream
parameter_list|(
name|Path
name|path
parameter_list|,
name|OpenOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|newOutputStream
argument_list|(
name|path
argument_list|,
name|options
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"newOutputStream"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|options
argument_list|)
operator|+
literal|": "
operator|+
name|path
argument_list|(
name|path
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
DECL|method|containsDestructive
specifier|private
name|boolean
name|containsDestructive
parameter_list|(
name|Set
argument_list|<
name|?
extends|extends
name|OpenOption
argument_list|>
name|options
parameter_list|)
block|{
return|return
operator|(
name|options
operator|.
name|contains
argument_list|(
name|StandardOpenOption
operator|.
name|APPEND
argument_list|)
operator|||
name|options
operator|.
name|contains
argument_list|(
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
operator|||
name|options
operator|.
name|contains
argument_list|(
name|StandardOpenOption
operator|.
name|DELETE_ON_CLOSE
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|newFileChannel
specifier|public
name|FileChannel
name|newFileChannel
parameter_list|(
name|Path
name|path
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|OpenOption
argument_list|>
name|options
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|newFileChannel
argument_list|(
name|path
argument_list|,
name|options
argument_list|,
name|attrs
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|containsDestructive
argument_list|(
name|options
argument_list|)
condition|)
block|{
name|sop
argument_list|(
literal|"newFileChannel"
operator|+
name|options
operator|+
literal|": "
operator|+
name|path
argument_list|(
name|path
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|newAsynchronousFileChannel
specifier|public
name|AsynchronousFileChannel
name|newAsynchronousFileChannel
parameter_list|(
name|Path
name|path
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|OpenOption
argument_list|>
name|options
parameter_list|,
name|ExecutorService
name|executor
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|newAsynchronousFileChannel
argument_list|(
name|path
argument_list|,
name|options
argument_list|,
name|executor
argument_list|,
name|attrs
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|containsDestructive
argument_list|(
name|options
argument_list|)
condition|)
block|{
name|sop
argument_list|(
literal|"newAsynchronousFileChannel"
operator|+
name|options
operator|+
literal|": "
operator|+
name|path
argument_list|(
name|path
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|newByteChannel
specifier|public
name|SeekableByteChannel
name|newByteChannel
parameter_list|(
name|Path
name|path
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|OpenOption
argument_list|>
name|options
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|newByteChannel
argument_list|(
name|path
argument_list|,
name|options
argument_list|,
name|attrs
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|containsDestructive
argument_list|(
name|options
argument_list|)
condition|)
block|{
name|sop
argument_list|(
literal|"newByteChannel"
operator|+
name|options
operator|+
literal|": "
operator|+
name|path
argument_list|(
name|path
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|createSymbolicLink
specifier|public
name|void
name|createSymbolicLink
parameter_list|(
name|Path
name|link
parameter_list|,
name|Path
name|target
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|createSymbolicLink
argument_list|(
name|link
argument_list|,
name|target
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"createSymbolicLink: "
operator|+
name|path
argument_list|(
name|link
argument_list|)
operator|+
literal|" -> "
operator|+
name|path
argument_list|(
name|target
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createLink
specifier|public
name|void
name|createLink
parameter_list|(
name|Path
name|link
parameter_list|,
name|Path
name|existing
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|super
operator|.
name|createLink
argument_list|(
name|link
argument_list|,
name|existing
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"createLink: "
operator|+
name|path
argument_list|(
name|link
argument_list|)
operator|+
literal|" -> "
operator|+
name|path
argument_list|(
name|existing
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteIfExists
specifier|public
name|boolean
name|deleteIfExists
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|deleteIfExists
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|exception
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|sop
argument_list|(
literal|"deleteIfExists: "
operator|+
name|path
argument_list|(
name|path
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
end_class
end_unit
