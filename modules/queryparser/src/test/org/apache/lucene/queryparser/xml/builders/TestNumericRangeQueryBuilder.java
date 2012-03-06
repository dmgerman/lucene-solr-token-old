begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.xml.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|builders
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|NumericRangeQuery
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
name|Query
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
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|ParserException
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_class
DECL|class|TestNumericRangeQueryBuilder
specifier|public
class|class
name|TestNumericRangeQueryBuilder
extends|extends
name|LuceneTestCase
block|{
DECL|method|testGetFilterHandleNumericParseErrorStrict
specifier|public
name|void
name|testGetFilterHandleNumericParseErrorStrict
parameter_list|()
throws|throws
name|Exception
block|{
name|NumericRangeQueryBuilder
name|filterBuilder
init|=
operator|new
name|NumericRangeQueryBuilder
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='int' lowerTerm='-1' upperTerm='NaN'/>"
decl_stmt|;
name|Document
name|doc
init|=
name|getDocumentFromString
argument_list|(
name|xml
argument_list|)
decl_stmt|;
try|try
block|{
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserException
name|e
parameter_list|)
block|{
return|return;
block|}
name|fail
argument_list|(
literal|"Expected to throw "
operator|+
name|ParserException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|testGetFilterInt
specifier|public
name|void
name|testGetFilterInt
parameter_list|()
throws|throws
name|Exception
block|{
name|NumericRangeQueryBuilder
name|filterBuilder
init|=
operator|new
name|NumericRangeQueryBuilder
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='int' lowerTerm='-1' upperTerm='10'/>"
decl_stmt|;
name|Document
name|doc
init|=
name|getDocumentFromString
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|Query
name|filter
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Integer
argument_list|>
name|numRangeFilter
init|=
operator|(
name|NumericRangeQuery
argument_list|<
name|Integer
argument_list|>
operator|)
name|filter
decl_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xml2
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='int' lowerTerm='-1' upperTerm='10' includeUpper='false'/>"
decl_stmt|;
name|Document
name|doc2
init|=
name|getDocumentFromString
argument_list|(
name|xml2
argument_list|)
decl_stmt|;
name|Query
name|filter2
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc2
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter2
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Integer
argument_list|>
name|numRangeFilter2
init|=
operator|(
name|NumericRangeQuery
operator|)
name|filter2
decl_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter2
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter2
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|numRangeFilter2
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|testGetFilterLong
specifier|public
name|void
name|testGetFilterLong
parameter_list|()
throws|throws
name|Exception
block|{
name|NumericRangeQueryBuilder
name|filterBuilder
init|=
operator|new
name|NumericRangeQueryBuilder
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='LoNg' lowerTerm='-2321' upperTerm='60000000'/>"
decl_stmt|;
name|Document
name|doc
init|=
name|getDocumentFromString
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|Query
name|filter
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Long
argument_list|>
name|numRangeFilter
init|=
operator|(
name|NumericRangeQuery
operator|)
name|filter
decl_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
operator|-
literal|2321L
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
literal|60000000L
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xml2
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='LoNg' lowerTerm='-2321' upperTerm='60000000' includeUpper='false'/>"
decl_stmt|;
name|Document
name|doc2
init|=
name|getDocumentFromString
argument_list|(
name|xml2
argument_list|)
decl_stmt|;
name|Query
name|filter2
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc2
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter2
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Long
argument_list|>
name|numRangeFilter2
init|=
operator|(
name|NumericRangeQuery
operator|)
name|filter2
decl_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
operator|-
literal|2321L
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
literal|60000000L
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter2
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter2
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|numRangeFilter2
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|testGetFilterDouble
specifier|public
name|void
name|testGetFilterDouble
parameter_list|()
throws|throws
name|Exception
block|{
name|NumericRangeQueryBuilder
name|filterBuilder
init|=
operator|new
name|NumericRangeQueryBuilder
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='doubLe' lowerTerm='-23.21' upperTerm='60000.00023'/>"
decl_stmt|;
name|Document
name|doc
init|=
name|getDocumentFromString
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|Query
name|filter
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Double
argument_list|>
name|numRangeFilter
init|=
operator|(
name|NumericRangeQuery
operator|)
name|filter
decl_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
operator|-
literal|23.21d
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
literal|60000.00023d
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xml2
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='doubLe' lowerTerm='-23.21' upperTerm='60000.00023' includeUpper='false'/>"
decl_stmt|;
name|Document
name|doc2
init|=
name|getDocumentFromString
argument_list|(
name|xml2
argument_list|)
decl_stmt|;
name|Query
name|filter2
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc2
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter2
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Double
argument_list|>
name|numRangeFilter2
init|=
operator|(
name|NumericRangeQuery
operator|)
name|filter2
decl_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
operator|-
literal|23.21d
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|valueOf
argument_list|(
literal|60000.00023d
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter2
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter2
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|numRangeFilter2
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|testGetFilterFloat
specifier|public
name|void
name|testGetFilterFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|NumericRangeQueryBuilder
name|filterBuilder
init|=
operator|new
name|NumericRangeQueryBuilder
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='FLOAT' lowerTerm='-2.321432' upperTerm='32432.23'/>"
decl_stmt|;
name|Document
name|doc
init|=
name|getDocumentFromString
argument_list|(
name|xml
argument_list|)
decl_stmt|;
name|Query
name|filter
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Float
argument_list|>
name|numRangeFilter
init|=
operator|(
name|NumericRangeQuery
operator|)
name|filter
decl_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
operator|-
literal|2.321432f
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
literal|32432.23f
argument_list|)
argument_list|,
name|numRangeFilter
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xml2
init|=
literal|"<NumericRangeQuery fieldName='AGE' type='FLOAT' lowerTerm='-2.321432' upperTerm='32432.23' includeUpper='false' precisionStep='2' />"
decl_stmt|;
name|Document
name|doc2
init|=
name|getDocumentFromString
argument_list|(
name|xml2
argument_list|)
decl_stmt|;
name|Query
name|filter2
init|=
name|filterBuilder
operator|.
name|getQuery
argument_list|(
name|doc2
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter2
operator|instanceof
name|NumericRangeQuery
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|NumericRangeQuery
argument_list|<
name|Float
argument_list|>
name|numRangeFilter2
init|=
operator|(
name|NumericRangeQuery
operator|)
name|filter2
decl_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
operator|-
literal|2.321432f
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|valueOf
argument_list|(
literal|32432.23f
argument_list|)
argument_list|,
name|numRangeFilter2
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AGE"
argument_list|,
name|numRangeFilter2
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numRangeFilter2
operator|.
name|includesMin
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|numRangeFilter2
operator|.
name|includesMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocumentFromString
specifier|private
specifier|static
name|Document
name|getDocumentFromString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
block|{
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|str
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit
