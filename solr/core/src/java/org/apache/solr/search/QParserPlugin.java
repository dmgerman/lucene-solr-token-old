begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|SolrInfoMBean
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
name|search
operator|.
name|join
operator|.
name|BlockJoinChildQParserPlugin
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
name|search
operator|.
name|join
operator|.
name|BlockJoinParentQParserPlugin
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
name|search
operator|.
name|join
operator|.
name|GraphQParserPlugin
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
name|search
operator|.
name|mlt
operator|.
name|MLTQParserPlugin
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
name|NamedListInitializedPlugin
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_class
DECL|class|QParserPlugin
specifier|public
specifier|abstract
class|class
name|QParserPlugin
implements|implements
name|NamedListInitializedPlugin
implements|,
name|SolrInfoMBean
block|{
comment|/** internal use - name of the default parser */
DECL|field|DEFAULT_QTYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_QTYPE
init|=
name|LuceneQParserPlugin
operator|.
name|NAME
decl_stmt|;
comment|/**    * Internal use - name to class mappings of builtin parsers.    * Each query parser plugin extending {@link QParserPlugin} has own instance of standardPlugins.    * This leads to cyclic dependencies of static fields and to case when NAME field is not yet initialized.    * This result to NPE during initialization.    * For every plugin, listed here, NAME field has to be final and static.    */
DECL|field|standardPlugins
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|QParserPlugin
argument_list|>
argument_list|>
name|standardPlugins
decl_stmt|;
static|static
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|QParserPlugin
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|30
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|LuceneQParserPlugin
operator|.
name|NAME
argument_list|,
name|LuceneQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|OldLuceneQParserPlugin
operator|.
name|NAME
argument_list|,
name|OldLuceneQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FunctionQParserPlugin
operator|.
name|NAME
argument_list|,
name|FunctionQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PrefixQParserPlugin
operator|.
name|NAME
argument_list|,
name|PrefixQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|BoostQParserPlugin
operator|.
name|NAME
argument_list|,
name|BoostQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|DisMaxQParserPlugin
operator|.
name|NAME
argument_list|,
name|DisMaxQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ExtendedDismaxQParserPlugin
operator|.
name|NAME
argument_list|,
name|ExtendedDismaxQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FieldQParserPlugin
operator|.
name|NAME
argument_list|,
name|FieldQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|RawQParserPlugin
operator|.
name|NAME
argument_list|,
name|RawQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|TermQParserPlugin
operator|.
name|NAME
argument_list|,
name|TermQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|TermsQParserPlugin
operator|.
name|NAME
argument_list|,
name|TermsQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|NestedQParserPlugin
operator|.
name|NAME
argument_list|,
name|NestedQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FunctionRangeQParserPlugin
operator|.
name|NAME
argument_list|,
name|FunctionRangeQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SpatialFilterQParserPlugin
operator|.
name|NAME
argument_list|,
name|SpatialFilterQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SpatialBoxQParserPlugin
operator|.
name|NAME
argument_list|,
name|SpatialBoxQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|JoinQParserPlugin
operator|.
name|NAME
argument_list|,
name|JoinQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SurroundQParserPlugin
operator|.
name|NAME
argument_list|,
name|SurroundQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SwitchQParserPlugin
operator|.
name|NAME
argument_list|,
name|SwitchQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|MaxScoreQParserPlugin
operator|.
name|NAME
argument_list|,
name|MaxScoreQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|BlockJoinParentQParserPlugin
operator|.
name|NAME
argument_list|,
name|BlockJoinParentQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|BlockJoinChildQParserPlugin
operator|.
name|NAME
argument_list|,
name|BlockJoinChildQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|CollapsingQParserPlugin
operator|.
name|NAME
argument_list|,
name|CollapsingQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SimpleQParserPlugin
operator|.
name|NAME
argument_list|,
name|SimpleQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ComplexPhraseQParserPlugin
operator|.
name|NAME
argument_list|,
name|ComplexPhraseQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ReRankQParserPlugin
operator|.
name|NAME
argument_list|,
name|ReRankQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ExportQParserPlugin
operator|.
name|NAME
argument_list|,
name|ExportQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|MLTQParserPlugin
operator|.
name|NAME
argument_list|,
name|MLTQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|HashQParserPlugin
operator|.
name|NAME
argument_list|,
name|HashQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|GraphQParserPlugin
operator|.
name|NAME
argument_list|,
name|GraphQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|XmlQParserPlugin
operator|.
name|NAME
argument_list|,
name|XmlQParserPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|standardPlugins
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
comment|/** return a {@link QParser} */
DECL|method|createParser
specifier|public
specifier|abstract
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
comment|// TODO: ideally use the NAME property that each qparser plugin has
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|""
return|;
comment|// UI required non-null to work
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYPARSER
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
operator|new
name|URL
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
