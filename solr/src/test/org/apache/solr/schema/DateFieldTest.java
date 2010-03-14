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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|DateField
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
name|DateMathParser
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
name|document
operator|.
name|Fieldable
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
name|TimeZone
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_class
DECL|class|DateFieldTest
specifier|public
class|class
name|DateFieldTest
extends|extends
name|LegacyDateFieldTest
block|{
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|f
operator|=
operator|new
name|DateField
argument_list|()
expr_stmt|;
block|}
DECL|method|testToInternal
specifier|public
name|void
name|testToInternal
parameter_list|()
throws|throws
name|Exception
block|{
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|"1995-12-31T23:59:59.999666Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|"1995-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.99"
argument_list|,
literal|"1995-12-31T23:59:59.99Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|"1995-12-31T23:59:59.9Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59Z"
argument_list|)
expr_stmt|;
comment|// here the input isn't in the canonical form, but we should be forgiving
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.99"
argument_list|,
literal|"1995-12-31T23:59:59.990Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|"1995-12-31T23:59:59.900Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|"1995-12-31T23:59:59.90Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59.000Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59.00Z"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|"1995-12-31T23:59:59.0Z"
argument_list|)
expr_stmt|;
comment|// kind of kludgy, but we have other tests for the actual date math
name|assertToI
argument_list|(
name|f
operator|.
name|toInternal
argument_list|(
name|p
operator|.
name|parseMath
argument_list|(
literal|"/DAY"
argument_list|)
argument_list|)
argument_list|,
literal|"NOW/DAY"
argument_list|)
expr_stmt|;
comment|// as of Solr 1.3
name|assertToI
argument_list|(
literal|"1995-12-31T00:00:00"
argument_list|,
literal|"1995-12-31T23:59:59Z/DAY"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T00:00:00"
argument_list|,
literal|"1995-12-31T23:59:59.123Z/DAY"
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T00:00:00"
argument_list|,
literal|"1995-12-31T23:59:59.123999Z/DAY"
argument_list|)
expr_stmt|;
block|}
DECL|method|testToInternalObj
specifier|public
name|void
name|testToInternalObj
parameter_list|()
throws|throws
name|Exception
block|{
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.999"
argument_list|,
literal|820454399999l
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.99"
argument_list|,
literal|820454399990l
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59.9"
argument_list|,
literal|820454399900l
argument_list|)
expr_stmt|;
name|assertToI
argument_list|(
literal|"1995-12-31T23:59:59"
argument_list|,
literal|820454399000l
argument_list|)
expr_stmt|;
block|}
DECL|method|assertParseMath
specifier|public
name|void
name|assertParseMath
parameter_list|(
name|long
name|expected
parameter_list|,
name|String
name|input
parameter_list|)
block|{
name|Date
name|d
init|=
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|f
operator|.
name|parseMath
argument_list|(
name|d
argument_list|,
name|input
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// as of Solr1.3
DECL|method|testParseMath
specifier|public
name|void
name|testParseMath
parameter_list|()
block|{
name|assertParseMath
argument_list|(
literal|820454699999l
argument_list|,
literal|"1995-12-31T23:59:59.999765Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|820454699999l
argument_list|,
literal|"1995-12-31T23:59:59.999Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|820454699990l
argument_list|,
literal|"1995-12-31T23:59:59.99Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00Z/DAY"
argument_list|)
expr_stmt|;
comment|// here the input isn't in the canonical form, but we should be forgiving
name|assertParseMath
argument_list|(
literal|820454699990l
argument_list|,
literal|"1995-12-31T23:59:59.990Z+5MINUTES"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00.0Z/DAY"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00.00Z/DAY"
argument_list|)
expr_stmt|;
name|assertParseMath
argument_list|(
literal|194918400000l
argument_list|,
literal|"1976-03-06T03:06:00.000Z/DAY"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertToObject
specifier|public
name|void
name|assertToObject
parameter_list|(
name|long
name|expected
parameter_list|,
name|String
name|input
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"Input: "
operator|+
name|input
argument_list|,
name|expected
argument_list|,
name|f
operator|.
name|toObject
argument_list|(
name|input
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// as of Solr1.3
DECL|method|testToObject
specifier|public
name|void
name|testToObject
parameter_list|()
throws|throws
name|Exception
block|{
name|assertToObject
argument_list|(
literal|820454399987l
argument_list|,
literal|"1995-12-31T23:59:59.987666Z"
argument_list|)
expr_stmt|;
name|assertToObject
argument_list|(
literal|820454399987l
argument_list|,
literal|"1995-12-31T23:59:59.987Z"
argument_list|)
expr_stmt|;
name|assertToObject
argument_list|(
literal|820454399980l
argument_list|,
literal|"1995-12-31T23:59:59.98Z"
argument_list|)
expr_stmt|;
name|assertToObject
argument_list|(
literal|820454399900l
argument_list|,
literal|"1995-12-31T23:59:59.9Z"
argument_list|)
expr_stmt|;
name|assertToObject
argument_list|(
literal|820454399000l
argument_list|,
literal|"1995-12-31T23:59:59Z"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFormatter
specifier|public
name|void
name|testFormatter
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00.005"
argument_list|,
name|f
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00"
argument_list|,
name|f
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00.37"
argument_list|,
name|f
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
literal|370
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:00.9"
argument_list|,
name|f
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
literal|900
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
