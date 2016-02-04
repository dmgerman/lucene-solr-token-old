begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package
begin_import
import|import static
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
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|noggit
operator|.
name|JSONUtil
import|;
end_import
begin_class
DECL|class|Replica
specifier|public
class|class
name|Replica
extends|extends
name|ZkNodeProps
block|{
comment|/**    * The replica's state. In general, if the node the replica is hosted on is    * not under {@code /live_nodes} in ZK, the replica's state should be    * discarded.    */
DECL|enum|State
specifier|public
enum|enum
name|State
block|{
comment|/**      * The replica is ready to receive updates and queries.      *<p>      *<b>NOTE</b>: when the node the replica is hosted on crashes, the      * replica's state may remain ACTIVE in ZK. To determine if the replica is      * truly active, you must also verify that its {@link Replica#getNodeName()      * node} is under {@code /live_nodes} in ZK (or use      * {@link ClusterState#liveNodesContain(String)}).      *</p>      */
DECL|enum constant|ACTIVE
name|ACTIVE
block|,
comment|/**      * The first state before {@link State#RECOVERING}. A node in this state      * should be actively trying to move to {@link State#RECOVERING}.      *<p>      *<b>NOTE</b>: a replica's state may appear DOWN in ZK also when the node      * it's hosted on gracefully shuts down. This is a best effort though, and      * should not be relied on.      *</p>      */
DECL|enum constant|DOWN
name|DOWN
block|,
comment|/**      * The node is recovering from the leader. This might involve peer-sync,      * full replication or finding out things are already in sync.      */
DECL|enum constant|RECOVERING
name|RECOVERING
block|,
comment|/**      * Recovery attempts have not worked, something is not right.      *<p>      *<b>NOTE</b>: This state doesn't matter if the node is not part of      * {@code /live_nodes} in ZK; in that case the node is not part of the      * cluster and it's state should be discarded.      *</p>      */
DECL|enum constant|RECOVERY_FAILED
name|RECOVERY_FAILED
block|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
comment|/** Converts the state string to a State instance. */
DECL|method|getState
specifier|public
specifier|static
name|State
name|getState
parameter_list|(
name|String
name|stateStr
parameter_list|)
block|{
return|return
name|stateStr
operator|==
literal|null
condition|?
literal|null
else|:
name|State
operator|.
name|valueOf
argument_list|(
name|stateStr
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|nodeName
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|State
name|state
decl_stmt|;
DECL|method|Replica
specifier|public
name|Replica
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propMap
parameter_list|)
block|{
name|super
argument_list|(
name|propMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
operator|(
name|String
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
expr_stmt|;
if|if
condition|(
name|propMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|state
operator|=
name|State
operator|.
name|getState
argument_list|(
operator|(
name|String
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|state
operator|=
name|State
operator|.
name|ACTIVE
expr_stmt|;
comment|//Default to ACTIVE
name|propMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
name|state
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
DECL|method|getCoreUrl
specifier|public
name|String
name|getCoreUrl
parameter_list|()
block|{
return|return
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|getStr
argument_list|(
name|BASE_URL_PROP
argument_list|)
argument_list|,
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
argument_list|)
return|;
block|}
comment|/** The name of the node this replica resides on */
DECL|method|getNodeName
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
comment|/** Returns the {@link State} of this replica. */
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|':'
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|propMap
argument_list|,
operator|-
literal|1
argument_list|)
return|;
comment|// small enough, keep it on one line (i.e. no indent)
block|}
block|}
end_class
end_unit
