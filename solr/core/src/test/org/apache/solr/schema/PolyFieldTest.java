begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|List
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
name|index
operator|.
name|IndexableField
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|BooleanClause
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
name|BooleanQuery
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
name|core
operator|.
name|SolrCore
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
begin_comment
comment|/**  * Test a whole slew of things related to PolyFields  */
end_comment
begin_class
DECL|class|PolyFieldTest
specifier|public
class|class
name|PolyFieldTest
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
annotation|@
name|Test
DECL|method|testSchemaBasics
specifier|public
name|void
name|testSchemaBasics
parameter_list|()
throws|throws
name|Exception
block|{
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
name|home
init|=
name|schema
operator|.
name|getField
argument_list|(
literal|"home"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|home
operator|.
name|isPolyField
argument_list|()
argument_list|)
expr_stmt|;
name|SchemaField
index|[]
name|dynFields
init|=
name|schema
operator|.
name|getDynamicFieldPrototypes
argument_list|()
decl_stmt|;
name|boolean
name|seen
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SchemaField
name|dynField
range|:
name|dynFields
control|)
block|{
if|if
condition|(
name|dynField
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"double"
argument_list|)
condition|)
block|{
name|seen
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Didn't find the expected dynamic field"
argument_list|,
name|seen
argument_list|)
expr_stmt|;
name|FieldType
name|homeFT
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
literal|"home"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|home
operator|.
name|getType
argument_list|()
argument_list|,
name|homeFT
argument_list|)
expr_stmt|;
name|FieldType
name|xy
init|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
literal|"xy"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|xy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|xy
operator|instanceof
name|PointType
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|xy
operator|.
name|isPolyField
argument_list|()
argument_list|)
expr_stmt|;
name|home
operator|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
literal|"home_0"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
literal|"double"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|home
operator|=
name|schema
operator|.
name|getField
argument_list|(
literal|"home"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|home
operator|=
name|schema
operator|.
name|getField
argument_list|(
literal|"homed"
argument_list|)
expr_stmt|;
comment|//sub field suffix
name|assertNotNull
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|home
operator|.
name|isPolyField
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPointFieldType
specifier|public
name|void
name|testPointFieldType
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
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|home
init|=
name|schema
operator|.
name|getField
argument_list|(
literal|"home"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"home is not a poly field"
argument_list|,
name|home
operator|.
name|isPolyField
argument_list|()
argument_list|)
expr_stmt|;
name|FieldType
name|tmp
init|=
name|home
operator|.
name|getType
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|tmp
operator|instanceof
name|PointType
argument_list|)
expr_stmt|;
name|PointType
name|pt
init|=
operator|(
name|PointType
operator|)
name|tmp
decl_stmt|;
name|assertEquals
argument_list|(
name|pt
operator|.
name|getDimension
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|double
index|[]
name|xy
init|=
operator|new
name|double
index|[]
block|{
literal|35.0
block|,
operator|-
literal|79.34
block|}
decl_stmt|;
name|String
name|point
init|=
name|xy
index|[
literal|0
index|]
operator|+
literal|","
operator|+
name|xy
index|[
literal|1
index|]
decl_stmt|;
name|List
argument_list|<
name|IndexableField
argument_list|>
name|fields
init|=
name|home
operator|.
name|createFields
argument_list|(
name|point
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|//should be 3, we have a stored field
comment|//first two fields contain the values, third is just stored and contains the original
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|hasValue
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
operator|||
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
operator|||
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|!=
literal|null
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Doesn't have a value: "
operator|+
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|hasValue
argument_list|)
expr_stmt|;
block|}
comment|/*assertTrue("first field " + fields[0].tokenStreamValue() +  " is not 35.0", pt.getSubType().toExternal(fields[0]).equals(String.valueOf(xy[0])));     assertTrue("second field is not -79.34", pt.getSubType().toExternal(fields[1]).equals(String.valueOf(xy[1])));     assertTrue("third field is not '35.0,-79.34'", pt.getSubType().toExternal(fields[2]).equals(point));*/
name|home
operator|=
name|schema
operator|.
name|getField
argument_list|(
literal|"home_ns"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|fields
operator|=
name|home
operator|.
name|createFields
argument_list|(
name|point
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//should be 2, since we aren't storing
name|home
operator|=
name|schema
operator|.
name|getField
argument_list|(
literal|"home_ns"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|home
argument_list|)
expr_stmt|;
try|try
block|{
name|fields
operator|=
name|home
operator|.
name|createFields
argument_list|(
literal|"35.0,foo"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//
block|}
comment|//
name|SchemaField
name|s1
init|=
name|schema
operator|.
name|getField
argument_list|(
literal|"test_p"
argument_list|)
decl_stmt|;
name|SchemaField
name|s2
init|=
name|schema
operator|.
name|getField
argument_list|(
literal|"test_p"
argument_list|)
decl_stmt|;
name|ValueSource
name|v1
init|=
name|s1
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|s1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ValueSource
name|v2
init|=
name|s2
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|s2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|v1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|v2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSearching
specifier|public
name|void
name|testSearching
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"home"
argument_list|,
name|i
operator|+
literal|","
operator|+
operator|(
name|i
operator|*
literal|100
operator|)
argument_list|,
literal|"homed"
argument_list|,
operator|(
name|i
operator|*
literal|1000
operator|)
operator|+
literal|","
operator|+
operator|(
name|i
operator|*
literal|10000
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='50']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"home:1,100"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[@name='home'][.='1,100']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"homed:1000,10000"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[@name='homed'][.='1000,10000']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(home, vector(0, 0))"
argument_list|)
argument_list|,
literal|"\"//*[@numFound='50']\""
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, home, vector(0, 0))"
argument_list|)
argument_list|,
literal|"\"//*[@numFound='50']\""
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"home:[10,10000 TO 30,30000]"
argument_list|)
argument_list|,
literal|"\"//*[@numFound='3']\""
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"homed:[1,1000 TO 2000,35000]"
argument_list|)
argument_list|,
literal|"\"//*[@numFound='2']\""
argument_list|)
expr_stmt|;
comment|//bad
name|ignoreException
argument_list|(
literal|"dimension"
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|"Query should throw an exception due to incorrect dimensions"
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"homed:[1 TO 2000]"
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSearchDetails
specifier|public
name|void
name|testSearchDetails
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
name|getLatestSchema
argument_list|()
decl_stmt|;
name|double
index|[]
name|xy
init|=
operator|new
name|double
index|[]
block|{
literal|35.0
block|,
operator|-
literal|79.34
block|}
decl_stmt|;
name|String
name|point
init|=
name|xy
index|[
literal|0
index|]
operator|+
literal|","
operator|+
name|xy
index|[
literal|1
index|]
decl_stmt|;
comment|//How about some queries?
comment|//don't need a parser for this path currently.  This may change
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"home_ns"
argument_list|,
name|point
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SchemaField
name|home
init|=
name|schema
operator|.
name|getField
argument_list|(
literal|"home_ns"
argument_list|)
decl_stmt|;
name|PointType
name|pt
init|=
operator|(
name|PointType
operator|)
name|home
operator|.
name|getType
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|pt
operator|.
name|getDimension
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|pt
operator|.
name|getFieldQuery
argument_list|(
literal|null
argument_list|,
name|home
argument_list|,
name|point
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|BooleanQuery
argument_list|)
expr_stmt|;
comment|//should have two clauses, one for 35.0 and the other for -79.34
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bq
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
