begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import
begin_comment
comment|/**  * Carrot2 parameter mapping (recognized and mapped if passed via Solr configuration).  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CarrotParams
specifier|public
specifier|final
class|class
name|CarrotParams
block|{
DECL|field|CARROT_PREFIX
specifier|private
specifier|static
name|String
name|CARROT_PREFIX
init|=
literal|"carrot."
decl_stmt|;
DECL|field|ALGORITHM
specifier|public
specifier|static
name|String
name|ALGORITHM
init|=
name|CARROT_PREFIX
operator|+
literal|"algorithm"
decl_stmt|;
DECL|field|TITLE_FIELD_NAME
specifier|public
specifier|static
name|String
name|TITLE_FIELD_NAME
init|=
name|CARROT_PREFIX
operator|+
literal|"title"
decl_stmt|;
DECL|field|URL_FIELD_NAME
specifier|public
specifier|static
name|String
name|URL_FIELD_NAME
init|=
name|CARROT_PREFIX
operator|+
literal|"url"
decl_stmt|;
DECL|field|SNIPPET_FIELD_NAME
specifier|public
specifier|static
name|String
name|SNIPPET_FIELD_NAME
init|=
name|CARROT_PREFIX
operator|+
literal|"snippet"
decl_stmt|;
DECL|field|LANGUAGE_FIELD_NAME
specifier|public
specifier|static
name|String
name|LANGUAGE_FIELD_NAME
init|=
name|CARROT_PREFIX
operator|+
literal|"lang"
decl_stmt|;
DECL|field|CUSTOM_FIELD_NAME
specifier|public
specifier|static
name|String
name|CUSTOM_FIELD_NAME
init|=
name|CARROT_PREFIX
operator|+
literal|"custom"
decl_stmt|;
DECL|field|PRODUCE_SUMMARY
specifier|public
specifier|static
name|String
name|PRODUCE_SUMMARY
init|=
name|CARROT_PREFIX
operator|+
literal|"produceSummary"
decl_stmt|;
DECL|field|SUMMARY_FRAGSIZE
specifier|public
specifier|static
name|String
name|SUMMARY_FRAGSIZE
init|=
name|CARROT_PREFIX
operator|+
literal|"fragSize"
decl_stmt|;
DECL|field|SUMMARY_SNIPPETS
specifier|public
specifier|static
name|String
name|SUMMARY_SNIPPETS
init|=
name|CARROT_PREFIX
operator|+
literal|"summarySnippets"
decl_stmt|;
DECL|field|NUM_DESCRIPTIONS
specifier|public
specifier|static
name|String
name|NUM_DESCRIPTIONS
init|=
name|CARROT_PREFIX
operator|+
literal|"numDescriptions"
decl_stmt|;
DECL|field|OUTPUT_SUB_CLUSTERS
specifier|public
specifier|static
name|String
name|OUTPUT_SUB_CLUSTERS
init|=
name|CARROT_PREFIX
operator|+
literal|"outputSubClusters"
decl_stmt|;
DECL|field|LANGUAGE_CODE_MAP
specifier|public
specifier|static
name|String
name|LANGUAGE_CODE_MAP
init|=
name|CARROT_PREFIX
operator|+
literal|"lcmap"
decl_stmt|;
comment|/**    * Points to Carrot<sup>2</sup> resources    */
DECL|field|RESOURCES_DIR
specifier|public
specifier|static
name|String
name|RESOURCES_DIR
init|=
name|CARROT_PREFIX
operator|+
literal|"resourcesDir"
decl_stmt|;
DECL|field|CARROT_PARAM_NAMES
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|CARROT_PARAM_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ALGORITHM
argument_list|,
name|TITLE_FIELD_NAME
argument_list|,
name|URL_FIELD_NAME
argument_list|,
name|SNIPPET_FIELD_NAME
argument_list|,
name|LANGUAGE_FIELD_NAME
argument_list|,
name|CUSTOM_FIELD_NAME
argument_list|,
name|PRODUCE_SUMMARY
argument_list|,
name|SUMMARY_FRAGSIZE
argument_list|,
name|SUMMARY_SNIPPETS
argument_list|,
name|NUM_DESCRIPTIONS
argument_list|,
name|OUTPUT_SUB_CLUSTERS
argument_list|,
name|RESOURCES_DIR
argument_list|,
name|LANGUAGE_CODE_MAP
argument_list|)
decl_stmt|;
comment|/** No instances. */
DECL|method|CarrotParams
specifier|private
name|CarrotParams
parameter_list|()
block|{}
block|}
end_class
end_unit
