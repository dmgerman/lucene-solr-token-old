begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
package|;
end_package
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  * CSVStrategyTest  *  * The test are organized in three different sections:  * The 'setter/getter' section, the lexer section and finally the strategy   * section. In case a test fails, you should follow a top-down approach for   * fixing a potential bug (its likely that the strategy itself fails if the lexer  * has problems...).  */
end_comment
begin_class
DECL|class|CSVStrategyTest
specifier|public
class|class
name|CSVStrategyTest
extends|extends
name|TestCase
block|{
comment|// ======================================================
comment|//   getters / setters
comment|// ======================================================
DECL|method|testGetSetCommentStart
specifier|public
name|void
name|testGetSetCommentStart
parameter_list|()
block|{
name|CSVStrategy
name|strategy
init|=
operator|(
name|CSVStrategy
operator|)
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
operator|.
name|clone
argument_list|()
decl_stmt|;
name|strategy
operator|.
name|setCommentStart
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getCommentStart
argument_list|()
argument_list|,
literal|'#'
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setCommentStart
argument_list|(
literal|'!'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getCommentStart
argument_list|()
argument_list|,
literal|'!'
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetSetEncapsulator
specifier|public
name|void
name|testGetSetEncapsulator
parameter_list|()
block|{
name|CSVStrategy
name|strategy
init|=
operator|(
name|CSVStrategy
operator|)
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
operator|.
name|clone
argument_list|()
decl_stmt|;
name|strategy
operator|.
name|setEncapsulator
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getEncapsulator
argument_list|()
argument_list|,
literal|'"'
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setEncapsulator
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getEncapsulator
argument_list|()
argument_list|,
literal|'\''
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetSetDelimiter
specifier|public
name|void
name|testGetSetDelimiter
parameter_list|()
block|{
name|CSVStrategy
name|strategy
init|=
operator|(
name|CSVStrategy
operator|)
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
operator|.
name|clone
argument_list|()
decl_stmt|;
name|strategy
operator|.
name|setDelimiter
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getDelimiter
argument_list|()
argument_list|,
literal|';'
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setDelimiter
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getDelimiter
argument_list|()
argument_list|,
literal|','
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setDelimiter
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getDelimiter
argument_list|()
argument_list|,
literal|'\t'
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetCSVStrategy
specifier|public
name|void
name|testSetCSVStrategy
parameter_list|()
block|{
name|CSVStrategy
name|strategy
init|=
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
decl_stmt|;
comment|// default settings
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getDelimiter
argument_list|()
argument_list|,
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getEncapsulator
argument_list|()
argument_list|,
literal|'"'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getCommentStart
argument_list|()
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|strategy
operator|.
name|getIgnoreLeadingWhitespaces
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|strategy
operator|.
name|getUnicodeEscapeInterpretation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|strategy
operator|.
name|getIgnoreEmptyLines
argument_list|()
argument_list|)
expr_stmt|;
comment|// explicit csv settings
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getDelimiter
argument_list|()
argument_list|,
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getEncapsulator
argument_list|()
argument_list|,
literal|'"'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getCommentStart
argument_list|()
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|strategy
operator|.
name|getIgnoreLeadingWhitespaces
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|strategy
operator|.
name|getUnicodeEscapeInterpretation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|strategy
operator|.
name|getIgnoreEmptyLines
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetExcelStrategy
specifier|public
name|void
name|testSetExcelStrategy
parameter_list|()
block|{
name|CSVStrategy
name|strategy
init|=
name|CSVStrategy
operator|.
name|EXCEL_STRATEGY
decl_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getDelimiter
argument_list|()
argument_list|,
literal|','
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getEncapsulator
argument_list|()
argument_list|,
literal|'"'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|strategy
operator|.
name|getCommentStart
argument_list|()
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|strategy
operator|.
name|getIgnoreLeadingWhitespaces
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|strategy
operator|.
name|getUnicodeEscapeInterpretation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|strategy
operator|.
name|getIgnoreEmptyLines
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
