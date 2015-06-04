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
name|FileLock
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
name|Files
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
name|BasicFileAttributes
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
name|FileTime
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
name|util
operator|.
name|Collections
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
comment|/**  *<p>Implements {@link LockFactory} using native OS file  * locks.  Note that because this LockFactory relies on  * java.nio.* APIs for locking, any problems with those APIs  * will cause locking to fail.  Specifically, on certain NFS  * environments the java.nio.* locks will fail (the lock can  * incorrectly be double acquired) whereas {@link  * SimpleFSLockFactory} worked perfectly in those same  * environments.  For NFS based access to an index, it's  * recommended that you try {@link SimpleFSLockFactory}  * first and work around the one limitation that a lock file  * could be left when the JVM exits abnormally.</p>  *  *<p>The primary benefit of {@link NativeFSLockFactory} is  * that locks (not the lock file itsself) will be properly  * removed (by the OS) if the JVM has an abnormal exit.</p>  *   *<p>Note that, unlike {@link SimpleFSLockFactory}, the existence of  * leftover lock files in the filesystem is fine because the OS  * will free the locks held against these files even though the  * files still remain. Lucene will never actively remove the lock  * files, so although you see them, the index may not be locked.</p>  *  *<p>Special care needs to be taken if you change the locking  * implementation: First be certain that no writer is in fact  * writing to the index otherwise you can easily corrupt  * your index. Be sure to do the LockFactory change on all Lucene  * instances and clean up all leftover lock files before starting  * the new configuration for the first time. Different implementations  * can not work together!</p>  *  *<p>If you suspect that this or any other LockFactory is  * not working properly in your environment, you can easily  * test it by using {@link VerifyingLockFactory}, {@link  * LockVerifyServer} and {@link LockStressTest}.</p>  *   *<p>This is a singleton, you have to use {@link #INSTANCE}.  *  * @see LockFactory  */
end_comment
begin_class
DECL|class|NativeFSLockFactory
specifier|public
specifier|final
class|class
name|NativeFSLockFactory
extends|extends
name|FSLockFactory
block|{
comment|/**    * Singleton instance    */
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|NativeFSLockFactory
name|INSTANCE
init|=
operator|new
name|NativeFSLockFactory
argument_list|()
decl_stmt|;
DECL|field|LOCK_HELD
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|LOCK_HELD
init|=
name|Collections
operator|.
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
DECL|method|NativeFSLockFactory
specifier|private
name|NativeFSLockFactory
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|obtainFSLock
specifier|protected
name|Lock
name|obtainFSLock
parameter_list|(
name|FSDirectory
name|dir
parameter_list|,
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|lockDir
init|=
name|dir
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
comment|// Ensure that lockDir exists and is a directory.
comment|// note: this will fail if lockDir is a symlink
name|Files
operator|.
name|createDirectories
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
name|Path
name|lockFile
init|=
name|lockDir
operator|.
name|resolve
argument_list|(
name|lockName
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|createFile
argument_list|(
name|lockFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
comment|// we must create the file to have a truly canonical path.
comment|// if it's already created, we don't care. if it cant be created, it will fail below.
block|}
comment|// fails if the lock file does not exist
specifier|final
name|Path
name|realPath
init|=
name|lockFile
operator|.
name|toRealPath
argument_list|()
decl_stmt|;
comment|// used as a best-effort check, to see if the underlying file has changed
specifier|final
name|FileTime
name|creationTime
init|=
name|Files
operator|.
name|readAttributes
argument_list|(
name|realPath
argument_list|,
name|BasicFileAttributes
operator|.
name|class
argument_list|)
operator|.
name|creationTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOCK_HELD
operator|.
name|add
argument_list|(
name|realPath
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|FileChannel
name|channel
init|=
literal|null
decl_stmt|;
name|FileLock
name|lock
init|=
literal|null
decl_stmt|;
try|try
block|{
name|channel
operator|=
name|FileChannel
operator|.
name|open
argument_list|(
name|realPath
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
expr_stmt|;
name|lock
operator|=
name|channel
operator|.
name|tryLock
argument_list|()
expr_stmt|;
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|NativeFSLock
argument_list|(
name|lock
argument_list|,
name|channel
argument_list|,
name|realPath
argument_list|,
name|creationTime
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|LockObtainFailedException
argument_list|(
literal|"Lock held by another program: "
operator|+
name|realPath
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
comment|// not successful - clear up and move out
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|channel
argument_list|)
expr_stmt|;
comment|// TODO: addSuppressed
name|clearLockHeld
argument_list|(
name|realPath
argument_list|)
expr_stmt|;
comment|// clear LOCK_HELD last
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|LockObtainFailedException
argument_list|(
literal|"Lock held by this virtual machine: "
operator|+
name|realPath
argument_list|)
throw|;
block|}
block|}
DECL|method|clearLockHeld
specifier|private
specifier|static
specifier|final
name|void
name|clearLockHeld
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|remove
init|=
name|LOCK_HELD
operator|.
name|remove
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|remove
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Lock path was cleared but never marked as held: "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
comment|// TODO: kind of bogus we even pass channel:
comment|// FileLock has an accessor, but mockfs doesnt yet mock the locks, too scary atm.
DECL|class|NativeFSLock
specifier|static
specifier|final
class|class
name|NativeFSLock
extends|extends
name|Lock
block|{
DECL|field|lock
specifier|final
name|FileLock
name|lock
decl_stmt|;
DECL|field|channel
specifier|final
name|FileChannel
name|channel
decl_stmt|;
DECL|field|path
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|creationTime
specifier|final
name|FileTime
name|creationTime
decl_stmt|;
DECL|field|closed
specifier|volatile
name|boolean
name|closed
decl_stmt|;
DECL|method|NativeFSLock
name|NativeFSLock
parameter_list|(
name|FileLock
name|lock
parameter_list|,
name|FileChannel
name|channel
parameter_list|,
name|Path
name|path
parameter_list|,
name|FileTime
name|creationTime
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ensureValid
specifier|public
name|void
name|ensureValid
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Lock instance already released: "
operator|+
name|this
argument_list|)
throw|;
block|}
comment|// check we are still in the locks map (some debugger or something crazy didn't remove us)
if|if
condition|(
operator|!
name|LOCK_HELD
operator|.
name|contains
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Lock path unexpectedly cleared from map: "
operator|+
name|this
argument_list|)
throw|;
block|}
comment|// check our lock wasn't invalidated.
if|if
condition|(
operator|!
name|lock
operator|.
name|isValid
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"FileLock invalidated by an external force: "
operator|+
name|this
argument_list|)
throw|;
block|}
comment|// try to validate the underlying file descriptor.
comment|// this will throw IOException if something is wrong.
name|long
name|size
init|=
name|channel
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Unexpected lock file size: "
operator|+
name|size
operator|+
literal|", (lock="
operator|+
name|this
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// try to validate the backing file name, that it still exists,
comment|// and has the same creation time as when we obtained the lock.
comment|// if it differs, someone deleted our lock file (and we are ineffective)
name|FileTime
name|ctime
init|=
name|Files
operator|.
name|readAttributes
argument_list|(
name|path
argument_list|,
name|BasicFileAttributes
operator|.
name|class
argument_list|)
operator|.
name|creationTime
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|creationTime
operator|.
name|equals
argument_list|(
name|ctime
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Underlying file changed by an external force at "
operator|+
name|creationTime
operator|+
literal|", (lock="
operator|+
name|this
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
comment|// NOTE: we don't validate, as unlike SimpleFSLockFactory, we can't break others locks
comment|// first release the lock, then the channel
try|try
init|(
name|FileChannel
name|channel
init|=
name|this
operator|.
name|channel
init|;
name|FileLock
name|lock
operator|=
name|this
operator|.
name|lock
init|)
block|{
assert|assert
name|lock
operator|!=
literal|null
assert|;
assert|assert
name|channel
operator|!=
literal|null
assert|;
block|}
finally|finally
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|clearLockHeld
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NativeFSLock(path="
operator|+
name|path
operator|+
literal|",impl="
operator|+
name|lock
operator|+
literal|",ctime="
operator|+
name|creationTime
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class
end_unit
