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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SolrParams
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
name|util
operator|.
name|NamedList
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
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|SolrPluginUtils
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
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import
begin_comment
comment|/**  * Identifies the language of a set of input fields using Tika's  * LanguageIdentifier. The tika-core-x.y.jar must be on the classpath  *<p/>  * The UpdateProcessorChain config entry can take a number of parameters  * which may also be passed as HTTP parameters on the update request  * and override the defaults. Here is the simplest processor config possible:  *   *<pre class="prettyprint">  *&lt;processor class=&quot;org.apache.solr.update.processor.LanguageIdentifierUpdateProcessorFactory&quot;&gt;  *&lt;str name=&quot;langid.fl&quot;&gt;title,text&lt;/str&gt;  *&lt;str name=&quot;langid.langField&quot;&gt;language_s&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  * See<a href="http://wiki.apache.org/solr/LanguageDetection">http://wiki.apache.org/solr/LanguageDetection</a>  * @since 3.5  */
end_comment
begin_class
DECL|class|LanguageIdentifierUpdateProcessorFactory
specifier|public
class|class
name|LanguageIdentifierUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
implements|,
name|LangIdParams
block|{
DECL|field|defaults
specifier|protected
name|SolrParams
name|defaults
decl_stmt|;
DECL|field|appends
specifier|protected
name|SolrParams
name|appends
decl_stmt|;
DECL|field|invariants
specifier|protected
name|SolrParams
name|invariants
decl_stmt|;
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{   }
comment|/**    * The UpdateRequestProcessor may be initialized in solrconfig.xml similarly    * to a RequestHandler, with defaults, appends and invariants.    * @param args a NamedList with the configuration parameters     */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
decl_stmt|;
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"defaults"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|defaults
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|defaults
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"appends"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|appends
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"invariants"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|invariants
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
comment|// Process defaults, appends and invariants if we got a request
if|if
condition|(
name|req
operator|!=
literal|null
condition|)
block|{
name|SolrPluginUtils
operator|.
name|setDefaults
argument_list|(
name|req
argument_list|,
name|defaults
argument_list|,
name|appends
argument_list|,
name|invariants
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LanguageIdentifierUpdateProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|next
argument_list|)
return|;
block|}
block|}
end_class
end_unit
