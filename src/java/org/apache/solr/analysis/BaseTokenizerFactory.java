begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
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
name|logging
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
name|core
operator|.
name|SolrConfig
import|;
end_import
begin_comment
comment|/**  * Simple abstract implementation that handles init arg processing.  *   * @version $Id$  */
end_comment
begin_class
DECL|class|BaseTokenizerFactory
specifier|public
specifier|abstract
class|class
name|BaseTokenizerFactory
implements|implements
name|TokenizerFactory
implements|,
name|SolrConfig
operator|.
name|Initializable
block|{
DECL|field|log
specifier|final
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|BaseTokenizerFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/** The init args */
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"calling the deprecated form of init; should be calling init(SolrConfig solrConfig, Map<String,String> args)"
argument_list|)
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
DECL|method|getArgs
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getArgs
parameter_list|()
block|{
return|return
name|args
return|;
block|}
block|}
end_class
end_unit
