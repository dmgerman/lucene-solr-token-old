begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
DECL|class|LogLevelHandlerTest
specifier|public
class|class
name|LogLevelHandlerTest
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
DECL|method|testLogLevelHandlerOutput
specifier|public
name|void
name|testLogLevelHandlerOutput
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"/admin/loglevel"
argument_list|)
argument_list|,
literal|"//arr[@name='loggers']/lst/str[.='global']/../str[@name='level'][.='INFO']"
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
literal|"/admin/loglevel"
argument_list|,
literal|"set"
argument_list|,
literal|"org.xxx.yyy.abc:null"
argument_list|,
literal|"set"
argument_list|,
literal|"org.xxx.yyy.zzz:FINEST"
argument_list|)
argument_list|,
literal|"//arr[@name='loggers']/lst/str[.='org.xxx.yyy.zzz']/../str[@name='level'][.='FINEST']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
