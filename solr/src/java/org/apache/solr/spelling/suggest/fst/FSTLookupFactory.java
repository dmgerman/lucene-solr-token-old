begin_unit
begin_package
DECL|package|org.apache.solr.spelling.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|fst
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
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|Lookup
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
name|search
operator|.
name|suggest
operator|.
name|fst
operator|.
name|FSTLookup
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
name|spelling
operator|.
name|suggest
operator|.
name|LookupFactory
import|;
end_import
begin_comment
comment|/**  * Factory for {@link FSTLookup}  */
end_comment
begin_class
DECL|class|FSTLookupFactory
specifier|public
class|class
name|FSTLookupFactory
extends|extends
name|LookupFactory
block|{
comment|/**    * The number of separate buckets for weights (discretization). The more buckets,    * the more fine-grained term weights (priorities) can be assigned. The speed of lookup    * will not decrease for prefixes which have highly-weighted completions (because these    * are filled-in first), but will decrease significantly for low-weighted terms (but    * these should be infrequent, so it is all right).    *     *<p>The number of buckets must be within [1, 255] range.    */
DECL|field|WEIGHT_BUCKETS
specifier|public
specifier|static
specifier|final
name|String
name|WEIGHT_BUCKETS
init|=
literal|"weightBuckets"
decl_stmt|;
comment|/**    * If<code>true</code>, exact suggestions are returned first, even if they are prefixes    * of other strings in the automaton (possibly with larger weights).     */
DECL|field|EXACT_MATCH_FIRST
specifier|public
specifier|static
specifier|final
name|String
name|EXACT_MATCH_FIRST
init|=
literal|"exactMatchFirst"
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Lookup
name|create
parameter_list|(
name|NamedList
name|params
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|int
name|buckets
init|=
name|params
operator|.
name|get
argument_list|(
name|WEIGHT_BUCKETS
argument_list|)
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|WEIGHT_BUCKETS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|10
decl_stmt|;
name|boolean
name|exactMatchFirst
init|=
name|params
operator|.
name|get
argument_list|(
name|EXACT_MATCH_FIRST
argument_list|)
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|EXACT_MATCH_FIRST
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|true
decl_stmt|;
return|return
operator|new
name|FSTLookup
argument_list|(
name|buckets
argument_list|,
name|exactMatchFirst
argument_list|)
return|;
block|}
block|}
end_class
end_unit
