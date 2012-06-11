begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
begin_comment
comment|/**  * Test for FieldReaderDataSource  *  *  * @see org.apache.solr.handler.dataimport.FieldReaderDataSource  * @since 1.4  */
end_comment
begin_class
DECL|class|TestFieldReader
specifier|public
class|class
name|TestFieldReader
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|Test
DECL|method|simple
specifier|public
name|void
name|simple
parameter_list|()
block|{
name|DataImporter
name|di
init|=
operator|new
name|DataImporter
argument_list|()
decl_stmt|;
name|di
operator|.
name|loadAndInit
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|TestDocBuilder
operator|.
name|SolrWriterImpl
name|sw
init|=
operator|new
name|TestDocBuilder
operator|.
name|SolrWriterImpl
argument_list|()
decl_stmt|;
name|RequestInfo
name|rp
init|=
operator|new
name|RequestInfo
argument_list|(
name|createMap
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"xml"
argument_list|,
name|xml
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from a"
argument_list|,
name|l
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|di
operator|.
name|runCmd
argument_list|(
name|rp
argument_list|,
name|sw
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sw
operator|.
name|docs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"y"
argument_list|)
argument_list|,
literal|"Hello"
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
block|}
DECL|field|config
name|String
name|config
init|=
literal|"<dataConfig>\n"
operator|+
literal|"<dataSource type=\"FieldReaderDataSource\" name=\"f\"/>\n"
operator|+
literal|"<dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>\n"
operator|+
literal|"<entity name=\"a\" query=\"select * from a\">\n"
operator|+
literal|"<entity name=\"b\" dataSource=\"f\" processor=\"XPathEntityProcessor\" forEach=\"/x\" dataField=\"a.xml\">\n"
operator|+
literal|"<field column=\"y\" xpath=\"/x/y\"/>\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</entity>\n"
operator|+
literal|"</document>\n"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|xml
name|String
name|xml
init|=
literal|"<x>\n"
operator|+
literal|"<y>Hello</y>\n"
operator|+
literal|"</x>"
decl_stmt|;
block|}
end_class
end_unit
