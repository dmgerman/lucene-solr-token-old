begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|NRTCachingDirectory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_comment
comment|/**  * Factory to instantiate {@link org.apache.lucene.store.NRTCachingDirectory}  */
end_comment
begin_class
DECL|class|NRTCachingDirectoryFactory
specifier|public
class|class
name|NRTCachingDirectoryFactory
extends|extends
name|StandardDirectoryFactory
block|{
DECL|field|maxMergeSizeMB
specifier|private
name|double
name|maxMergeSizeMB
decl_stmt|;
DECL|field|maxCachedMB
specifier|private
name|double
name|maxCachedMB
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|SolrParams
name|params
init|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|maxMergeSizeMB
operator|=
name|params
operator|.
name|getDouble
argument_list|(
literal|"maxMergeSizeMB"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxMergeSizeMB
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxMergeSizeMB must be greater than 0"
argument_list|)
throw|;
block|}
name|maxCachedMB
operator|=
name|params
operator|.
name|getDouble
argument_list|(
literal|"maxCachedMB"
argument_list|,
literal|48
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxCachedMB
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxCachedMB must be greater than 0"
argument_list|)
throw|;
block|}
block|}
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
operator|new
name|NRTCachingDirectory
argument_list|(
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
argument_list|,
name|maxMergeSizeMB
argument_list|,
name|maxCachedMB
argument_list|)
return|;
block|}
block|}
end_class
end_unit
