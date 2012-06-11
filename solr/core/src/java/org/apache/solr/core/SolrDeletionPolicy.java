begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexCommit
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
name|index
operator|.
name|IndexDeletionPolicy
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|schema
operator|.
name|DateField
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
name|util
operator|.
name|DateMathParser
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
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
name|Locale
import|;
end_import
begin_comment
comment|/**  * Standard Solr deletion policy that allows reserving index commit points  * for certain amounts of time to support features such as index replication  * or snapshooting directly out of a live index directory.  *  *  * @see org.apache.lucene.index.IndexDeletionPolicy  */
end_comment
begin_class
DECL|class|SolrDeletionPolicy
specifier|public
class|class
name|SolrDeletionPolicy
implements|implements
name|IndexDeletionPolicy
implements|,
name|NamedListInitializedPlugin
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrCore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxCommitAge
specifier|private
name|String
name|maxCommitAge
init|=
literal|null
decl_stmt|;
DECL|field|maxCommitsToKeep
specifier|private
name|int
name|maxCommitsToKeep
init|=
literal|1
decl_stmt|;
DECL|field|maxOptimizedCommitsToKeep
specifier|private
name|int
name|maxOptimizedCommitsToKeep
init|=
literal|0
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|String
name|keepOptimizedOnlyString
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"keepOptimizedOnly"
argument_list|)
decl_stmt|;
name|String
name|maxCommitsToKeepString
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxCommitsToKeep"
argument_list|)
decl_stmt|;
name|String
name|maxOptimizedCommitsToKeepString
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxOptimizedCommitsToKeep"
argument_list|)
decl_stmt|;
name|String
name|maxCommitAgeString
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"maxCommitAge"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxCommitsToKeepString
operator|!=
literal|null
operator|&&
name|maxCommitsToKeepString
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|maxCommitsToKeep
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|maxCommitsToKeepString
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxCommitAgeString
operator|!=
literal|null
operator|&&
name|maxCommitAgeString
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|maxCommitAge
operator|=
literal|"-"
operator|+
name|maxCommitAgeString
expr_stmt|;
if|if
condition|(
name|maxOptimizedCommitsToKeepString
operator|!=
literal|null
operator|&&
name|maxOptimizedCommitsToKeepString
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|maxOptimizedCommitsToKeep
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|maxOptimizedCommitsToKeepString
argument_list|)
expr_stmt|;
block|}
comment|// legacy support
if|if
condition|(
name|keepOptimizedOnlyString
operator|!=
literal|null
operator|&&
name|keepOptimizedOnlyString
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|boolean
name|keepOptimizedOnly
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|keepOptimizedOnlyString
argument_list|)
decl_stmt|;
if|if
condition|(
name|keepOptimizedOnly
condition|)
block|{
name|maxOptimizedCommitsToKeep
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxOptimizedCommitsToKeep
argument_list|,
name|maxCommitsToKeep
argument_list|)
expr_stmt|;
name|maxCommitsToKeep
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
DECL|method|str
specifier|static
name|String
name|str
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"commit{"
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|commit
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|FSDirectory
condition|)
block|{
name|FSDirectory
name|fsd
init|=
operator|(
name|FSDirectory
operator|)
name|dir
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"dir="
argument_list|)
operator|.
name|append
argument_list|(
name|fsd
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"dir="
argument_list|)
operator|.
name|append
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|",segFN="
argument_list|)
operator|.
name|append
argument_list|(
name|commit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",generation="
argument_list|)
operator|.
name|append
argument_list|(
name|commit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",filenames="
argument_list|)
operator|.
name|append
argument_list|(
name|commit
operator|.
name|getFileNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|str
specifier|static
name|String
name|str
parameter_list|(
name|List
name|commits
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"num="
argument_list|)
operator|.
name|append
argument_list|(
name|commits
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexCommit
name|commit
range|:
operator|(
name|List
argument_list|<
name|IndexCommit
argument_list|>
operator|)
name|commits
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|str
argument_list|(
name|commit
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Internal use for Lucene... do not explicitly call.    */
DECL|method|onInit
specifier|public
name|void
name|onInit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrDeletionPolicy.onInit: commits:"
operator|+
name|str
argument_list|(
name|commits
argument_list|)
argument_list|)
expr_stmt|;
name|updateCommits
argument_list|(
operator|(
name|List
argument_list|<
name|IndexCommit
argument_list|>
operator|)
name|commits
argument_list|)
expr_stmt|;
block|}
comment|/**    * Internal use for Lucene... do not explicitly call.    */
DECL|method|onCommit
specifier|public
name|void
name|onCommit
parameter_list|(
name|List
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrDeletionPolicy.onCommit: commits:"
operator|+
name|str
argument_list|(
name|commits
argument_list|)
argument_list|)
expr_stmt|;
name|updateCommits
argument_list|(
operator|(
name|List
argument_list|<
name|IndexCommit
argument_list|>
operator|)
name|commits
argument_list|)
expr_stmt|;
block|}
DECL|method|updateCommits
specifier|private
name|void
name|updateCommits
parameter_list|(
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
block|{
comment|// to be safe, we should only call delete on a commit point passed to us
comment|// in this specific call (may be across diff IndexWriter instances).
comment|// this will happen rarely, so just synchronize everything
comment|// for safety and to avoid race conditions
synchronized|synchronized
init|(
name|this
init|)
block|{
name|long
name|maxCommitAgeTimeStamp
init|=
operator|-
literal|1L
decl_stmt|;
name|IndexCommit
name|newest
init|=
name|commits
operator|.
name|get
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"newest commit = "
operator|+
name|newest
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|singleSegKept
init|=
operator|(
name|newest
operator|.
name|getSegmentCount
argument_list|()
operator|==
literal|1
operator|)
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|int
name|totalKept
init|=
literal|1
decl_stmt|;
comment|// work our way from newest to oldest, skipping the first since we always want to keep it.
for|for
control|(
name|int
name|i
init|=
name|commits
operator|.
name|size
argument_list|()
operator|-
literal|2
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|IndexCommit
name|commit
init|=
name|commits
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// delete anything too old, regardless of other policies
try|try
block|{
if|if
condition|(
name|maxCommitAge
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|maxCommitAgeTimeStamp
operator|==
operator|-
literal|1
condition|)
block|{
name|DateMathParser
name|dmp
init|=
operator|new
name|DateMathParser
argument_list|(
name|DateField
operator|.
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|maxCommitAgeTimeStamp
operator|=
name|dmp
operator|.
name|parseMath
argument_list|(
name|maxCommitAge
argument_list|)
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|IndexDeletionPolicyWrapper
operator|.
name|getCommitTimestamp
argument_list|(
name|commit
argument_list|)
operator|<
name|maxCommitAgeTimeStamp
condition|)
block|{
name|commit
operator|.
name|delete
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
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
literal|"Exception while checking commit point's age for deletion"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|singleSegKept
operator|<
name|maxOptimizedCommitsToKeep
operator|&&
name|commit
operator|.
name|getSegmentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|totalKept
operator|++
expr_stmt|;
name|singleSegKept
operator|++
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|totalKept
operator|<
name|maxCommitsToKeep
condition|)
block|{
name|totalKept
operator|++
expr_stmt|;
continue|continue;
block|}
name|commit
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|// end synchronized
block|}
DECL|method|getId
specifier|private
name|String
name|getId
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|commit
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
comment|// For anything persistent, make something that will
comment|// be the same, regardless of the Directory instance.
if|if
condition|(
name|dir
operator|instanceof
name|FSDirectory
condition|)
block|{
name|FSDirectory
name|fsd
init|=
operator|(
name|FSDirectory
operator|)
name|dir
decl_stmt|;
name|File
name|fdir
init|=
name|fsd
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fdir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|commit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getMaxCommitAge
specifier|public
name|String
name|getMaxCommitAge
parameter_list|()
block|{
return|return
name|maxCommitAge
return|;
block|}
DECL|method|getMaxCommitsToKeep
specifier|public
name|int
name|getMaxCommitsToKeep
parameter_list|()
block|{
return|return
name|maxCommitsToKeep
return|;
block|}
DECL|method|getMaxOptimizedCommitsToKeep
specifier|public
name|int
name|getMaxOptimizedCommitsToKeep
parameter_list|()
block|{
return|return
name|maxOptimizedCommitsToKeep
return|;
block|}
DECL|method|setMaxCommitsToKeep
specifier|public
name|void
name|setMaxCommitsToKeep
parameter_list|(
name|int
name|maxCommitsToKeep
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|maxCommitsToKeep
operator|=
name|maxCommitsToKeep
expr_stmt|;
block|}
block|}
DECL|method|setMaxOptimizedCommitsToKeep
specifier|public
name|void
name|setMaxOptimizedCommitsToKeep
parameter_list|(
name|int
name|maxOptimizedCommitsToKeep
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|maxOptimizedCommitsToKeep
operator|=
name|maxOptimizedCommitsToKeep
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
