begin_unit
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Appender
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|SimpleLayout
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|WriterAppender
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
name|Before
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
DECL|class|RequestLoggingTest
specifier|public
class|class
name|RequestLoggingTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|writer
specifier|private
name|StringWriter
name|writer
decl_stmt|;
DECL|field|appender
specifier|private
name|Appender
name|appender
decl_stmt|;
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
name|Before
DECL|method|setupAppender
specifier|public
name|void
name|setupAppender
parameter_list|()
block|{
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|appender
operator|=
operator|new
name|WriterAppender
argument_list|(
operator|new
name|SimpleLayout
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLogBeforeExecuteWithCoreLogger
specifier|public
name|void
name|testLogBeforeExecuteWithCoreLogger
parameter_list|()
block|{
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SolrCore
operator|.
name|class
argument_list|)
decl_stmt|;
name|testLogBeforeExecute
argument_list|(
name|logger
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLogBeforeExecuteWithRequestLogger
specifier|public
name|void
name|testLogBeforeExecuteWithRequestLogger
parameter_list|()
block|{
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.apache.solr.core.SolrCore.Request"
argument_list|)
decl_stmt|;
name|testLogBeforeExecute
argument_list|(
name|logger
argument_list|)
expr_stmt|;
block|}
DECL|method|testLogBeforeExecute
specifier|public
name|void
name|testLogBeforeExecute
parameter_list|(
name|Logger
name|logger
parameter_list|)
block|{
name|Level
name|level
init|=
name|logger
operator|.
name|getLevel
argument_list|()
decl_stmt|;
name|logger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|logger
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
try|try
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|output
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"DEBUG.*q=\\*:\\*.*"
argument_list|)
operator|.
name|matcher
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|group
init|=
name|matcher
operator|.
name|group
argument_list|()
decl_stmt|;
specifier|final
name|String
name|msg
init|=
literal|"Should not have post query information"
decl_stmt|;
name|assertFalse
argument_list|(
name|msg
argument_list|,
name|group
operator|.
name|contains
argument_list|(
literal|"hits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|msg
argument_list|,
name|group
operator|.
name|contains
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|msg
argument_list|,
name|group
operator|.
name|contains
argument_list|(
literal|"QTime"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|logger
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|logger
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
