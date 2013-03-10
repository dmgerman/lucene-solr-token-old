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
name|index
operator|.
name|DirectoryReader
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
name|BeforeClass
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
begin_class
DECL|class|AlternateDirectoryTest
specifier|public
class|class
name|AlternateDirectoryTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-altdirectory.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Simple test to ensure that alternate IndexReaderFactory is being used.    */
annotation|@
name|Test
DECL|method|testAltDirectoryUsed
specifier|public
name|void
name|testAltDirectoryUsed
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"qt"
argument_list|,
literal|"standard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|TestFSDirectoryFactory
operator|.
name|openCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|TestIndexReaderFactory
operator|.
name|newReaderCalled
argument_list|)
expr_stmt|;
block|}
DECL|class|TestFSDirectoryFactory
specifier|static
specifier|public
class|class
name|TestFSDirectoryFactory
extends|extends
name|StandardDirectoryFactory
block|{
DECL|field|openCalled
specifier|public
specifier|static
specifier|volatile
name|boolean
name|openCalled
init|=
literal|false
decl_stmt|;
DECL|field|dir
specifier|public
specifier|static
specifier|volatile
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
block|{
name|openCalled
operator|=
literal|true
expr_stmt|;
return|return
name|dir
operator|=
name|newFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|TestIndexReaderFactory
specifier|static
specifier|public
class|class
name|TestIndexReaderFactory
extends|extends
name|IndexReaderFactory
block|{
DECL|field|newReaderCalled
specifier|static
specifier|volatile
name|boolean
name|newReaderCalled
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|newReader
specifier|public
name|DirectoryReader
name|newReader
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|TestIndexReaderFactory
operator|.
name|newReaderCalled
operator|=
literal|true
expr_stmt|;
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
