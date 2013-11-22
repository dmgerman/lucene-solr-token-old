begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package
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
begin_class
DECL|class|CollectionAdminResponse
specifier|public
class|class
name|CollectionAdminResponse
extends|extends
name|SolrResponseBase
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getCollectionStatus
specifier|public
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|getCollectionStatus
parameter_list|()
block|{
return|return
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"success"
argument_list|)
return|;
block|}
DECL|method|isSuccess
specifier|public
name|boolean
name|isSuccess
parameter_list|()
block|{
return|return
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"success"
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|// this messages are typically from individual nodes, since
comment|// all the failures at the router are propagated as exceptions
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getErrorMessages
specifier|public
name|NamedList
argument_list|<
name|String
argument_list|>
name|getErrorMessages
parameter_list|()
block|{
return|return
operator|(
name|NamedList
argument_list|<
name|String
argument_list|>
operator|)
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"failure"
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getCollectionCoresStatus
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|getCollectionCoresStatus
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|res
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|cols
init|=
name|getCollectionStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|cols
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|e
range|:
name|cols
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|item
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|core
init|=
operator|(
name|String
operator|)
name|item
operator|.
name|get
argument_list|(
literal|"core"
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|put
argument_list|(
name|core
argument_list|,
operator|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
operator|)
name|item
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getCollectionNodesStatus
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|getCollectionNodesStatus
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|res
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|cols
init|=
name|getCollectionStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|cols
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|e
range|:
name|cols
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
call|(
name|NamedList
argument_list|<
name|Integer
argument_list|>
call|)
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
block|}
end_class
end_unit