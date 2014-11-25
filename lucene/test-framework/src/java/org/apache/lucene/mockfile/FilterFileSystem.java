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
name|nio
operator|.
name|file
operator|.
name|FileStore
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
name|FileSystems
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
name|PathMatcher
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
name|WatchService
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
name|UserPrincipalLookupService
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
name|spi
operator|.
name|FileSystemProvider
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
begin_comment
comment|/**    * A {@code FilterFileSystem} contains another   * {@code FileSystem}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   */
end_comment
begin_class
DECL|class|FilterFileSystem
specifier|public
class|class
name|FilterFileSystem
extends|extends
name|FileSystem
block|{
comment|/**    * FileSystemProvider that created this FilterFileSystem    */
DECL|field|parent
specifier|protected
specifier|final
name|FilterFileSystemProvider
name|parent
decl_stmt|;
comment|/**     * The underlying {@code FileSystem} instance.     */
DECL|field|delegate
specifier|protected
specifier|final
name|FileSystem
name|delegate
decl_stmt|;
comment|/**    * Construct a {@code FilterFileSystem} based on     * the specified base filesystem.    *<p>    * Note that base filesystem is closed if this filesystem is closed,    * however the default filesystem provider will never be closed, it doesn't    * support that.    * @param delegate specified base channel.    */
DECL|method|FilterFileSystem
specifier|public
name|FilterFileSystem
parameter_list|(
name|FilterFileSystemProvider
name|parent
parameter_list|,
name|FileSystem
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|provider
specifier|public
name|FileSystemProvider
name|provider
parameter_list|()
block|{
return|return
name|parent
return|;
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
if|if
condition|(
name|delegate
operator|==
name|FileSystems
operator|.
name|getDefault
argument_list|()
condition|)
block|{
comment|// you can't close the default provider!
name|parent
operator|.
name|onClose
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
name|FileSystem
name|d
init|=
name|delegate
init|)
block|{
name|parent
operator|.
name|onClose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|isOpen
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isOpen
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isReadOnly
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isReadOnly
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSeparator
specifier|public
name|String
name|getSeparator
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSeparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRootDirectories
specifier|public
name|Iterable
argument_list|<
name|Path
argument_list|>
name|getRootDirectories
parameter_list|()
block|{
specifier|final
name|Iterable
argument_list|<
name|Path
argument_list|>
name|roots
init|=
name|delegate
operator|.
name|getRootDirectories
argument_list|()
decl_stmt|;
return|return
parameter_list|()
lambda|->
block|{
specifier|final
name|Iterator
argument_list|<
name|Path
argument_list|>
name|iterator
init|=
name|roots
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Path
name|next
parameter_list|()
block|{
return|return
operator|new
name|FilterPath
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|FilterFileSystem
operator|.
name|this
argument_list|)
return|;
block|}
block|}
return|;
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStores
specifier|public
name|Iterable
argument_list|<
name|FileStore
argument_list|>
name|getFileStores
parameter_list|()
block|{
specifier|final
name|Iterable
argument_list|<
name|FileStore
argument_list|>
name|fileStores
init|=
name|delegate
operator|.
name|getFileStores
argument_list|()
decl_stmt|;
return|return
parameter_list|()
lambda|->
block|{
specifier|final
name|Iterator
argument_list|<
name|FileStore
argument_list|>
name|iterator
init|=
name|fileStores
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|FileStore
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileStore
name|next
parameter_list|()
block|{
return|return
operator|new
name|FilterFileStore
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|,
name|parent
operator|.
name|getScheme
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|supportedFileAttributeViews
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|supportedFileAttributeViews
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|supportedFileAttributeViews
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPath
specifier|public
name|Path
name|getPath
parameter_list|(
name|String
name|first
parameter_list|,
name|String
modifier|...
name|more
parameter_list|)
block|{
return|return
operator|new
name|FilterPath
argument_list|(
name|delegate
operator|.
name|getPath
argument_list|(
name|first
argument_list|,
name|more
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPathMatcher
specifier|public
name|PathMatcher
name|getPathMatcher
parameter_list|(
name|String
name|syntaxAndPattern
parameter_list|)
block|{
specifier|final
name|PathMatcher
name|matcher
init|=
name|delegate
operator|.
name|getPathMatcher
argument_list|(
name|syntaxAndPattern
argument_list|)
decl_stmt|;
return|return
name|path
lambda|->
block|{
if|if
condition|(
name|path
operator|instanceof
name|FilterPath
condition|)
block|{
return|return
name|matcher
operator|.
name|matches
argument_list|(
operator|(
operator|(
name|FilterPath
operator|)
name|path
operator|)
operator|.
name|delegate
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getUserPrincipalLookupService
specifier|public
name|UserPrincipalLookupService
name|getUserPrincipalLookupService
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getUserPrincipalLookupService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newWatchService
specifier|public
name|WatchService
name|newWatchService
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newWatchService
argument_list|()
return|;
block|}
block|}
end_class
end_unit
