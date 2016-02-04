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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**   * Cache regenerator that just populates the new cache  * with the old items.  *<p>  * This is useful for e.g. CachingWrapperFilters that are not  * invalidated by the creation of a new searcher.  */
end_comment
begin_class
DECL|class|NoOpRegenerator
specifier|public
class|class
name|NoOpRegenerator
implements|implements
name|CacheRegenerator
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|regenerateItem
specifier|public
name|boolean
name|regenerateItem
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrCache
name|newCache
parameter_list|,
name|SolrCache
name|oldCache
parameter_list|,
name|Object
name|oldKey
parameter_list|,
name|Object
name|oldVal
parameter_list|)
throws|throws
name|IOException
block|{
name|newCache
operator|.
name|put
argument_list|(
name|oldKey
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
