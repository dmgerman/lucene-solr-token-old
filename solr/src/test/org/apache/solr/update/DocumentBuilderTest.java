begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|SolrInputDocument
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
name|util
operator|.
name|AbstractSolrTestCase
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
name|FieldType
import|;
end_import
begin_comment
comment|/**  *   *  */
end_comment
begin_class
DECL|class|DocumentBuilderTest
specifier|public
class|class
name|DocumentBuilderTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|testBuildDocument
specifier|public
name|void
name|testBuildDocument
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
comment|// undefined field
try|try
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"unknown field"
argument_list|,
literal|12345
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should throw an error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should be bad request"
argument_list|,
literal|400
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNullField
specifier|public
name|void
name|testNullField
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
comment|// make sure a null value is not indexed
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiField
specifier|public
name|void
name|testMultiField
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
comment|// make sure a null value is not indexed
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"home"
argument_list|,
literal|"2.2,3.3"
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|Document
name|out
init|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|get
argument_list|(
literal|"home"
argument_list|)
argument_list|)
expr_stmt|;
comment|//contains the stored value and term vector, if there is one
name|assertNotNull
argument_list|(
name|out
operator|.
name|getField
argument_list|(
literal|"home_0"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"double"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|out
operator|.
name|getField
argument_list|(
literal|"home_1"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"double"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
