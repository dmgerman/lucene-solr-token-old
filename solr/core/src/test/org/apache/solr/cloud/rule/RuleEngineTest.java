begin_unit
begin_package
DECL|package|org.apache.solr.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
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
name|SolrTestCaseJ4
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
name|cloud
operator|.
name|rule
operator|.
name|ReplicaAssigner
operator|.
name|Position
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
name|Utils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
operator|.
name|Rule
operator|.
name|parseRule
import|;
end_import
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
name|util
operator|.
name|Utils
operator|.
name|makeMap
import|;
end_import
begin_class
DECL|class|RuleEngineTest
specifier|public
class|class
name|RuleEngineTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testPlacement2
specifier|public
name|void
name|testPlacement2
parameter_list|()
block|{
name|String
name|s
init|=
literal|"{"
operator|+
literal|"  '127.0.0.1:49961_':{"
operator|+
literal|"    'node':'127.0.0.1:49961_',"
operator|+
literal|"    'freedisk':992,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.1:49955_':{"
operator|+
literal|"    'node':'127.0.0.1:49955_',"
operator|+
literal|"    'freedisk':992,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.1:49952_':{"
operator|+
literal|"    'node':'127.0.0.1:49952_',"
operator|+
literal|"    'freedisk':992,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.1:49947_':{"
operator|+
literal|"    'node':'127.0.0.1:49947_',"
operator|+
literal|"    'freedisk':992,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.1:49958_':{"
operator|+
literal|"    'node':'127.0.0.1:49958_',"
operator|+
literal|"    'freedisk':992,"
operator|+
literal|"    'cores':1}}"
decl_stmt|;
name|MockSnitch
operator|.
name|nodeVsTags
operator|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|s
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|shardVsReplicaCount
init|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Rule
argument_list|>
name|rules
init|=
name|parseRules
argument_list|(
literal|"[{'cores':'<4'}, {"
operator|+
literal|"'replica':'1',shard:'*','node':'*'},"
operator|+
literal|" {'freedisk':'>1'}]"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Position
argument_list|,
name|String
argument_list|>
name|mapping
init|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
DECL|method|testPlacement3
specifier|public
name|void
name|testPlacement3
parameter_list|()
block|{
name|String
name|s
init|=
literal|"{"
operator|+
literal|"  '127.0.0.1:49961_':{"
operator|+
literal|"    'node':'127.0.0.1:49961_',"
operator|+
literal|"    'freedisk':992,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.2:49955_':{"
operator|+
literal|"    'node':'127.0.0.1:49955_',"
operator|+
literal|"    'freedisk':995,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.3:49952_':{"
operator|+
literal|"    'node':'127.0.0.1:49952_',"
operator|+
literal|"    'freedisk':990,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.1:49947_':{"
operator|+
literal|"    'node':'127.0.0.1:49947_',"
operator|+
literal|"    'freedisk':980,"
operator|+
literal|"    'cores':1},"
operator|+
literal|"  '127.0.0.2:49958_':{"
operator|+
literal|"    'node':'127.0.0.1:49958_',"
operator|+
literal|"    'freedisk':970,"
operator|+
literal|"    'cores':1}}"
decl_stmt|;
name|MockSnitch
operator|.
name|nodeVsTags
operator|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|s
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
comment|//test not
name|List
argument_list|<
name|Rule
argument_list|>
name|rules
init|=
name|parseRules
argument_list|(
literal|"[{cores:'<4'}, "
operator|+
literal|"{replica:'1',shard:'*',node:'*'},"
operator|+
literal|"{node:'!127.0.0.1:49947_'},"
operator|+
literal|"{freedisk:'>1'}]"
argument_list|)
decl_stmt|;
name|Map
name|shardVsReplicaCount
init|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Position
argument_list|,
name|String
argument_list|>
name|mapping
init|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mapping
operator|.
name|containsValue
argument_list|(
literal|"127.0.0.1:49947_"
argument_list|)
argument_list|)
expr_stmt|;
name|rules
operator|=
name|parseRules
argument_list|(
literal|"[{cores:'<4'}, "
operator|+
literal|"{replica:'1',node:'*'},"
operator|+
literal|"{freedisk:'>980'}]"
argument_list|)
expr_stmt|;
name|shardVsReplicaCount
operator|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings0
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|rules
operator|=
name|parseRules
argument_list|(
literal|"[{cores:'<4'}, "
operator|+
literal|"{replica:'1',node:'*'},"
operator|+
literal|"{freedisk:'>980~'}]"
argument_list|)
expr_stmt|;
name|shardVsReplicaCount
operator|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings0
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mapping
operator|.
name|containsValue
argument_list|(
literal|"127.0.0.2:49958_"
argument_list|)
argument_list|)
expr_stmt|;
name|rules
operator|=
name|parseRules
argument_list|(
literal|"[{cores:'<4'}, "
operator|+
literal|"{replica:'1',shard:'*',host:'*'}]"
argument_list|)
expr_stmt|;
name|shardVsReplicaCount
operator|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|rules
operator|=
name|parseRules
argument_list|(
literal|"[{cores:'<4'}, "
operator|+
literal|"{replica:'1',shard:'**',host:'*'}]"
argument_list|)
expr_stmt|;
name|shardVsReplicaCount
operator|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings0
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|rules
operator|=
name|parseRules
argument_list|(
literal|"[{cores:'<4'}, "
operator|+
literal|"{replica:'1~',shard:'**',host:'*'}]"
argument_list|)
expr_stmt|;
name|shardVsReplicaCount
operator|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
DECL|method|parseRules
specifier|private
name|List
argument_list|<
name|Rule
argument_list|>
name|parseRules
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|List
name|maps
init|=
operator|(
name|List
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|s
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Rule
argument_list|>
name|rules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|map
range|:
name|maps
control|)
name|rules
operator|.
name|add
argument_list|(
operator|new
name|Rule
argument_list|(
operator|(
name|Map
operator|)
name|map
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rules
return|;
block|}
annotation|@
name|Test
DECL|method|testPlacement
specifier|public
name|void
name|testPlacement
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rulesStr
init|=
literal|"rack:*,replica:<2"
decl_stmt|;
name|List
argument_list|<
name|Rule
argument_list|>
name|rules
init|=
name|parse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rulesStr
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|shardVsReplicaCount
init|=
name|makeMap
argument_list|(
literal|"shard1"
argument_list|,
literal|3
argument_list|,
literal|"shard2"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Map
name|nodeVsTags
init|=
name|makeMap
argument_list|(
literal|"node1:80"
argument_list|,
name|makeMap
argument_list|(
literal|"rack"
argument_list|,
literal|"178"
argument_list|)
argument_list|,
literal|"node2:80"
argument_list|,
name|makeMap
argument_list|(
literal|"rack"
argument_list|,
literal|"179"
argument_list|)
argument_list|,
literal|"node3:80"
argument_list|,
name|makeMap
argument_list|(
literal|"rack"
argument_list|,
literal|"180"
argument_list|)
argument_list|,
literal|"node4:80"
argument_list|,
name|makeMap
argument_list|(
literal|"rack"
argument_list|,
literal|"181"
argument_list|)
argument_list|,
literal|"node5:80"
argument_list|,
name|makeMap
argument_list|(
literal|"rack"
argument_list|,
literal|"182"
argument_list|)
argument_list|)
decl_stmt|;
name|MockSnitch
operator|.
name|nodeVsTags
operator|=
name|nodeVsTags
expr_stmt|;
name|Map
argument_list|<
name|Position
argument_list|,
name|String
argument_list|>
name|mapping
init|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings0
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|rulesStr
operator|=
literal|"rack:*,replica:<2~"
expr_stmt|;
name|rules
operator|=
name|parse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rulesStr
argument_list|)
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|rulesStr
operator|=
literal|"rack:*,shard:*,replica:<2"
expr_stmt|;
comment|//for each shard there can be a max of 1 replica
name|rules
operator|=
name|parse
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rulesStr
argument_list|)
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|shardVsReplicaCount
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|MockSnitch
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|MockSnitch
operator|.
name|nodeVsTags
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
DECL|class|MockSnitch
specifier|public
specifier|static
class|class
name|MockSnitch
extends|extends
name|Snitch
block|{
DECL|field|nodeVsTags
specifier|static
name|Map
name|nodeVsTags
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getTags
specifier|public
name|void
name|getTags
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedTags
parameter_list|,
name|SnitchContext
name|ctx
parameter_list|)
block|{
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|putAll
argument_list|(
operator|(
name|Map
argument_list|<
name|?
extends|extends
name|String
argument_list|,
name|?
argument_list|>
operator|)
name|nodeVsTags
operator|.
name|get
argument_list|(
name|solrNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isKnownTag
specifier|public
name|boolean
name|isKnownTag
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
name|Map
name|next
init|=
operator|(
name|Map
operator|)
name|nodeVsTags
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|next
operator|.
name|containsKey
argument_list|(
name|tag
argument_list|)
return|;
block|}
block|}
DECL|method|parse
specifier|public
specifier|static
name|List
argument_list|<
name|Rule
argument_list|>
name|parse
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|rules
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|rules
operator|!=
literal|null
operator|&&
operator|!
name|rules
operator|.
name|isEmpty
argument_list|()
assert|;
name|ArrayList
argument_list|<
name|Rule
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|rules
control|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
continue|continue;
name|result
operator|.
name|add
argument_list|(
operator|new
name|Rule
argument_list|(
name|parseRule
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
