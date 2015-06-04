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
name|nio
operator|.
name|file
operator|.
name|AccessDeniedException
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
name|FileAlreadyExistsException
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
begin_comment
comment|/**  *<p>Implements {@link LockFactory} using {@link  * Files#createFile}.</p>  *  *<p>The main downside with using this API for locking is   * that the Lucene write lock may not be released when   * the JVM exits abnormally.</p>  *  *<p>When this happens, an {@link LockObtainFailedException}  * is hit when trying to create a writer, in which case you may  * need to explicitly clear the lock file first by  * manually removing the file.  But, first be certain that  * no writer is in fact writing to the index otherwise you  * can easily corrupt your index.</p>  *  *<p>Special care needs to be taken if you change the locking  * implementation: First be certain that no writer is in fact  * writing to the index otherwise you can easily corrupt  * your index. Be sure to do the LockFactory change all Lucene  * instances and clean up all leftover lock files before starting  * the new configuration for the first time. Different implementations  * can not work together!</p>  *  *<p>If you suspect that this or any other LockFactory is  * not working properly in your environment, you can easily  * test it by using {@link VerifyingLockFactory}, {@link  * LockVerifyServer} and {@link LockStressTest}.</p>  *   *<p>This is a singleton, you have to use {@link #INSTANCE}.  *  * @see LockFactory  */
end_comment
begin_class
DECL|class|SimpleFSLockFactory
specifier|public
specifier|final
class|class
name|SimpleFSLockFactory
extends|extends
name|FSLockFactory
block|{
comment|/**    * Singleton instance    */
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|SimpleFSLockFactory
name|INSTANCE
init|=
operator|new
name|SimpleFSLockFactory
argument_list|()
decl_stmt|;
DECL|method|SimpleFSLockFactory
specifier|private
name|SimpleFSLockFactory
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
comment|// create the file: this will fail if it already exists
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
name|FileAlreadyExistsException
decl||
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// convert optional specific exception to our optional specific exception
throw|throw
operator|new
name|LockObtainFailedException
argument_list|(
literal|"Lock held elsewhere: "
operator|+
name|lockFile
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// used as a best-effort check, to see if the underlying file has changed
specifier|final
name|FileTime
name|creationTime
init|=
name|Files
operator|.
name|readAttributes
argument_list|(
name|lockFile
argument_list|,
name|BasicFileAttributes
operator|.
name|class
argument_list|)
operator|.
name|creationTime
argument_list|()
decl_stmt|;
return|return
operator|new
name|SimpleFSLock
argument_list|(
name|lockFile
argument_list|,
name|creationTime
argument_list|)
return|;
block|}
DECL|class|SimpleFSLock
specifier|static
specifier|final
class|class
name|SimpleFSLock
extends|extends
name|Lock
block|{
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|creationTime
specifier|private
specifier|final
name|FileTime
name|creationTime
decl_stmt|;
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
decl_stmt|;
DECL|method|SimpleFSLock
name|SimpleFSLock
parameter_list|(
name|Path
name|path
parameter_list|,
name|FileTime
name|creationTime
parameter_list|)
throws|throws
name|IOException
block|{
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
try|try
block|{
comment|// NOTE: unlike NativeFSLockFactory, we can potentially delete someone else's
comment|// lock if things have gone wrong. we do best-effort check (ensureValid) to
comment|// avoid doing this.
try|try
block|{
name|ensureValid
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|exc
parameter_list|)
block|{
comment|// notify the user they may need to intervene.
throw|throw
operator|new
name|LockReleaseFailedException
argument_list|(
literal|"Lock file cannot be safely removed. Manual intervention is recommended."
argument_list|,
name|exc
argument_list|)
throw|;
block|}
comment|// we did a best effort check, now try to remove the file. if something goes wrong,
comment|// we need to make it clear to the user that the directory may still remain locked.
try|try
block|{
name|Files
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
name|exc
parameter_list|)
block|{
throw|throw
operator|new
name|LockReleaseFailedException
argument_list|(
literal|"Unable to remove lock file. Manual intervention is recommended"
argument_list|,
name|exc
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|closed
operator|=
literal|true
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
literal|"SimpleFSLock(path="
operator|+
name|path
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
