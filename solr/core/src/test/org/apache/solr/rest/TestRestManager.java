begin_unit
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
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
name|Locale
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
name|solr
operator|.
name|common
operator|.
name|SolrException
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
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
name|rest
operator|.
name|ManagedResourceStorage
operator|.
name|StorageIO
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
name|rest
operator|.
name|schema
operator|.
name|analysis
operator|.
name|ManagedWordSetResource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|Request
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|Reference
import|;
end_import
begin_comment
comment|/**  * Tests {@link RestManager} functionality, including resource registration,  * and REST API requests and responses.  */
end_comment
begin_class
DECL|class|TestRestManager
specifier|public
class|class
name|TestRestManager
extends|extends
name|SolrRestletTestBase
block|{
DECL|class|BogusManagedResource
specifier|private
class|class
name|BogusManagedResource
extends|extends
name|ManagedResource
block|{
DECL|method|BogusManagedResource
specifier|protected
name|BogusManagedResource
parameter_list|(
name|String
name|resourceId
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|,
name|StorageIO
name|storageIO
parameter_list|)
throws|throws
name|SolrException
block|{
name|super
argument_list|(
name|resourceId
argument_list|,
name|loader
argument_list|,
name|storageIO
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onManagedDataLoadedFromStorage
specifier|protected
name|void
name|onManagedDataLoadedFromStorage
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|managedInitArgs
parameter_list|,
name|Object
name|managedData
parameter_list|)
throws|throws
name|SolrException
block|{}
annotation|@
name|Override
DECL|method|applyUpdatesToManagedData
specifier|protected
name|Object
name|applyUpdatesToManagedData
parameter_list|(
name|Object
name|updates
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|doDeleteChild
specifier|public
name|void
name|doDeleteChild
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|doGet
specifier|public
name|void
name|doGet
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
block|{}
block|}
DECL|class|MockAnalysisComponent
specifier|private
class|class
name|MockAnalysisComponent
implements|implements
name|ManagedResourceObserver
block|{
annotation|@
name|Override
DECL|method|onManagedResourceInitialized
specifier|public
name|void
name|onManagedResourceInitialized
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|args
parameter_list|,
name|ManagedResource
name|res
parameter_list|)
throws|throws
name|SolrException
block|{
name|assertTrue
argument_list|(
name|res
operator|instanceof
name|ManagedWordSetResource
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test RestManager initialization and handling of registered ManagedResources.     */
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testManagedResourceRegistrationAndInitialization
specifier|public
name|void
name|testManagedResourceRegistrationAndInitialization
parameter_list|()
throws|throws
name|Exception
block|{
comment|// first, we need to register some ManagedResources, which is done with the registry
comment|// provided by the SolrResourceLoader
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"./"
argument_list|)
decl_stmt|;
name|RestManager
operator|.
name|Registry
name|registry
init|=
name|loader
operator|.
name|getManagedResourceRegistry
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Expected a non-null RestManager.Registry from the SolrResourceLoader!"
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|String
name|resourceId
init|=
literal|"/config/test/foo"
decl_stmt|;
name|registry
operator|.
name|registerManagedResource
argument_list|(
name|resourceId
argument_list|,
name|ManagedWordSetResource
operator|.
name|class
argument_list|,
operator|new
name|MockAnalysisComponent
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the two different components can register the same ManagedResource in the registry
name|registry
operator|.
name|registerManagedResource
argument_list|(
name|resourceId
argument_list|,
name|ManagedWordSetResource
operator|.
name|class
argument_list|,
operator|new
name|MockAnalysisComponent
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify we can register another resource under a different resourceId
name|registry
operator|.
name|registerManagedResource
argument_list|(
literal|"/config/test/foo2"
argument_list|,
name|ManagedWordSetResource
operator|.
name|class
argument_list|,
operator|new
name|MockAnalysisComponent
argument_list|()
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"REST API path .* already registered to instances of "
argument_list|)
expr_stmt|;
name|String
name|failureMessage
init|=
literal|"Should not be able to register a different"
operator|+
literal|" ManagedResource implementation for {}"
decl_stmt|;
comment|// verify that some other hooligan cannot register another type
comment|// of ManagedResource implementation under the same resourceId
try|try
block|{
name|registry
operator|.
name|registerManagedResource
argument_list|(
name|resourceId
argument_list|,
name|BogusManagedResource
operator|.
name|class
argument_list|,
operator|new
name|MockAnalysisComponent
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|failureMessage
argument_list|,
name|resourceId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|solrExc
parameter_list|)
block|{
comment|// expected output
block|}
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
name|ignoreException
argument_list|(
literal|"is a reserved endpoint used by the Solr REST API!"
argument_list|)
expr_stmt|;
name|failureMessage
operator|=
literal|"Should not be able to register reserved endpoint {}"
expr_stmt|;
comment|// verify that already-spoken-for REST API endpoints can't be registered
name|Set
argument_list|<
name|String
argument_list|>
name|reservedEndpoints
init|=
name|registry
operator|.
name|getReservedEndpoints
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|reservedEndpoints
operator|.
name|size
argument_list|()
operator|>
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reservedEndpoints
operator|.
name|contains
argument_list|(
name|RestManager
operator|.
name|SCHEMA_BASE_PATH
operator|+
name|RestManager
operator|.
name|MANAGED_ENDPOINT
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|endpoint
range|:
name|reservedEndpoints
control|)
block|{
try|try
block|{
name|registry
operator|.
name|registerManagedResource
argument_list|(
name|endpoint
argument_list|,
name|BogusManagedResource
operator|.
name|class
argument_list|,
operator|new
name|MockAnalysisComponent
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|failureMessage
argument_list|,
name|endpoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|solrExc
parameter_list|)
block|{
comment|// expected output
block|}
comment|// also try to register already-spoken-for REST API endpoints with a child segment
name|endpoint
operator|+=
literal|"/kid"
expr_stmt|;
try|try
block|{
name|registry
operator|.
name|registerManagedResource
argument_list|(
name|endpoint
argument_list|,
name|BogusManagedResource
operator|.
name|class
argument_list|,
operator|new
name|MockAnalysisComponent
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|failureMessage
argument_list|,
name|endpoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|solrExc
parameter_list|)
block|{
comment|// expected output
block|}
block|}
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|RestManager
name|restManager
init|=
operator|new
name|RestManager
argument_list|()
decl_stmt|;
name|restManager
operator|.
name|init
argument_list|(
name|loader
argument_list|,
name|initArgs
argument_list|,
operator|new
name|ManagedResourceStorage
operator|.
name|InMemoryStorageIO
argument_list|()
argument_list|)
expr_stmt|;
name|ManagedResource
name|res
init|=
name|restManager
operator|.
name|getManagedResource
argument_list|(
name|resourceId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|res
operator|instanceof
name|ManagedWordSetResource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|res
operator|.
name|getResourceId
argument_list|()
argument_list|,
name|resourceId
argument_list|)
expr_stmt|;
name|restManager
operator|.
name|getManagedResource
argument_list|(
literal|"/config/test/foo2"
argument_list|)
expr_stmt|;
comment|// exception if it isn't registered
block|}
comment|/**    * Tests {@link RestManager}'s responses to REST API requests on /config/managed    * and /schema/managed.  Also tests {@link ManagedWordSetResource} functionality    * through the REST API.    */
annotation|@
name|Test
DECL|method|testRestManagerEndpoints
specifier|public
name|void
name|testRestManagerEndpoints
parameter_list|()
throws|throws
name|Exception
block|{
comment|// relies on these ManagedResources being activated in the schema-rest.xml used by this test
name|assertJQ
argument_list|(
literal|"/schema/managed"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|/*      * TODO: can't assume these will be here unless schema-rest.xml includes these declarations      *               "/managedResources/[0]/class=='org.apache.solr.rest.schema.analysis.ManagedWordSetResource'",              "/managedResources/[0]/resourceId=='/schema/analysis/stopwords/english'",              "/managedResources/[1]/class=='org.apache.solr.rest.schema.analysis.ManagedSynonymFilterFactory$SynonymManager'",              "/managedResources/[1]/resourceId=='/schema/analysis/synonyms/english'");     */
comment|// no pre-existing managed config components
comment|//    assertJQ("/config/managed", "/managedResources==[]");
comment|// add a ManagedWordSetResource for managing protected words (for stemming)
name|String
name|newEndpoint
init|=
literal|"/schema/analysis/protwords/english"
decl_stmt|;
name|assertJPut
argument_list|(
name|newEndpoint
argument_list|,
name|json
argument_list|(
literal|"{ 'class':'solr.ManagedWordSetResource' }"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
literal|"/schema/managed"
argument_list|,
literal|"/managedResources/[0]/class=='org.apache.solr.rest.schema.analysis.ManagedWordSetResource'"
argument_list|,
literal|"/managedResources/[0]/resourceId=='/schema/analysis/protwords/english'"
argument_list|)
expr_stmt|;
comment|// query the resource we just created
name|assertJQ
argument_list|(
name|newEndpoint
argument_list|,
literal|"/wordSet/managedList==[]"
argument_list|)
expr_stmt|;
comment|// add some words to this new word list manager
name|assertJPut
argument_list|(
name|newEndpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"this"
argument_list|,
literal|"is"
argument_list|,
literal|"a"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|newEndpoint
argument_list|,
literal|"/wordSet/managedList==['a','is','test','this']"
argument_list|,
literal|"/wordSet/initArgs=={'ignoreCase':false}"
argument_list|)
expr_stmt|;
comment|// make sure the default is serialized even if not specified
comment|// Test for case-sensitivity - "Test" lookup should fail
name|assertJQ
argument_list|(
name|newEndpoint
operator|+
literal|"/Test"
argument_list|,
literal|"/responseHeader/status==404"
argument_list|)
expr_stmt|;
comment|// Switch to case-insensitive
name|assertJPut
argument_list|(
name|newEndpoint
argument_list|,
name|json
argument_list|(
literal|"{ 'initArgs':{ 'ignoreCase':'true' } }"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// Test for case-insensitivity - "Test" lookup should succeed
name|assertJQ
argument_list|(
name|newEndpoint
operator|+
literal|"/Test"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// Switch to case-sensitive - this request should fail: changing ignoreCase from true to false is not permitted
name|assertJPut
argument_list|(
name|newEndpoint
argument_list|,
name|json
argument_list|(
literal|"{ 'initArgs':{ 'ignoreCase':false } }"
argument_list|)
argument_list|,
literal|"/responseHeader/status==400"
argument_list|)
expr_stmt|;
comment|// Test XML response format
name|assertQ
argument_list|(
name|newEndpoint
operator|+
literal|"?wt=xml"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status']=0"
argument_list|,
literal|"/response/lst[@name='wordSet']/arr[@name='managedList']/str[1]='a'"
argument_list|,
literal|"/response/lst[@name='wordSet']/arr[@name='managedList']/str[2]='is'"
argument_list|,
literal|"/response/lst[@name='wordSet']/arr[@name='managedList']/str[3]='test'"
argument_list|,
literal|"/response/lst[@name='wordSet']/arr[@name='managedList']/str[4]='this'"
argument_list|)
expr_stmt|;
comment|// delete the one we created above
name|assertJDelete
argument_list|(
name|newEndpoint
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// make sure it's really gone
comment|//    assertJQ("/config/managed", "/managedResources==[]");
block|}
annotation|@
name|Test
DECL|method|testReloadFromPersistentStorage
specifier|public
name|void
name|testReloadFromPersistentStorage
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"./"
argument_list|)
decl_stmt|;
name|File
name|unitTestStorageDir
init|=
name|createTempDir
argument_list|(
literal|"testRestManager"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|unitTestStorageDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" is not a directory!"
argument_list|,
name|unitTestStorageDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unitTestStorageDir
operator|.
name|canRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|unitTestStorageDir
operator|.
name|canWrite
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|ioInitArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|ioInitArgs
operator|.
name|add
argument_list|(
name|ManagedResourceStorage
operator|.
name|STORAGE_DIR_INIT_ARG
argument_list|,
name|unitTestStorageDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|StorageIO
name|storageIO
init|=
operator|new
name|ManagedResourceStorage
operator|.
name|FileStorageIO
argument_list|()
decl_stmt|;
name|storageIO
operator|.
name|configure
argument_list|(
name|loader
argument_list|,
name|ioInitArgs
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|RestManager
name|restManager
init|=
operator|new
name|RestManager
argument_list|()
decl_stmt|;
name|restManager
operator|.
name|init
argument_list|(
name|loader
argument_list|,
name|initArgs
argument_list|,
name|storageIO
argument_list|)
expr_stmt|;
comment|// verifies a RestManager can be reloaded from a previous RestManager's data
name|RestManager
name|restManager2
init|=
operator|new
name|RestManager
argument_list|()
decl_stmt|;
name|restManager2
operator|.
name|init
argument_list|(
name|loader
argument_list|,
name|initArgs
argument_list|,
name|storageIO
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResolveResourceId
specifier|public
name|void
name|testResolveResourceId
parameter_list|()
throws|throws
name|Exception
block|{
name|Request
name|testRequest
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|Reference
name|rootRef
init|=
operator|new
name|Reference
argument_list|(
literal|"http://solr.apache.org/"
argument_list|)
decl_stmt|;
name|testRequest
operator|.
name|setRootRef
argument_list|(
name|rootRef
argument_list|)
expr_stmt|;
name|Reference
name|resourceRef
init|=
operator|new
name|Reference
argument_list|(
literal|"http://solr.apache.org/schema/analysis/synonyms/de"
argument_list|)
decl_stmt|;
name|testRequest
operator|.
name|setResourceRef
argument_list|(
name|resourceRef
argument_list|)
expr_stmt|;
name|String
name|resourceId
init|=
name|RestManager
operator|.
name|ManagedEndpoint
operator|.
name|resolveResourceId
argument_list|(
name|testRequest
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|resourceId
argument_list|,
literal|"/schema/analysis/synonyms/de"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResolveResourceIdDecodeUrlEntities
specifier|public
name|void
name|testResolveResourceIdDecodeUrlEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|Request
name|testRequest
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|Reference
name|rootRef
init|=
operator|new
name|Reference
argument_list|(
literal|"http://solr.apache.org/"
argument_list|)
decl_stmt|;
name|testRequest
operator|.
name|setRootRef
argument_list|(
name|rootRef
argument_list|)
expr_stmt|;
name|Reference
name|resourceRef
init|=
operator|new
name|Reference
argument_list|(
literal|"http://solr.apache.org/schema/analysis/synonyms/de/%C3%84ndern"
argument_list|)
decl_stmt|;
name|testRequest
operator|.
name|setResourceRef
argument_list|(
name|resourceRef
argument_list|)
expr_stmt|;
name|String
name|resourceId
init|=
name|RestManager
operator|.
name|ManagedEndpoint
operator|.
name|resolveResourceId
argument_list|(
name|testRequest
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|resourceId
argument_list|,
literal|"/schema/analysis/synonyms/de/Ãndern"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
