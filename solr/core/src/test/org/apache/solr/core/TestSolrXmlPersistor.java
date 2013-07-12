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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|List
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import
begin_class
DECL|class|TestSolrXmlPersistor
specifier|public
class|class
name|TestSolrXmlPersistor
block|{
DECL|field|EMPTY_CD_LIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|EMPTY_CD_LIST
init|=
name|ImmutableList
operator|.
expr|<
name|CoreDescriptor
operator|>
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|selfClosingCoresTagIsPersisted
specifier|public
name|void
name|selfClosingCoresTagIsPersisted
parameter_list|()
block|{
specifier|final
name|String
name|solrxml
init|=
literal|"<solr><cores adminHandler=\"/admin\"/></solr>"
decl_stmt|;
name|SolrXMLCoresLocator
name|persistor
init|=
operator|new
name|SolrXMLCoresLocator
argument_list|(
operator|new
name|File
argument_list|(
literal|"testfile.xml"
argument_list|)
argument_list|,
name|solrxml
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|persistor
operator|.
name|buildSolrXML
argument_list|(
name|EMPTY_CD_LIST
argument_list|)
argument_list|,
literal|"<solr><cores adminHandler=\"/admin\"></cores></solr>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|emptyCoresTagIsPersisted
specifier|public
name|void
name|emptyCoresTagIsPersisted
parameter_list|()
block|{
specifier|final
name|String
name|solrxml
init|=
literal|"<solr><cores adminHandler=\"/admin\"></cores></solr>"
decl_stmt|;
name|SolrXMLCoresLocator
name|persistor
init|=
operator|new
name|SolrXMLCoresLocator
argument_list|(
operator|new
name|File
argument_list|(
literal|"testfile.xml"
argument_list|)
argument_list|,
name|solrxml
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|persistor
operator|.
name|buildSolrXML
argument_list|(
name|EMPTY_CD_LIST
argument_list|)
argument_list|,
literal|"<solr><cores adminHandler=\"/admin\"></cores></solr>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|emptySolrXmlIsPersisted
specifier|public
name|void
name|emptySolrXmlIsPersisted
parameter_list|()
block|{
specifier|final
name|String
name|solrxml
init|=
literal|"<solr></solr>"
decl_stmt|;
name|SolrXMLCoresLocator
name|persistor
init|=
operator|new
name|SolrXMLCoresLocator
argument_list|(
operator|new
name|File
argument_list|(
literal|"testfile.xml"
argument_list|)
argument_list|,
name|solrxml
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|persistor
operator|.
name|buildSolrXML
argument_list|(
name|EMPTY_CD_LIST
argument_list|)
argument_list|,
literal|"<solr><cores></cores></solr>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simpleCoreDescriptorIsPersisted
specifier|public
name|void
name|simpleCoreDescriptorIsPersisted
parameter_list|()
block|{
specifier|final
name|String
name|solrxml
init|=
literal|"<solr><cores></cores></solr>"
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/example/solr"
argument_list|)
decl_stmt|;
name|CoreContainer
name|cc
init|=
operator|new
name|CoreContainer
argument_list|(
name|loader
argument_list|)
decl_stmt|;
specifier|final
name|CoreDescriptor
name|cd
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
literal|"testcore"
argument_list|,
literal|"instance/dir/"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|cds
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|cd
argument_list|)
decl_stmt|;
name|SolrXMLCoresLocator
name|persistor
init|=
operator|new
name|SolrXMLCoresLocator
argument_list|(
operator|new
name|File
argument_list|(
literal|"testfile.xml"
argument_list|)
argument_list|,
name|solrxml
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|persistor
operator|.
name|buildSolrXML
argument_list|(
name|cds
argument_list|)
argument_list|,
literal|"<solr><cores>"
operator|+
name|SolrXMLCoresLocator
operator|.
name|NEWLINE
operator|+
literal|"<core name=\"testcore\" instanceDir=\"instance/dir/\"/>"
operator|+
name|SolrXMLCoresLocator
operator|.
name|NEWLINE
operator|+
literal|"</cores></solr>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|shardHandlerInfoIsPersisted
specifier|public
name|void
name|shardHandlerInfoIsPersisted
parameter_list|()
block|{
specifier|final
name|String
name|solrxml
init|=
literal|"<solr>"
operator|+
literal|"<cores adminHandler=\"whatever\">"
operator|+
literal|"<core name=\"testcore\" instanceDir=\"instance/dir/\"/>"
operator|+
literal|"<shardHandlerFactory name=\"shardHandlerFactory\" class=\"HttpShardHandlerFactory\">"
operator|+
literal|"<int name=\"socketTimeout\">${socketTimeout:500}</int>"
operator|+
literal|"<str name=\"arbitrary\">arbitraryValue</str>"
operator|+
literal|"</shardHandlerFactory>"
operator|+
literal|"</cores>"
operator|+
literal|"</solr>"
decl_stmt|;
name|SolrXMLCoresLocator
name|locator
init|=
operator|new
name|SolrXMLCoresLocator
argument_list|(
operator|new
name|File
argument_list|(
literal|"testfile.xml"
argument_list|)
argument_list|,
name|solrxml
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|locator
operator|.
name|getTemplate
argument_list|()
operator|.
name|contains
argument_list|(
literal|"{{CORES_PLACEHOLDER}}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|locator
operator|.
name|getTemplate
argument_list|()
operator|.
name|contains
argument_list|(
literal|"<shardHandlerFactory "
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|locator
operator|.
name|getTemplate
argument_list|()
operator|.
name|contains
argument_list|(
literal|"${socketTimeout:500}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simpleShardHandlerInfoIsPersisted
specifier|public
name|void
name|simpleShardHandlerInfoIsPersisted
parameter_list|()
block|{
specifier|final
name|String
name|solrxml
init|=
literal|"<solr>"
operator|+
literal|"<cores adminHandler=\"whatever\">"
operator|+
literal|"<core name=\"testcore\" instanceDir=\"instance/dir/\"/>"
operator|+
literal|"<shardHandlerFactory name=\"shardHandlerFactory\" class=\"HttpShardHandlerFactory\"/>"
operator|+
literal|"</cores>"
operator|+
literal|"</solr>"
decl_stmt|;
name|SolrXMLCoresLocator
name|locator
init|=
operator|new
name|SolrXMLCoresLocator
argument_list|(
operator|new
name|File
argument_list|(
literal|"testfile.xml"
argument_list|)
argument_list|,
name|solrxml
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|locator
operator|.
name|getTemplate
argument_list|()
operator|.
name|contains
argument_list|(
literal|"{{CORES_PLACEHOLDER}}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|locator
operator|.
name|getTemplate
argument_list|()
operator|.
name|contains
argument_list|(
literal|"<shardHandlerFactory "
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|complexXmlIsParsed
specifier|public
name|void
name|complexXmlIsParsed
parameter_list|()
block|{
name|SolrXMLCoresLocator
name|locator
init|=
operator|new
name|SolrXMLCoresLocator
argument_list|(
operator|new
name|File
argument_list|(
literal|"testfile.xml"
argument_list|)
argument_list|,
name|TestSolrXmlPersistence
operator|.
name|SOLR_XML_LOTS_SYSVARS
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|locator
operator|.
name|getTemplate
argument_list|()
operator|.
name|contains
argument_list|(
literal|"{{CORES_PLACEHOLDER}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
