begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|File
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
name|MockDirectoryWrapper
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
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Opens a directory with {@link LuceneTestCase#newDirectory()}  */
end_comment
begin_class
DECL|class|MockDirectoryFactory
specifier|public
class|class
name|MockDirectoryFactory
extends|extends
name|CachingDirectoryFactory
block|{
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|LuceneTestCase
operator|.
name|newDirectory
argument_list|()
decl_stmt|;
comment|// Somehow removing unref'd files in Solr tests causes
comment|// problems... there's some interaction w/
comment|// CachingDirectoryFactory.  Once we track down where Solr
comment|// isn't closing an IW, we can re-enable this:
name|dir
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|dir
return|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|fullPath
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|DirectoryFactory
operator|.
name|class
init|)
block|{
name|CacheValue
name|cacheValue
init|=
name|byPathCache
operator|.
name|get
argument_list|(
name|fullPath
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cacheValue
operator|!=
literal|null
condition|)
block|{
name|directory
operator|=
name|cacheValue
operator|.
name|directory
expr_stmt|;
block|}
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
