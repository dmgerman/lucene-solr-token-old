begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.beans
package|package
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
name|beans
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|beans
operator|.
name|DocumentObjectBinder
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
name|beans
operator|.
name|Field
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
name|impl
operator|.
name|XMLResponseParser
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
name|response
operator|.
name|QueryResponse
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
name|util
operator|.
name|ClientUtils
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
name|SolrDocumentList
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
name|common
operator|.
name|SolrInputField
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
name|SolrDocument
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
name|junit
operator|.
name|Assert
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|Date
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
begin_class
DECL|class|TestDocumentObjectBinder
specifier|public
class|class
name|TestDocumentObjectBinder
extends|extends
name|TestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentObjectBinder
name|binder
init|=
operator|new
name|DocumentObjectBinder
argument_list|()
decl_stmt|;
name|XMLResponseParser
name|parser
init|=
operator|new
name|XMLResponseParser
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
literal|null
decl_stmt|;
name|nl
operator|=
name|parser
operator|.
name|processResponse
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
expr_stmt|;
name|QueryResponse
name|res
init|=
operator|new
name|QueryResponse
argument_list|(
name|nl
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|solDocList
init|=
name|res
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Item
argument_list|>
name|l
init|=
name|binder
operator|.
name|getBeans
argument_list|(
name|Item
operator|.
name|class
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|solDocList
operator|.
name|size
argument_list|()
argument_list|,
name|l
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|solDocList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"features"
argument_list|)
argument_list|,
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|features
argument_list|)
expr_stmt|;
name|Item
name|item
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|item
operator|.
name|id
operator|=
literal|"aaa"
expr_stmt|;
name|item
operator|.
name|categories
operator|=
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"bbb"
block|,
literal|"ccc"
block|}
expr_stmt|;
name|SolrInputDocument
name|out
init|=
name|binder
operator|.
name|toSolrInputDocument
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|item
operator|.
name|id
argument_list|,
name|out
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrInputField
name|catfield
init|=
name|out
operator|.
name|getField
argument_list|(
literal|"cat"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|catfield
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"[aaa, bbb, ccc]"
argument_list|,
name|catfield
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test the error on not settable stuff...
name|NotGettableItem
name|ng
init|=
operator|new
name|NotGettableItem
argument_list|()
decl_stmt|;
name|ng
operator|.
name|setInStock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|out
operator|=
name|binder
operator|.
name|toSolrInputDocument
argument_list|(
name|ng
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should throw an error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
comment|// ok -- this should happen...
block|}
block|}
DECL|method|testSingleVal4Array
specifier|public
name|void
name|testSingleVal4Array
parameter_list|()
block|{
name|DocumentObjectBinder
name|binder
init|=
operator|new
name|DocumentObjectBinder
argument_list|()
decl_stmt|;
name|SolrDocumentList
name|solDocList
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|SolrDocument
name|d
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|solDocList
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Item
argument_list|>
name|l
init|=
name|binder
operator|.
name|getBeans
argument_list|(
name|Item
operator|.
name|class
argument_list|,
name|solDocList
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|categories
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testToAndFromSolrDocument
specifier|public
name|void
name|testToAndFromSolrDocument
parameter_list|()
block|{
name|Item
name|item
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|item
operator|.
name|id
operator|=
literal|"one"
expr_stmt|;
name|item
operator|.
name|inStock
operator|=
literal|false
expr_stmt|;
name|item
operator|.
name|categories
operator|=
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"bbb"
block|,
literal|"ccc"
block|}
expr_stmt|;
name|item
operator|.
name|features
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|item
operator|.
name|categories
argument_list|)
expr_stmt|;
name|DocumentObjectBinder
name|binder
init|=
operator|new
name|DocumentObjectBinder
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|binder
operator|.
name|toSolrInputDocument
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|ClientUtils
operator|.
name|toSolrDocument
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|Item
name|out
init|=
name|binder
operator|.
name|getBeans
argument_list|(
name|Item
operator|.
name|class
argument_list|,
name|docs
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// make sure it came out the same
name|Assert
operator|.
name|assertEquals
argument_list|(
name|item
operator|.
name|id
argument_list|,
name|out
operator|.
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|item
operator|.
name|inStock
argument_list|,
name|out
operator|.
name|inStock
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|item
operator|.
name|categories
operator|.
name|length
argument_list|,
name|out
operator|.
name|categories
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|item
operator|.
name|features
argument_list|,
name|out
operator|.
name|features
argument_list|)
expr_stmt|;
block|}
DECL|class|Item
specifier|public
specifier|static
class|class
name|Item
block|{
annotation|@
name|Field
DECL|field|id
name|String
name|id
decl_stmt|;
annotation|@
name|Field
argument_list|(
literal|"cat"
argument_list|)
DECL|field|categories
name|String
index|[]
name|categories
decl_stmt|;
annotation|@
name|Field
DECL|field|features
name|List
argument_list|<
name|String
argument_list|>
name|features
decl_stmt|;
annotation|@
name|Field
DECL|field|timestamp
name|Date
name|timestamp
decl_stmt|;
annotation|@
name|Field
argument_list|(
literal|"highway_mileage"
argument_list|)
DECL|field|mwyMileage
name|int
name|mwyMileage
decl_stmt|;
DECL|field|inStock
name|boolean
name|inStock
init|=
literal|false
decl_stmt|;
annotation|@
name|Field
DECL|method|setInStock
specifier|public
name|void
name|setInStock
parameter_list|(
name|Boolean
name|b
parameter_list|)
block|{
name|inStock
operator|=
name|b
expr_stmt|;
block|}
comment|// required if you want to fill SolrDocuments with the same annotaion...
DECL|method|isInStock
specifier|public
name|boolean
name|isInStock
parameter_list|()
block|{
return|return
name|inStock
return|;
block|}
block|}
DECL|class|NotGettableItem
specifier|public
specifier|static
class|class
name|NotGettableItem
block|{
annotation|@
name|Field
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|inStock
specifier|private
name|boolean
name|inStock
decl_stmt|;
DECL|field|aaa
specifier|private
name|String
name|aaa
decl_stmt|;
annotation|@
name|Field
DECL|method|setInStock
specifier|public
name|void
name|setInStock
parameter_list|(
name|Boolean
name|b
parameter_list|)
block|{
name|inStock
operator|=
name|b
expr_stmt|;
block|}
DECL|method|getAaa
specifier|public
name|String
name|getAaa
parameter_list|()
block|{
return|return
name|aaa
return|;
block|}
annotation|@
name|Field
DECL|method|setAaa
specifier|public
name|void
name|setAaa
parameter_list|(
name|String
name|aaa
parameter_list|)
block|{
name|this
operator|.
name|aaa
operator|=
name|aaa
expr_stmt|;
block|}
block|}
DECL|field|xml
specifier|public
specifier|static
specifier|final
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<response>"
operator|+
literal|"<lst name=\"responseHeader\"><int name=\"status\">0</int><int name=\"QTime\">0</int><lst name=\"params\"><str name=\"start\">0</str><str name=\"q\">*:*\n"
operator|+
literal|"</str><str name=\"version\">2.2</str><str name=\"rows\">4</str></lst></lst><result name=\"response\" numFound=\"26\" start=\"0\"><doc><arr name=\"cat\">"
operator|+
literal|"<str>electronics</str><str>hard drive</str></arr><arr name=\"features\"><str>7200RPM, 8MB cache, IDE Ultra ATA-133</str>"
operator|+
literal|"<str>NoiseGuard, SilentSeek technology, Fluid Dynamic Bearing (FDB) motor</str></arr><str name=\"id\">SP2514N</str>"
operator|+
literal|"<bool name=\"inStock\">true</bool><str name=\"manu\">Samsung Electronics Co. Ltd.</str><str name=\"name\">Samsung SpinPoint P120 SP2514N - hard drive - 250 GB - ATA-133</str>"
operator|+
literal|"<int name=\"popularity\">6</int><float name=\"price\">92.0</float><str name=\"sku\">SP2514N</str><date name=\"timestamp\">2008-04-16T10:35:57.078Z</date></doc>"
operator|+
literal|"<doc><arr name=\"cat\"><str>electronics</str><str>hard drive</str></arr><arr name=\"features\"><str>SATA 3.0Gb/s, NCQ</str><str>8.5ms seek</str>"
operator|+
literal|"<str>16MB cache</str></arr><str name=\"id\">6H500F0</str><bool name=\"inStock\">true</bool><str name=\"manu\">Maxtor Corp.</str>"
operator|+
literal|"<str name=\"name\">Maxtor DiamondMax 11 - hard drive - 500 GB - SATA-300</str><int name=\"popularity\">6</int><float name=\"price\">350.0</float>"
operator|+
literal|"<str name=\"sku\">6H500F0</str><date name=\"timestamp\">2008-04-16T10:35:57.109Z</date></doc><doc><arr name=\"cat\"><str>electronics</str>"
operator|+
literal|"<str>connector</str></arr><arr name=\"features\"><str>car power adapter, white</str></arr><str name=\"id\">F8V7067-APL-KIT</str>"
operator|+
literal|"<bool name=\"inStock\">false</bool><str name=\"manu\">Belkin</str><str name=\"name\">Belkin Mobile Power Cord for iPod w/ Dock</str>"
operator|+
literal|"<int name=\"popularity\">1</int><float name=\"price\">19.95</float><str name=\"sku\">F8V7067-APL-KIT</str>"
operator|+
literal|"<date name=\"timestamp\">2008-04-16T10:35:57.140Z</date><float name=\"weight\">4.0</float></doc><doc>"
operator|+
literal|"<arr name=\"cat\"><str>electronics</str><str>connector</str></arr><arr name=\"features\">"
operator|+
literal|"<str>car power adapter for iPod, white</str></arr><str name=\"id\">IW-02</str><bool name=\"inStock\">false</bool>"
operator|+
literal|"<str name=\"manu\">Belkin</str><str name=\"name\">iPod&amp; iPod Mini USB 2.0 Cable</str>"
operator|+
literal|"<int name=\"popularity\">1</int><float name=\"price\">11.5</float><str name=\"sku\">IW-02</str>"
operator|+
literal|"<date name=\"timestamp\">2008-04-16T10:35:57.140Z</date><float name=\"weight\">2.0</float></doc></result>\n"
operator|+
literal|"</response>"
decl_stmt|;
block|}
end_class
end_unit
