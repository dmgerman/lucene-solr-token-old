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
name|Closeable
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
begin_comment
comment|/** A delegating Directory that records which files were  *  written to and deleted. */
end_comment
begin_class
DECL|class|TrackingDirectoryWrapper
specifier|public
specifier|final
class|class
name|TrackingDirectoryWrapper
extends|extends
name|Directory
implements|implements
name|Closeable
block|{
DECL|field|other
specifier|private
specifier|final
name|Directory
name|other
decl_stmt|;
DECL|field|createdFileNames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|createdFileNames
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
DECL|method|TrackingDirectoryWrapper
specifier|public
name|TrackingDirectoryWrapper
parameter_list|(
name|Directory
name|other
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
block|}
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
return|return
name|other
operator|.
name|listAll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|other
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
return|;
block|}
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
name|createdFileNames
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|other
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
return|return
name|other
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
return|;
block|}
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
name|createdFileNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|other
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|context
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
block|{
name|other
operator|.
name|sync
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
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
return|return
name|other
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|other
operator|.
name|makeLock
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|other
operator|.
name|clearLock
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
name|other
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|other
operator|.
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLockFactory
specifier|public
name|LockFactory
name|getLockFactory
parameter_list|()
block|{
return|return
name|other
operator|.
name|getLockFactory
argument_list|()
return|;
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
name|other
operator|.
name|getLockID
argument_list|()
return|;
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
literal|"TrackingDirectoryWrapper("
operator|+
name|other
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|Directory
name|to
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
name|createdFileNames
operator|.
name|add
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|other
operator|.
name|copy
argument_list|(
name|to
argument_list|,
name|src
argument_list|,
name|dest
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSlicer
specifier|public
name|Directory
operator|.
name|IndexInputSlicer
name|createSlicer
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|other
operator|.
name|createSlicer
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|// maybe clone before returning.... all callers are
comment|// cloning anyway....
DECL|method|getCreatedFiles
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getCreatedFiles
parameter_list|()
block|{
return|return
name|createdFileNames
return|;
block|}
block|}
end_class
end_unit
