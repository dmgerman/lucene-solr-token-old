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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|ZkController
operator|.
name|ContextKey
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
name|SolrException
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
name|SolrZkClient
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
name|ZkCmdExecutor
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
name|ZooKeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
operator|.
name|ConnectionLossException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|WatchedEvent
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|Watcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|Watcher
operator|.
name|Event
operator|.
name|EventType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * Leader Election process. This class contains the logic by which a  * leader is chosen. First call * {@link #setup(ElectionContext)} to ensure  * the election process is init'd. Next call  * {@link #joinElection(ElectionContext, boolean)} to start the leader election.  *   * The implementation follows the classic ZooKeeper recipe of creating an  * ephemeral, sequential node for each candidate and then looking at the set  * of such nodes - if the created node is the lowest sequential node, the  * candidate that created the node is the leader. If not, the candidate puts  * a watch on the next lowest node it finds, and if that node goes down,   * starts the whole process over by checking if it's the lowest sequential node, etc.  *   */
end_comment
begin_class
DECL|class|LeaderElector
specifier|public
class|class
name|LeaderElector
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ELECTION_NODE
specifier|static
specifier|final
name|String
name|ELECTION_NODE
init|=
literal|"/election"
decl_stmt|;
DECL|field|LEADER_SEQ
specifier|public
specifier|final
specifier|static
name|Pattern
name|LEADER_SEQ
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*?/?.*?-n_(\\d+)"
argument_list|)
decl_stmt|;
DECL|field|SESSION_ID
specifier|private
specifier|final
specifier|static
name|Pattern
name|SESSION_ID
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*?/?(.*?-.*?)-n_\\d+"
argument_list|)
decl_stmt|;
DECL|field|NODE_NAME
specifier|private
specifier|final
specifier|static
name|Pattern
name|NODE_NAME
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*?/?(.*?-)(.*?)-n_\\d+"
argument_list|)
decl_stmt|;
DECL|field|zkClient
specifier|protected
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|zkCmdExecutor
specifier|private
name|ZkCmdExecutor
name|zkCmdExecutor
decl_stmt|;
DECL|field|context
specifier|private
specifier|volatile
name|ElectionContext
name|context
decl_stmt|;
DECL|field|watcher
specifier|private
name|ElectionWatcher
name|watcher
decl_stmt|;
DECL|field|electionContexts
specifier|private
name|Map
argument_list|<
name|ContextKey
argument_list|,
name|ElectionContext
argument_list|>
name|electionContexts
decl_stmt|;
DECL|field|contextKey
specifier|private
name|ContextKey
name|contextKey
decl_stmt|;
DECL|method|LeaderElector
specifier|public
name|LeaderElector
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|)
block|{
name|this
operator|.
name|zkClient
operator|=
name|zkClient
expr_stmt|;
name|zkCmdExecutor
operator|=
operator|new
name|ZkCmdExecutor
argument_list|(
name|zkClient
operator|.
name|getZkClientTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|LeaderElector
specifier|public
name|LeaderElector
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|ContextKey
name|key
parameter_list|,
name|Map
argument_list|<
name|ContextKey
argument_list|,
name|ElectionContext
argument_list|>
name|electionContexts
parameter_list|)
block|{
name|this
operator|.
name|zkClient
operator|=
name|zkClient
expr_stmt|;
name|zkCmdExecutor
operator|=
operator|new
name|ZkCmdExecutor
argument_list|(
name|zkClient
operator|.
name|getZkClientTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|electionContexts
operator|=
name|electionContexts
expr_stmt|;
name|this
operator|.
name|contextKey
operator|=
name|key
expr_stmt|;
block|}
DECL|method|getContext
specifier|public
name|ElectionContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
comment|/**    * Check if the candidate with the given n_* sequence number is the leader.    * If it is, set the leaderId on the leader zk node. If it is not, start    * watching the candidate that is in line before this one - if it goes down, check    * if this candidate is the leader again.    *    * @param replacement has someone else been the leader already?    */
DECL|method|checkIfIamLeader
specifier|private
name|void
name|checkIfIamLeader
parameter_list|(
specifier|final
name|ElectionContext
name|context
parameter_list|,
name|boolean
name|replacement
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|context
operator|.
name|checkIfIamLeaderFired
argument_list|()
expr_stmt|;
comment|// get all other numbers...
specifier|final
name|String
name|holdElectionPath
init|=
name|context
operator|.
name|electionPath
operator|+
name|ELECTION_NODE
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|seqs
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|holdElectionPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|sortSeqs
argument_list|(
name|seqs
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|intSeqs
init|=
name|getSeqs
argument_list|(
name|seqs
argument_list|)
decl_stmt|;
if|if
condition|(
name|intSeqs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Our node is no longer in line to be leader"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// We can't really rely on the sequence number stored in the old watcher, it may be stale, thus this check.
name|int
name|seq
init|=
operator|-
literal|1
decl_stmt|;
comment|// See if we've already been re-added, and this is an old context. In which case, use our current sequence number.
name|String
name|newLeaderSeq
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
name|elec
range|:
name|seqs
control|)
block|{
if|if
condition|(
name|getNodeName
argument_list|(
name|elec
argument_list|)
operator|.
name|equals
argument_list|(
name|getNodeName
argument_list|(
name|context
operator|.
name|leaderSeqPath
argument_list|)
argument_list|)
operator|&&
name|seq
operator|<
name|getSeq
argument_list|(
name|elec
argument_list|)
condition|)
block|{
name|seq
operator|=
name|getSeq
argument_list|(
name|elec
argument_list|)
expr_stmt|;
comment|// so use the current sequence number.
name|newLeaderSeq
operator|=
name|elec
expr_stmt|;
break|break;
block|}
block|}
comment|// Now, if we've been re-added, presumably we've also set up watchers and all that kind of thing, so we're done
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|newLeaderSeq
argument_list|)
operator|&&
name|seq
operator|>
name|getSeq
argument_list|(
name|context
operator|.
name|leaderSeqPath
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Node "
operator|+
name|context
operator|.
name|leaderSeqPath
operator|+
literal|" already in queue as "
operator|+
name|newLeaderSeq
operator|+
literal|" nothing to do."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Fallback in case we're all coming in here fresh and there is no node for this core already in the election queue.
if|if
condition|(
name|seq
operator|==
operator|-
literal|1
condition|)
block|{
name|seq
operator|=
name|getSeq
argument_list|(
name|context
operator|.
name|leaderSeqPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|seq
operator|<=
name|intSeqs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
condition|)
block|{
if|if
condition|(
name|seq
operator|==
name|intSeqs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|&&
operator|!
name|context
operator|.
name|leaderSeqPath
operator|.
name|equals
argument_list|(
name|holdElectionPath
operator|+
literal|"/"
operator|+
name|seqs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
comment|//somebody else already  became the leader with the same sequence id , not me
name|log
operator|.
name|info
argument_list|(
literal|"was going to be leader {} , seq(0) {}"
argument_list|,
name|context
operator|.
name|leaderSeqPath
argument_list|,
name|holdElectionPath
operator|+
literal|"/"
operator|+
name|seqs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//but someone else jumped the line
comment|// The problem is that deleting the ZK node that's watched by others
comment|// results in an unpredictable sequencing of the events and sometime the context that comes in for checking
comment|// this happens to be after the node has already taken over leadership. So just leave out of here.
comment|// This caused one of the tests to fail on having two nodes with the same name in the queue. I'm not sure
comment|// the assumption that this is a bad state is valid.
if|if
condition|(
name|getNodeName
argument_list|(
name|context
operator|.
name|leaderSeqPath
argument_list|)
operator|.
name|equals
argument_list|(
name|getNodeName
argument_list|(
name|seqs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
return|return;
block|}
name|retryElection
argument_list|(
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//join at the tail again
return|return;
block|}
try|try
block|{
name|runIamLeaderProcess
argument_list|(
name|context
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NodeExistsException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"node exists"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|retryElection
argument_list|(
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
comment|// I am not the leader - watch the node below me
name|int
name|toWatch
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|intSeqs
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|intSeqs
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|<
name|seq
operator|&&
operator|!
name|getNodeName
argument_list|(
name|context
operator|.
name|leaderSeqPath
argument_list|)
operator|.
name|equals
argument_list|(
name|getNodeName
argument_list|(
name|seqs
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
name|toWatch
operator|=
name|idx
expr_stmt|;
block|}
if|if
condition|(
name|intSeqs
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|>=
name|seq
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|toWatch
operator|<
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Our node is no longer in line to be leader"
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|String
name|watchedNode
init|=
name|holdElectionPath
operator|+
literal|"/"
operator|+
name|seqs
operator|.
name|get
argument_list|(
name|toWatch
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|getData
argument_list|(
name|watchedNode
argument_list|,
name|watcher
operator|=
operator|new
name|ElectionWatcher
argument_list|(
name|context
operator|.
name|leaderSeqPath
argument_list|,
name|watchedNode
argument_list|,
name|seq
argument_list|,
name|context
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Watching path {} to know if I could be the leader"
argument_list|,
name|watchedNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|SessionExpiredException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed setting watch"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// we couldn't set our watch - the node before us may already be down?
comment|// we need to check if we are the leader again
name|checkIfIamLeader
argument_list|(
name|context
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// TODO: get this core param out of here
DECL|method|runIamLeaderProcess
specifier|protected
name|void
name|runIamLeaderProcess
parameter_list|(
specifier|final
name|ElectionContext
name|context
parameter_list|,
name|boolean
name|weAreReplacement
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|context
operator|.
name|runLeaderProcess
argument_list|(
name|weAreReplacement
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns int given String of form n_0000000001 or n_0000000003, etc.    *     * @return sequence number    */
DECL|method|getSeq
specifier|public
specifier|static
name|int
name|getSeq
parameter_list|(
name|String
name|nStringSequence
parameter_list|)
block|{
name|int
name|seq
init|=
literal|0
decl_stmt|;
name|Matcher
name|m
init|=
name|LEADER_SEQ
operator|.
name|matcher
argument_list|(
name|nStringSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|seq
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Could not find regex match in:"
operator|+
name|nStringSequence
argument_list|)
throw|;
block|}
return|return
name|seq
return|;
block|}
DECL|method|getNodeId
specifier|private
name|String
name|getNodeId
parameter_list|(
name|String
name|nStringSequence
parameter_list|)
block|{
name|String
name|id
decl_stmt|;
name|Matcher
name|m
init|=
name|SESSION_ID
operator|.
name|matcher
argument_list|(
name|nStringSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|id
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Could not find regex match in:"
operator|+
name|nStringSequence
argument_list|)
throw|;
block|}
return|return
name|id
return|;
block|}
DECL|method|getNodeName
specifier|public
specifier|static
name|String
name|getNodeName
parameter_list|(
name|String
name|nStringSequence
parameter_list|)
block|{
name|String
name|result
decl_stmt|;
name|Matcher
name|m
init|=
name|NODE_NAME
operator|.
name|matcher
argument_list|(
name|nStringSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|result
operator|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Could not find regex match in:"
operator|+
name|nStringSequence
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Returns int list given list of form n_0000000001, n_0000000003, etc.    *     * @return int seqs    */
DECL|method|getSeqs
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|getSeqs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|seqs
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|intSeqs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|seqs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|seq
range|:
name|seqs
control|)
block|{
name|intSeqs
operator|.
name|add
argument_list|(
name|getSeq
argument_list|(
name|seq
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|intSeqs
return|;
block|}
DECL|method|joinElection
specifier|public
name|int
name|joinElection
parameter_list|(
name|ElectionContext
name|context
parameter_list|,
name|boolean
name|replacement
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
return|return
name|joinElection
argument_list|(
name|context
argument_list|,
name|replacement
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Begin participating in the election process. Gets a new sequential number      * and begins watching the node with the sequence number before it, unless it      * is the lowest number, in which case, initiates the leader process. If the      * node that is watched goes down, check if we are the new lowest node, else      * watch the next lowest numbered node.      *      * @return sequential node number      */
DECL|method|joinElection
specifier|public
name|int
name|joinElection
parameter_list|(
name|ElectionContext
name|context
parameter_list|,
name|boolean
name|replacement
parameter_list|,
name|boolean
name|joinAtHead
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|context
operator|.
name|joinedElectionFired
argument_list|()
expr_stmt|;
specifier|final
name|String
name|shardsElectZkPath
init|=
name|context
operator|.
name|electionPath
operator|+
name|LeaderElector
operator|.
name|ELECTION_NODE
decl_stmt|;
name|long
name|sessionId
init|=
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getSessionId
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|sessionId
operator|+
literal|"-"
operator|+
name|context
operator|.
name|id
decl_stmt|;
name|String
name|leaderSeqPath
init|=
literal|null
decl_stmt|;
name|boolean
name|cont
init|=
literal|true
decl_stmt|;
name|int
name|tries
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|cont
condition|)
block|{
try|try
block|{
if|if
condition|(
name|joinAtHead
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Node {} trying to join election at the head"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|OverseerTaskProcessor
operator|.
name|getSortedElectionNodes
argument_list|(
name|zkClient
argument_list|,
name|shardsElectZkPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
name|leaderSeqPath
operator|=
name|zkClient
operator|.
name|create
argument_list|(
name|shardsElectZkPath
operator|+
literal|"/"
operator|+
name|id
operator|+
literal|"-n_"
argument_list|,
literal|null
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL_SEQUENTIAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|firstInLine
init|=
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"The current head: {}"
argument_list|,
name|firstInLine
argument_list|)
expr_stmt|;
name|Matcher
name|m
init|=
name|LEADER_SEQ
operator|.
name|matcher
argument_list|(
name|firstInLine
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Could not find regex match in:"
operator|+
name|firstInLine
argument_list|)
throw|;
block|}
name|leaderSeqPath
operator|=
name|shardsElectZkPath
operator|+
literal|"/"
operator|+
name|id
operator|+
literal|"-n_"
operator|+
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
name|leaderSeqPath
argument_list|,
literal|null
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|leaderSeqPath
operator|=
name|zkClient
operator|.
name|create
argument_list|(
name|shardsElectZkPath
operator|+
literal|"/"
operator|+
name|id
operator|+
literal|"-n_"
argument_list|,
literal|null
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL_SEQUENTIAL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Joined leadership election with path: {}"
argument_list|,
name|leaderSeqPath
argument_list|)
expr_stmt|;
name|context
operator|.
name|leaderSeqPath
operator|=
name|leaderSeqPath
expr_stmt|;
name|cont
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectionLossException
name|e
parameter_list|)
block|{
comment|// we don't know if we made our node or not...
name|List
argument_list|<
name|String
argument_list|>
name|entries
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|shardsElectZkPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|foundId
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|entries
control|)
block|{
name|String
name|nodeId
init|=
name|getNodeId
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
comment|// we did create our node...
name|foundId
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|foundId
condition|)
block|{
name|cont
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|tries
operator|++
operator|>
literal|20
condition|)
block|{
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e2
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// we must have failed in creating the election node - someone else must
comment|// be working on it, lets try again
if|if
condition|(
name|tries
operator|++
operator|>
literal|20
condition|)
block|{
name|context
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|cont
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e2
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|checkIfIamLeader
argument_list|(
name|context
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
return|return
name|getSeq
argument_list|(
name|context
operator|.
name|leaderSeqPath
argument_list|)
return|;
block|}
DECL|class|ElectionWatcher
specifier|private
class|class
name|ElectionWatcher
implements|implements
name|Watcher
block|{
DECL|field|myNode
DECL|field|watchedNode
specifier|final
name|String
name|myNode
decl_stmt|,
name|watchedNode
decl_stmt|;
DECL|field|context
specifier|final
name|ElectionContext
name|context
decl_stmt|;
DECL|field|canceled
specifier|private
name|boolean
name|canceled
init|=
literal|false
decl_stmt|;
DECL|method|ElectionWatcher
specifier|private
name|ElectionWatcher
parameter_list|(
name|String
name|myNode
parameter_list|,
name|String
name|watchedNode
parameter_list|,
name|int
name|seq
parameter_list|,
name|ElectionContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|myNode
operator|=
name|myNode
expr_stmt|;
name|this
operator|.
name|watchedNode
operator|=
name|watchedNode
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|cancel
name|void
name|cancel
parameter_list|()
block|{
name|canceled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
comment|// session events are not change events,
comment|// and do not remove the watcher
if|if
condition|(
name|EventType
operator|.
name|None
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|canceled
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"This watcher is not active anymore {}"
argument_list|,
name|myNode
argument_list|)
expr_stmt|;
try|try
block|{
name|zkClient
operator|.
name|delete
argument_list|(
name|myNode
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|nne
parameter_list|)
block|{
comment|// expected . don't do anything
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"My watched node still exists and can't remove "
operator|+
name|myNode
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
try|try
block|{
comment|// am I the next leader?
name|checkIfIamLeader
argument_list|(
name|context
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Set up any ZooKeeper nodes needed for leader election.    */
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|(
specifier|final
name|ElectionContext
name|context
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|String
name|electZKPath
init|=
name|context
operator|.
name|electionPath
operator|+
name|LeaderElector
operator|.
name|ELECTION_NODE
decl_stmt|;
name|zkCmdExecutor
operator|.
name|ensureExists
argument_list|(
name|electZKPath
argument_list|,
name|zkClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/**    * Sort n string sequence list.    */
DECL|method|sortSeqs
specifier|public
specifier|static
name|void
name|sortSeqs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|seqs
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|seqs
argument_list|,
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|o1
parameter_list|,
name|String
name|o2
parameter_list|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSeq
argument_list|(
name|o1
argument_list|)
argument_list|)
operator|.
name|compareTo
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSeq
argument_list|(
name|o2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|i
operator|==
literal|0
condition|?
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
else|:
name|i
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|retryElection
name|void
name|retryElection
parameter_list|(
name|ElectionContext
name|context
parameter_list|,
name|boolean
name|joinAtHead
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|ElectionWatcher
name|watcher
init|=
name|this
operator|.
name|watcher
decl_stmt|;
name|ElectionContext
name|ctx
init|=
name|context
operator|.
name|copy
argument_list|()
decl_stmt|;
if|if
condition|(
name|electionContexts
operator|!=
literal|null
condition|)
block|{
name|electionContexts
operator|.
name|put
argument_list|(
name|contextKey
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|watcher
operator|!=
literal|null
condition|)
name|watcher
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|cancelElection
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|ctx
expr_stmt|;
name|joinElection
argument_list|(
name|ctx
argument_list|,
literal|true
argument_list|,
name|joinAtHead
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
