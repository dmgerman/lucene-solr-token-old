begin_unit
begin_package
DECL|package|org.apache.solr.rest.schema.analysis
package|package
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
name|ArrayList
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|solr
operator|.
name|util
operator|.
name|RestTestBase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
import|;
end_import
begin_class
DECL|class|TestManagedSynonymFilterFactory
specifier|public
class|class
name|TestManagedSynonymFilterFactory
extends|extends
name|RestTestBase
block|{
DECL|field|tmpSolrHome
specifier|private
specifier|static
name|File
name|tmpSolrHome
decl_stmt|;
comment|/**    * Setup to make the schema mutable    */
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|tmpSolrHome
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
argument_list|,
name|tmpSolrHome
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrSchemaRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrSchemaRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrRestApi
argument_list|,
literal|"/schema/*"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|createJettyAndHarness
argument_list|(
name|tmpSolrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-rest.xml"
argument_list|,
literal|"/solr"
argument_list|,
literal|true
argument_list|,
name|extraServlets
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|private
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tmpSolrHome
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"enable.update.log"
argument_list|)
expr_stmt|;
if|if
condition|(
name|restTestHarness
operator|!=
literal|null
condition|)
block|{
name|restTestHarness
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|restTestHarness
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testManagedSynonyms
specifier|public
name|void
name|testManagedSynonyms
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this endpoint depends on at least one field type containing the following
comment|// declaration in the schema-rest.xml:
comment|//
comment|//<filter class="solr.ManagedSynonymFilterFactory" managed="english" />
comment|//
name|String
name|endpoint
init|=
literal|"/schema/analysis/synonyms/english"
decl_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/initArgs/ignoreCase==false"
argument_list|,
literal|"/synonymMappings/managedMap=={}"
argument_list|)
expr_stmt|;
comment|// put a new mapping into the synonyms
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|syns
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|syns
operator|.
name|put
argument_list|(
literal|"happy"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"glad"
argument_list|,
literal|"cheerful"
argument_list|,
literal|"joyful"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|syns
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/managedMap/happy==['cheerful','glad','joyful']"
argument_list|)
expr_stmt|;
comment|// request to a specific mapping
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/happy"
argument_list|,
literal|"/happy==['cheerful','glad','joyful']"
argument_list|)
expr_stmt|;
comment|// does not exist
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/sad"
argument_list|,
literal|"/error/code==404"
argument_list|)
expr_stmt|;
comment|// verify the user can update the ignoreCase initArg
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|json
argument_list|(
literal|"{ 'initArgs':{ 'ignoreCase':true } }"
argument_list|)
argument_list|,
literal|"responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/initArgs/ignoreCase==true"
argument_list|)
expr_stmt|;
name|syns
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|syns
operator|.
name|put
argument_list|(
literal|"sad"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"unhappy"
argument_list|)
argument_list|)
expr_stmt|;
name|syns
operator|.
name|put
argument_list|(
literal|"SAD"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bummed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|syns
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/managedMap/sad==['unhappy']"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/managedMap/SAD==['bummed']"
argument_list|)
expr_stmt|;
comment|// expect a union of values when requesting the "sad" child
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/sad"
argument_list|,
literal|"/sad==['bummed','unhappy']"
argument_list|)
expr_stmt|;
comment|// verify delete works
name|assertJDelete
argument_list|(
name|endpoint
operator|+
literal|"/sad"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/managedMap=={'happy':['cheerful','glad','joyful']}"
argument_list|)
expr_stmt|;
comment|// should fail with 404 as foo doesn't exist
name|assertJDelete
argument_list|(
name|endpoint
operator|+
literal|"/foo"
argument_list|,
literal|"/error/code==404"
argument_list|)
expr_stmt|;
comment|// verify that a newly added synonym gets expanded on the query side after core reload
name|String
name|newFieldName
init|=
literal|"managed_en_field"
decl_stmt|;
comment|// make sure the new field doesn't already exist
name|assertQ
argument_list|(
literal|"/schema/fields/"
operator|+
name|newFieldName
operator|+
literal|"?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
comment|// add the new field
name|assertJPut
argument_list|(
literal|"/schema/fields/"
operator|+
name|newFieldName
argument_list|,
name|json
argument_list|(
literal|"{'type':'managed_en'}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// make sure the new field exists now
name|assertQ
argument_list|(
literal|"/schema/fields/"
operator|+
name|newFieldName
operator|+
literal|"?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|newFieldName
argument_list|,
literal|"I am a happy test today but yesterday I was angry"
argument_list|,
literal|"id"
argument_list|,
literal|"5150"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/select?q="
operator|+
name|newFieldName
operator|+
literal|":angry"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='5150']"
argument_list|)
expr_stmt|;
comment|// add a mapping that will expand a query for "mad" to match docs with "angry"
name|syns
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|syns
operator|.
name|put
argument_list|(
literal|"mad"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"angry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|syns
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/managedMap/mad==['angry']"
argument_list|)
expr_stmt|;
comment|// should not match as the synonym mapping between mad and angry does not
comment|// get applied until core reload
name|assertQ
argument_list|(
literal|"/select?q="
operator|+
name|newFieldName
operator|+
literal|":mad"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='0']"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|reload
argument_list|()
expr_stmt|;
comment|// now query for mad and we should see our test doc
name|assertQ
argument_list|(
literal|"/select?q="
operator|+
name|newFieldName
operator|+
literal|":mad"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='5150']"
argument_list|)
expr_stmt|;
comment|// test for SOLR-6015
name|syns
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|syns
operator|.
name|put
argument_list|(
literal|"mb"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"megabyte"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|syns
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|syns
operator|.
name|put
argument_list|(
literal|"MB"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"MiB"
argument_list|,
literal|"Megabyte"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|syns
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/MB"
argument_list|,
literal|"/MB==['Megabyte','MiB','megabyte']"
argument_list|)
expr_stmt|;
comment|// test for SOLR-6878 - by default, expand is true, but only applies when sending in a list
name|List
argument_list|<
name|String
argument_list|>
name|m2mSyns
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|m2mSyns
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"funny"
argument_list|,
literal|"entertaining"
argument_list|,
literal|"whimiscal"
argument_list|,
literal|"jocular"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|m2mSyns
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/funny"
argument_list|,
literal|"/funny==['entertaining','jocular','whimiscal']"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/entertaining"
argument_list|,
literal|"/entertaining==['funny','jocular','whimiscal']"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/jocular"
argument_list|,
literal|"/jocular==['entertaining','funny','whimiscal']"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/whimiscal"
argument_list|,
literal|"/whimiscal==['entertaining','funny','jocular']"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Can we add and remove stopwords with umlauts    */
annotation|@
name|Test
DECL|method|testCanHandleDecodingAndEncodingForSynonyms
specifier|public
name|void
name|testCanHandleDecodingAndEncodingForSynonyms
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|endpoint
init|=
literal|"/schema/analysis/synonyms/german"
decl_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/initArgs/ignoreCase==false"
argument_list|,
literal|"/synonymMappings/managedMap=={}"
argument_list|)
expr_stmt|;
comment|// does not exist
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/frÃ¶hlich"
argument_list|,
literal|"/error/code==404"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|syns
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// now put a synonym
name|syns
operator|.
name|put
argument_list|(
literal|"frÃ¶hlich"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"glÃ¼cklick"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|syns
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// and check if it exists
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/synonymMappings/managedMap/frÃ¶hlich==['glÃ¼cklick']"
argument_list|)
expr_stmt|;
comment|// verify delete works
name|assertJDelete
argument_list|(
name|endpoint
operator|+
literal|"/frÃ¶hlich"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// was it really deleted?
name|assertJDelete
argument_list|(
name|endpoint
operator|+
literal|"/frÃ¶hlich"
argument_list|,
literal|"/error/code==404"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
