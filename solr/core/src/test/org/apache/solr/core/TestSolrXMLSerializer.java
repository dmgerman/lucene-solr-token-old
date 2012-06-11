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
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Properties
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|SolrXMLSerializer
operator|.
name|SolrCoreXMLDef
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
name|SolrXMLSerializer
operator|.
name|SolrXMLDef
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_class
DECL|class|TestSolrXMLSerializer
specifier|public
class|class
name|TestSolrXMLSerializer
extends|extends
name|LuceneTestCase
block|{
DECL|field|xpathFactory
specifier|private
specifier|static
specifier|final
name|XPathFactory
name|xpathFactory
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|defaultCoreNameKey
specifier|private
specifier|static
specifier|final
name|String
name|defaultCoreNameKey
init|=
literal|"defaultCoreName"
decl_stmt|;
DECL|field|defaultCoreNameVal
specifier|private
specifier|static
specifier|final
name|String
name|defaultCoreNameVal
init|=
literal|"collection1"
decl_stmt|;
DECL|field|peristentKey
specifier|private
specifier|static
specifier|final
name|String
name|peristentKey
init|=
literal|"persistent"
decl_stmt|;
DECL|field|persistentVal
specifier|private
specifier|static
specifier|final
name|String
name|persistentVal
init|=
literal|"true"
decl_stmt|;
DECL|field|sharedLibKey
specifier|private
specifier|static
specifier|final
name|String
name|sharedLibKey
init|=
literal|"sharedLib"
decl_stmt|;
DECL|field|sharedLibVal
specifier|private
specifier|static
specifier|final
name|String
name|sharedLibVal
init|=
literal|"true"
decl_stmt|;
DECL|field|adminPathKey
specifier|private
specifier|static
specifier|final
name|String
name|adminPathKey
init|=
literal|"adminPath"
decl_stmt|;
DECL|field|adminPathVal
specifier|private
specifier|static
specifier|final
name|String
name|adminPathVal
init|=
literal|"/admin"
decl_stmt|;
DECL|field|shareSchemaKey
specifier|private
specifier|static
specifier|final
name|String
name|shareSchemaKey
init|=
literal|"admin"
decl_stmt|;
DECL|field|shareSchemaVal
specifier|private
specifier|static
specifier|final
name|String
name|shareSchemaVal
init|=
literal|"true"
decl_stmt|;
DECL|field|instanceDirKey
specifier|private
specifier|static
specifier|final
name|String
name|instanceDirKey
init|=
literal|"instanceDir"
decl_stmt|;
DECL|field|instanceDirVal
specifier|private
specifier|static
specifier|final
name|String
name|instanceDirVal
init|=
literal|"core1"
decl_stmt|;
annotation|@
name|Test
DECL|method|basicUsageTest
specifier|public
name|void
name|basicUsageTest
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrXMLSerializer
name|serializer
init|=
operator|new
name|SolrXMLSerializer
argument_list|()
decl_stmt|;
name|SolrXMLDef
name|solrXMLDef
init|=
name|getTestSolrXMLDef
argument_list|(
name|defaultCoreNameKey
argument_list|,
name|defaultCoreNameVal
argument_list|,
name|peristentKey
argument_list|,
name|persistentVal
argument_list|,
name|sharedLibKey
argument_list|,
name|sharedLibVal
argument_list|,
name|adminPathKey
argument_list|,
name|adminPathVal
argument_list|,
name|shareSchemaKey
argument_list|,
name|shareSchemaVal
argument_list|,
name|instanceDirKey
argument_list|,
name|instanceDirVal
argument_list|)
decl_stmt|;
name|Writer
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
block|{
name|serializer
operator|.
name|persist
argument_list|(
name|w
argument_list|,
name|solrXMLDef
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertResults
argument_list|(
operator|(
operator|(
name|StringWriter
operator|)
name|w
operator|)
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
comment|// again with default file
name|File
name|tmpFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"solr"
argument_list|,
literal|".xml"
argument_list|,
name|TEMP_DIR
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|persistFile
argument_list|(
name|tmpFile
argument_list|,
name|solrXMLDef
argument_list|)
expr_stmt|;
name|assertResults
argument_list|(
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|tmpFile
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResults
specifier|private
name|void
name|assertResults
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|UnsupportedEncodingException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|XPathExpressionException
block|{
name|DocumentBuilder
name|builder
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|BufferedInputStream
name|is
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|document
decl_stmt|;
try|try
block|{
comment|//      is.mark(0);
comment|//      System.out.println("SolrXML:" + IOUtils.toString(is, "UTF-8"));
comment|//      is.reset();
name|document
operator|=
name|builder
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr[@"
operator|+
name|peristentKey
operator|+
literal|"='"
operator|+
name|persistentVal
operator|+
literal|"']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr[@"
operator|+
name|sharedLibKey
operator|+
literal|"='"
operator|+
name|sharedLibVal
operator|+
literal|"']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr/cores[@"
operator|+
name|defaultCoreNameKey
operator|+
literal|"='"
operator|+
name|defaultCoreNameVal
operator|+
literal|"']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr/cores[@"
operator|+
name|adminPathKey
operator|+
literal|"='"
operator|+
name|adminPathVal
operator|+
literal|"']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|(
literal|"/solr/cores/core[@"
operator|+
name|instanceDirKey
operator|+
literal|"='"
operator|+
name|instanceDirVal
operator|+
literal|"']"
argument_list|,
name|document
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getTestSolrXMLDef
specifier|private
name|SolrXMLDef
name|getTestSolrXMLDef
parameter_list|(
name|String
name|defaultCoreNameKey
parameter_list|,
name|String
name|defaultCoreNameVal
parameter_list|,
name|String
name|peristentKey
parameter_list|,
name|String
name|persistentVal
parameter_list|,
name|String
name|sharedLibKey
parameter_list|,
name|String
name|sharedLibVal
parameter_list|,
name|String
name|adminPathKey
parameter_list|,
name|String
name|adminPathVal
parameter_list|,
name|String
name|shareSchemaKey
parameter_list|,
name|String
name|shareSchemaVal
parameter_list|,
name|String
name|instanceDirKey
parameter_list|,
name|String
name|instanceDirVal
parameter_list|)
block|{
comment|//<solr attrib="value">
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|rootSolrAttribs
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
name|rootSolrAttribs
operator|.
name|put
argument_list|(
name|sharedLibKey
argument_list|,
name|sharedLibVal
argument_list|)
expr_stmt|;
name|rootSolrAttribs
operator|.
name|put
argument_list|(
name|peristentKey
argument_list|,
name|persistentVal
argument_list|)
expr_stmt|;
comment|//<solr attrib="value"><cores attrib="value">
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coresAttribs
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
name|coresAttribs
operator|.
name|put
argument_list|(
name|adminPathKey
argument_list|,
name|adminPathVal
argument_list|)
expr_stmt|;
name|coresAttribs
operator|.
name|put
argument_list|(
name|shareSchemaKey
argument_list|,
name|shareSchemaVal
argument_list|)
expr_stmt|;
name|coresAttribs
operator|.
name|put
argument_list|(
name|defaultCoreNameKey
argument_list|,
name|defaultCoreNameVal
argument_list|)
expr_stmt|;
name|SolrXMLDef
name|solrXMLDef
init|=
operator|new
name|SolrXMLDef
argument_list|()
decl_stmt|;
comment|//<solr attrib="value"><cores attrib="value"><core attrib="value">
name|List
argument_list|<
name|SolrCoreXMLDef
argument_list|>
name|solrCoreXMLDefs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrCoreXMLDef
argument_list|>
argument_list|()
decl_stmt|;
name|SolrCoreXMLDef
name|coreDef
init|=
operator|new
name|SolrCoreXMLDef
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coreAttribs
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
name|coreAttribs
operator|.
name|put
argument_list|(
name|instanceDirKey
argument_list|,
name|instanceDirVal
argument_list|)
expr_stmt|;
name|coreDef
operator|.
name|coreAttribs
operator|=
name|coreAttribs
expr_stmt|;
name|coreDef
operator|.
name|coreProperties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|solrCoreXMLDefs
operator|.
name|add
argument_list|(
name|coreDef
argument_list|)
expr_stmt|;
name|solrXMLDef
operator|.
name|coresDefs
operator|=
name|solrCoreXMLDefs
expr_stmt|;
name|Properties
name|containerProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|solrXMLDef
operator|.
name|containerProperties
operator|=
name|containerProperties
expr_stmt|;
name|solrXMLDef
operator|.
name|solrAttribs
operator|=
name|rootSolrAttribs
expr_stmt|;
name|solrXMLDef
operator|.
name|coresAttribs
operator|=
name|coresAttribs
expr_stmt|;
return|return
name|solrXMLDef
return|;
block|}
DECL|method|exists
specifier|public
specifier|static
name|boolean
name|exists
parameter_list|(
name|String
name|xpathStr
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|XPathExpressionException
block|{
name|XPath
name|xpath
init|=
name|xpathFactory
operator|.
name|newXPath
argument_list|()
decl_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|xpathStr
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
block|}
end_class
end_unit
