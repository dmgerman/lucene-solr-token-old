begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|UnsupportedEncodingException
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
comment|/**  * Leader Election process. This class contains the logic by which a  * leader is chosen. First call * {@link #setup(ElectionContext)} to ensure  * the election process is init'd. Next call  * {@link #joinElection(ElectionContext)} to start the leader election.  *   * The implementation follows the classic ZooKeeper recipe of creating an  * ephemeral, sequential node for each candidate and then looking at the set  * of such nodes - if the created node is the lowest sequential node, the  * candidate that created the node is the leader. If not, the candidate puts  * a watch on the next lowest node it finds, and if that node goes down,   * starts the whole process over by checking if it's the lowest sequential node, etc.  *   * TODO: now we could just reuse the lock package code for leader election  */
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
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LeaderElector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ELECTION_NODE
specifier|private
specifier|static
specifier|final
name|String
name|ELECTION_NODE
init|=
literal|"/election"
decl_stmt|;
DECL|field|LEADER_SEQ
specifier|private
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
DECL|field|zkClient
specifier|protected
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|zkCmdExecutor
specifier|private
name|ZkCmdExecutor
name|zkCmdExecutor
init|=
operator|new
name|ZkCmdExecutor
argument_list|()
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
block|}
comment|/**    * Check if the candidate with the given n_* sequence number is the leader.    * If it is, set the leaderId on the leader zk node. If it is not, start    * watching the candidate that is in line before this one - if it goes down, check    * if this candidate is the leader again.    * @param leaderSeqPath     *     * @param seq    * @param context     * @param replacement has someone else been the leader already?    * @throws KeeperException    * @throws InterruptedException    * @throws IOException     * @throws UnsupportedEncodingException    */
DECL|method|checkIfIamLeader
specifier|private
name|void
name|checkIfIamLeader
parameter_list|(
specifier|final
name|String
name|leaderSeqPath
parameter_list|,
specifier|final
name|int
name|seq
parameter_list|,
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
name|runIamLeaderProcess
argument_list|(
name|leaderSeqPath
argument_list|,
name|context
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// I am not the leader - watch the node below me
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|intSeqs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|s
init|=
name|intSeqs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|seq
operator|<
name|s
condition|)
block|{
comment|// we found who we come before - watch the guy in front
break|break;
block|}
block|}
name|int
name|index
init|=
name|i
operator|-
literal|2
decl_stmt|;
if|if
condition|(
name|index
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
name|zkClient
operator|.
name|getData
argument_list|(
name|holdElectionPath
operator|+
literal|"/"
operator|+
name|seqs
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
comment|// am I the next leader?
try|try
block|{
name|checkIfIamLeader
argument_list|(
name|leaderSeqPath
argument_list|,
name|seq
argument_list|,
name|context
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
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
catch|catch
parameter_list|(
name|IOException
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
argument_list|,
literal|null
argument_list|,
literal|true
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
comment|// we couldn't set our watch - the node before us may already be down?
comment|// we need to check if we are the leader again
name|checkIfIamLeader
argument_list|(
name|leaderSeqPath
argument_list|,
name|seq
argument_list|,
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
name|String
name|leaderSeqPath
parameter_list|,
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
name|leaderSeqPath
argument_list|,
name|weAreReplacement
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns int given String of form n_0000000001 or n_0000000003, etc.    *     * @param nStringSequence    * @return    */
DECL|method|getSeq
specifier|private
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
comment|/**    * Returns int list given list of form n_0000000001, n_0000000003, etc.    *     * @param seqs    * @return    */
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
argument_list|<
name|Integer
argument_list|>
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
comment|/**    * Begin participating in the election process. Gets a new sequential number    * and begins watching the node with the sequence number before it, unless it    * is the lowest number, in which case, initiates the leader process. If the    * node that is watched goes down, check if we are the new lowest node, else    * watch the next lowest numbered node.    *     * @param context    * @return sequential node number    * @throws KeeperException    * @throws InterruptedException    * @throws IOException     * @throws UnsupportedEncodingException    */
DECL|method|joinElection
specifier|public
name|int
name|joinElection
parameter_list|(
name|ElectionContext
name|context
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
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
throw|throw
name|e
throw|;
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
literal|9
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
name|cont
operator|=
literal|true
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|seq
init|=
name|getSeq
argument_list|(
name|leaderSeqPath
argument_list|)
decl_stmt|;
name|checkIfIamLeader
argument_list|(
name|leaderSeqPath
argument_list|,
name|seq
argument_list|,
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|seq
return|;
block|}
comment|/**    * Set up any ZooKeeper nodes needed for leader election.    *     * @param context    * @throws InterruptedException    * @throws KeeperException    */
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
block|}
comment|/**    * Sort n string sequence list.    *     * @param seqs    */
DECL|method|sortSeqs
specifier|private
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
return|return
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
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
