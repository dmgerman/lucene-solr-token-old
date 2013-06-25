begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package
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
name|logging
operator|.
name|log4j
operator|.
name|Log4jInfo
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
DECL|class|LoggingHandlerTest
specifier|public
class|class
name|LoggingHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
comment|// TODO: This only tests Log4j at the moment, as that's what's defined
comment|// through the CoreContainer.
comment|// TODO: Would be nice to throw an exception on trying to set a
comment|// log level that doesn't exist
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
DECL|method|testLogLevelHandlerOutput
specifier|public
name|void
name|testLogLevelHandlerOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
name|tst
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.apache.solr.SolrTestCaseJ4"
argument_list|)
decl_stmt|;
name|tst
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
name|Log4jInfo
name|wrap
init|=
operator|new
name|Log4jInfo
argument_list|(
name|tst
operator|.
name|getName
argument_list|()
argument_list|,
name|tst
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Show Log Levels OK"
argument_list|,
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/logging"
argument_list|)
argument_list|,
literal|"//arr[@name='loggers']/lst/str[.='"
operator|+
name|wrap
operator|.
name|getName
argument_list|()
operator|+
literal|"']/../str[@name='level'][.='"
operator|+
name|wrap
operator|.
name|getLevel
argument_list|()
operator|+
literal|"']"
argument_list|,
literal|"//arr[@name='loggers']/lst/str[.='org.apache']/../null[@name='level']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Set and remove a level"
argument_list|,
name|req
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/admin/logging"
argument_list|,
literal|"set"
argument_list|,
literal|"org.xxx.yyy.abc:null"
argument_list|,
literal|"set"
argument_list|,
literal|"org.xxx.yyy.zzz:TRACE"
argument_list|)
argument_list|,
literal|"//arr[@name='loggers']/lst/str[.='org.xxx.yyy.zzz']/../str[@name='level'][.='TRACE']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
