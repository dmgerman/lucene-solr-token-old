begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ClusterState
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
name|cloud
operator|.
name|ZkStateReader
import|;
end_import
begin_comment
comment|// does not yet mock zkclient at all
end_comment
begin_class
DECL|class|MockZkStateReader
specifier|public
class|class
name|MockZkStateReader
extends|extends
name|ZkStateReader
block|{
DECL|field|collections
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|collections
decl_stmt|;
DECL|method|MockZkStateReader
specifier|public
name|MockZkStateReader
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|collections
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|MockSolrZkClient
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
name|this
operator|.
name|collections
operator|=
name|collections
expr_stmt|;
block|}
DECL|method|getAllCollections
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAllCollections
parameter_list|()
block|{
return|return
name|collections
return|;
block|}
block|}
end_class
end_unit
