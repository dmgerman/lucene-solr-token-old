begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|net
operator|.
name|URL
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|lucene
operator|.
name|search
operator|.
name|FieldCache
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
name|FieldCache
operator|.
name|CacheEntry
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
name|util
operator|.
name|FieldCacheSanityChecker
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
name|util
operator|.
name|FieldCacheSanityChecker
operator|.
name|Insanity
import|;
end_import
begin_comment
comment|/**  * A SolrInfoMBean that provides introspection of the Lucene FiledCache, this is<b>NOT</b> a cache that is manged by Solr.  *  *  */
end_comment
begin_class
DECL|class|SolrFieldCacheMBean
specifier|public
class|class
name|SolrFieldCacheMBean
implements|implements
name|SolrInfoMBean
block|{
DECL|field|checker
specifier|protected
name|FieldCacheSanityChecker
name|checker
init|=
operator|new
name|FieldCacheSanityChecker
argument_list|()
decl_stmt|;
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
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
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Provides introspection of the Lucene FieldCache, "
operator|+
literal|"this is **NOT** a cache that is managed by Solr."
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|CACHE
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|stats
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|CacheEntry
index|[]
name|entries
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getCacheEntries
argument_list|()
decl_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"entries_count"
argument_list|,
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|CacheEntry
name|e
init|=
name|entries
index|[
name|i
index|]
decl_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"entry#"
operator|+
name|i
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Insanity
index|[]
name|insanity
init|=
name|checker
operator|.
name|check
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"insanity_count"
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|insanity
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|/** RAM estimation is both CPU and memory intensive... we don't want to do it unless asked.       // we only estimate the size of insane entries       for (CacheEntry e : insanity[i].getCacheEntries()) {         // don't re-estimate if we've already done it.         if (null == e.getEstimatedSize()) e.estimateSize();       }       **/
name|stats
operator|.
name|add
argument_list|(
literal|"insanity#"
operator|+
name|i
argument_list|,
name|insanity
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|stats
return|;
block|}
block|}
end_class
end_unit
