begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
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
name|Map
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
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|CommonParams
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
name|MapSolrParams
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
name|SolrCore
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|search
operator|.
name|similarities
operator|.
name|MockConfigurableSimilarityProvider
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
name|search
operator|.
name|similarities
operator|.
name|SimilarityProvider
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
DECL|class|IndexSchemaTest
specifier|public
class|class
name|IndexSchemaTest
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test assumes the schema includes:    *<dynamicField name="dynamic_*" type="string" indexed="true" stored="true"/>    *<dynamicField name="*_dynamic" type="string" indexed="true" stored="true"/>    */
annotation|@
name|Test
DECL|method|testDynamicCopy
specifier|public
name|void
name|testDynamicCopy
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"title"
argument_list|,
literal|"test"
argument_list|,
literal|"aaa_dynamic"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"title:test"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Make sure they got in"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"aaa_dynamic:aaa"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"dynamic source"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"dynamic_aaa:aaa"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"dynamic destination"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRuntimeFieldCreation
specifier|public
name|void
name|testRuntimeFieldCreation
parameter_list|()
block|{
comment|// any field manipulation needs to happen when you know the core will not
comment|// be accepting any requests.  Typically this is done within the inform()
comment|// method.  Since this is a single threaded test, we can change the fields
comment|// willi-nilly
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|String
name|fieldName
init|=
literal|"runtimefield"
decl_stmt|;
name|SchemaField
name|sf
init|=
operator|new
name|SchemaField
argument_list|(
name|fieldName
argument_list|,
name|schema
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
literal|"string"
argument_list|)
argument_list|)
decl_stmt|;
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|sf
argument_list|)
expr_stmt|;
comment|// also register a new copy field (from our new field)
name|schema
operator|.
name|registerCopyField
argument_list|(
name|fieldName
argument_list|,
literal|"dynamic_runtime"
argument_list|)
expr_stmt|;
name|schema
operator|.
name|refreshAnalyzers
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"title"
argument_list|,
literal|"test"
argument_list|,
name|fieldName
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
name|fieldName
operator|+
literal|":aaa"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Make sure they got in"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
comment|// Check to see if our copy field made it out safely
name|query
operator|.
name|setQuery
argument_list|(
literal|"dynamic_runtime:aaa"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Make sure they got in"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsDynamicField
specifier|public
name|void
name|testIsDynamicField
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|isDynamicField
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|schema
operator|.
name|isDynamicField
argument_list|(
literal|"aaa_i"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|isDynamicField
argument_list|(
literal|"no_such_field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProperties
specifier|public
name|void
name|testProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|multiValued
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
