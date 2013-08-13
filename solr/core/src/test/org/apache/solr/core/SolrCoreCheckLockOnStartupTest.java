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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
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
name|LockObtainFailedException
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
name|NativeFSLockFactory
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
name|SimpleFSLockFactory
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
name|Version
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
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|util
operator|.
name|Map
import|;
end_import
begin_class
DECL|class|SolrCoreCheckLockOnStartupTest
specifier|public
class|class
name|SolrCoreCheckLockOnStartupTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"org.apache.solr.core.SimpleFSDirectoryFactory"
argument_list|)
expr_stmt|;
comment|//explicitly creates the temp dataDir so we know where the index will be located
name|createTempDir
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|indexWriterConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
name|newFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
comment|//creates a new index on the known location
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|indexWriterConfig
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleLockErrorOnStartup
specifier|public
name|void
name|testSimpleLockErrorOnStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"index"
argument_list|)
argument_list|,
operator|new
name|SimpleFSLockFactory
argument_list|()
argument_list|)
decl_stmt|;
comment|//creates a new IndexWriter without releasing the lock yet
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|ignoreException
argument_list|(
literal|"locked"
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.lockType"
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
comment|//opening a new core on the same index
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkForCoreInitException
argument_list|(
name|LockObtainFailedException
operator|.
name|class
argument_list|)
condition|)
return|return;
name|fail
argument_list|(
literal|"Expected "
operator|+
name|LockObtainFailedException
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.tests.lockType"
argument_list|)
expr_stmt|;
name|unIgnoreException
argument_list|(
literal|"locked"
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|deleteCore
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNativeLockErrorOnStartup
specifier|public
name|void
name|testNativeLockErrorOnStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Acquiring lock on {}"
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|directory
init|=
name|newFSDirectory
argument_list|(
name|indexDir
argument_list|,
operator|new
name|NativeFSLockFactory
argument_list|()
argument_list|)
decl_stmt|;
comment|//creates a new IndexWriter without releasing the lock yet
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|ignoreException
argument_list|(
literal|"locked"
argument_list|)
expr_stmt|;
try|try
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.lockType"
argument_list|,
literal|"native"
argument_list|)
expr_stmt|;
comment|//opening a new core on the same index
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|CoreContainer
name|cc
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkForCoreInitException
argument_list|(
name|LockObtainFailedException
operator|.
name|class
argument_list|)
condition|)
return|return;
name|fail
argument_list|(
literal|"Expected "
operator|+
name|LockObtainFailedException
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.tests.lockType"
argument_list|)
expr_stmt|;
name|unIgnoreException
argument_list|(
literal|"locked"
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|deleteCore
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkForCoreInitException
specifier|private
name|boolean
name|checkForCoreInitException
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|clazz
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|entry
range|:
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getCoreInitFailures
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|Throwable
name|t
init|=
name|entry
operator|.
name|getValue
argument_list|()
init|;
name|t
operator|!=
literal|null
condition|;
name|t
operator|=
name|t
operator|.
name|getCause
argument_list|()
control|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|t
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
