begin_unit
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ModifiableSolrParams
import|;
end_import
begin_class
DECL|class|TikaLanguageIdentifierUpdateProcessorFactoryTest
specifier|public
class|class
name|TikaLanguageIdentifierUpdateProcessorFactoryTest
extends|extends
name|LanguageIdentifierUpdateProcessorFactoryTestCase
block|{
annotation|@
name|Override
DECL|method|createLangIdProcessor
specifier|protected
name|LanguageIdentifierUpdateProcessor
name|createLangIdProcessor
parameter_list|(
name|ModifiableSolrParams
name|parameters
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|TikaLanguageIdentifierUpdateProcessor
argument_list|(
name|_parser
operator|.
name|buildRequestFrom
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|parameters
argument_list|,
literal|null
argument_list|)
argument_list|,
name|resp
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class
end_unit
