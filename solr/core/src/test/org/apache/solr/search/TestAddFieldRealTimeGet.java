begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|schema
operator|.
name|TestManagedSchema
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
name|Collections
import|;
end_import
begin_class
DECL|class|TestAddFieldRealTimeGet
specifier|public
class|class
name|TestAddFieldRealTimeGet
extends|extends
name|TestRTGBase
block|{
DECL|field|tmpSolrHome
specifier|private
specifier|static
name|File
name|tmpSolrHome
decl_stmt|;
DECL|field|tmpConfDir
specifier|private
specifier|static
name|File
name|tmpConfDir
decl_stmt|;
DECL|field|collection
specifier|private
specifier|static
specifier|final
name|String
name|collection
init|=
literal|"collection1"
decl_stmt|;
DECL|field|confDir
specifier|private
specifier|static
specifier|final
name|String
name|confDir
init|=
name|collection
operator|+
literal|"/conf"
decl_stmt|;
annotation|@
name|Before
DECL|method|initManagedSchemaCore
specifier|private
name|void
name|initManagedSchemaCore
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|tmpSolrHomePath
init|=
name|dataDir
operator|+
name|File
operator|.
name|separator
operator|+
name|TestManagedSchema
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|tmpSolrHome
operator|=
operator|new
name|File
argument_list|(
name|tmpSolrHomePath
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|tmpConfDir
operator|=
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|File
name|testHomeConfDir
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
name|confDir
argument_list|)
decl_stmt|;
specifier|final
name|String
name|configFileName
init|=
literal|"solrconfig-managed-schema.xml"
decl_stmt|;
specifier|final
name|String
name|schemaFileName
init|=
literal|"schema-id-and-version-fields-only.xml"
decl_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
name|configFileName
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
name|schemaFileName
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFileToDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|testHomeConfDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
name|tmpConfDir
argument_list|)
expr_stmt|;
comment|// initCore will trigger an upgrade to managed schema, since the solrconfig has
comment|//<schemaFactory class="ManagedIndexSchemaFactory" ... />
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
literal|"true"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
name|configFileName
argument_list|,
name|schemaFileName
argument_list|,
name|tmpSolrHome
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|newFieldName
init|=
literal|"newfield"
decl_stmt|;
name|String
name|newFieldType
init|=
literal|"string"
decl_stmt|;
name|String
name|newFieldValue
init|=
literal|"xyz"
decl_stmt|;
name|assertFailedU
argument_list|(
literal|"Should fail due to unknown field '"
operator|+
name|newFieldName
operator|+
literal|"'"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|newFieldName
argument_list|,
name|newFieldValue
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|newField
init|=
name|schema
operator|.
name|newField
argument_list|(
name|newFieldName
argument_list|,
name|newFieldType
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSchema
name|newSchema
init|=
name|schema
operator|.
name|addField
argument_list|(
name|newField
argument_list|)
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
name|String
name|newFieldKeyValue
init|=
literal|"'"
operator|+
name|newFieldName
operator|+
literal|"':'"
operator|+
name|newFieldValue
operator|+
literal|"'"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|newFieldName
argument_list|,
name|newFieldValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|newFieldName
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1',"
operator|+
name|newFieldKeyValue
operator|+
literal|"}}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|newFieldName
argument_list|)
argument_list|,
literal|"=={'response':{'numFound':1,'start':0,'docs':[{'id':'1',"
operator|+
name|newFieldKeyValue
operator|+
literal|"}]}}"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|newFieldName
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1',"
operator|+
name|newFieldKeyValue
operator|+
literal|"}}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|newFieldName
argument_list|)
argument_list|,
literal|"=={'response':{'numFound':1,'start':0,'docs':[{'id':'1',"
operator|+
name|newFieldKeyValue
operator|+
literal|"}]}}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
