begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_comment
comment|/**  * Stores a core's configuration in the form of a SolrConfig and IndexSchema  */
end_comment
begin_class
DECL|class|ConfigSet
specifier|public
class|class
name|ConfigSet
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|solrconfig
specifier|private
specifier|final
name|SolrConfig
name|solrconfig
decl_stmt|;
DECL|field|indexSchema
specifier|private
specifier|final
name|IndexSchema
name|indexSchema
decl_stmt|;
DECL|field|properties
specifier|private
specifier|final
name|NamedList
name|properties
decl_stmt|;
DECL|method|ConfigSet
specifier|public
name|ConfigSet
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrConfig
name|solrConfig
parameter_list|,
name|IndexSchema
name|indexSchema
parameter_list|,
name|NamedList
name|properties
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|solrconfig
operator|=
name|solrConfig
expr_stmt|;
name|this
operator|.
name|indexSchema
operator|=
name|indexSchema
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getSolrConfig
specifier|public
name|SolrConfig
name|getSolrConfig
parameter_list|()
block|{
return|return
name|solrconfig
return|;
block|}
DECL|method|getIndexSchema
specifier|public
name|IndexSchema
name|getIndexSchema
parameter_list|()
block|{
return|return
name|indexSchema
return|;
block|}
DECL|method|getProperties
specifier|public
name|NamedList
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
block|}
end_class
end_unit
