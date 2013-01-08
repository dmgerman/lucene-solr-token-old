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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|FSDirectory
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
name|IOContext
import|;
end_import
begin_comment
comment|/**  * Directory provider which mimics original Solr   * {@link org.apache.lucene.store.FSDirectory} based behavior.  *   * File based DirectoryFactory implementations generally extend  * this class.  *   */
end_comment
begin_class
DECL|class|StandardDirectoryFactory
specifier|public
class|class
name|StandardDirectoryFactory
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
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|String
name|normalize
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
return|;
block|}
DECL|method|isPersistent
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|CacheValue
name|val
init|=
name|byDirectoryCache
operator|.
name|get
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown directory "
operator|+
name|dir
argument_list|)
throw|;
block|}
name|File
name|dirFile
init|=
operator|new
name|File
argument_list|(
name|val
operator|.
name|path
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dirFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
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
name|File
name|dirFile
init|=
operator|new
name|File
argument_list|(
name|fullPath
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dirFile
argument_list|)
expr_stmt|;
block|}
comment|/**    * Override for more efficient moves.    *     * @throws IOException    *           If there is a low-level I/O error.    */
annotation|@
name|Override
DECL|method|move
specifier|public
name|void
name|move
parameter_list|(
name|Directory
name|fromDir
parameter_list|,
name|Directory
name|toDir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fromDir
operator|instanceof
name|FSDirectory
operator|&&
name|toDir
operator|instanceof
name|FSDirectory
condition|)
block|{
name|File
name|dir1
init|=
operator|(
operator|(
name|FSDirectory
operator|)
name|fromDir
operator|)
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|File
name|dir2
init|=
operator|(
operator|(
name|FSDirectory
operator|)
name|toDir
operator|)
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|File
name|indexFileInTmpDir
init|=
operator|new
name|File
argument_list|(
name|dir1
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|File
name|indexFileInIndex
init|=
operator|new
name|File
argument_list|(
name|dir2
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|indexFileInTmpDir
operator|.
name|renameTo
argument_list|(
name|indexFileInIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
return|return;
block|}
block|}
name|super
operator|.
name|move
argument_list|(
name|fromDir
argument_list|,
name|toDir
argument_list|,
name|fileName
argument_list|,
name|ioContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
