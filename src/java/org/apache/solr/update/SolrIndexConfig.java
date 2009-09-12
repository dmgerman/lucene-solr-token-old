begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|core
operator|.
name|SolrConfig
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
name|LogByteSizeMergePolicy
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
name|ConcurrentMergeScheduler
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
name|IndexWriter
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
comment|//
end_comment
begin_comment
comment|// For performance reasons, we don't want to re-read
end_comment
begin_comment
comment|// config params each time an index writer is created.
end_comment
begin_comment
comment|// This config object encapsulates IndexWriter config params.
end_comment
begin_comment
comment|//
end_comment
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|SolrIndexConfig
specifier|public
class|class
name|SolrIndexConfig
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrIndexConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|defaultsName
specifier|public
specifier|static
specifier|final
name|String
name|defaultsName
init|=
literal|"indexDefaults"
decl_stmt|;
DECL|field|DEFAULT_MERGE_POLICY_CLASSNAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MERGE_POLICY_CLASSNAME
init|=
name|LogByteSizeMergePolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_MERGE_SCHEDULER_CLASSNAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
init|=
name|ConcurrentMergeScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|defaultDefaults
specifier|static
specifier|final
name|SolrIndexConfig
name|defaultDefaults
init|=
operator|new
name|SolrIndexConfig
argument_list|()
decl_stmt|;
DECL|method|SolrIndexConfig
specifier|private
name|SolrIndexConfig
parameter_list|()
block|{
name|useCompoundFile
operator|=
literal|true
expr_stmt|;
name|maxBufferedDocs
operator|=
operator|-
literal|1
expr_stmt|;
name|maxMergeDocs
operator|=
operator|-
literal|1
expr_stmt|;
name|mergeFactor
operator|=
operator|-
literal|1
expr_stmt|;
name|ramBufferSizeMB
operator|=
literal|16
expr_stmt|;
name|maxFieldLength
operator|=
operator|-
literal|1
expr_stmt|;
name|writeLockTimeout
operator|=
operator|-
literal|1
expr_stmt|;
name|commitLockTimeout
operator|=
operator|-
literal|1
expr_stmt|;
name|lockType
operator|=
literal|null
expr_stmt|;
name|mergePolicyClassName
operator|=
name|DEFAULT_MERGE_POLICY_CLASSNAME
expr_stmt|;
name|mergeSchedulerClassname
operator|=
name|DEFAULT_MERGE_SCHEDULER_CLASSNAME
expr_stmt|;
name|luceneAutoCommit
operator|=
literal|false
expr_stmt|;
name|termIndexInterval
operator|=
name|IndexWriter
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
expr_stmt|;
block|}
DECL|field|useCompoundFile
specifier|public
specifier|final
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|public
specifier|final
name|int
name|maxBufferedDocs
decl_stmt|;
DECL|field|maxMergeDocs
specifier|public
specifier|final
name|int
name|maxMergeDocs
decl_stmt|;
DECL|field|mergeFactor
specifier|public
specifier|final
name|int
name|mergeFactor
decl_stmt|;
DECL|field|ramBufferSizeMB
specifier|public
specifier|final
name|double
name|ramBufferSizeMB
decl_stmt|;
DECL|field|maxFieldLength
specifier|public
specifier|final
name|int
name|maxFieldLength
decl_stmt|;
DECL|field|writeLockTimeout
specifier|public
specifier|final
name|int
name|writeLockTimeout
decl_stmt|;
DECL|field|commitLockTimeout
specifier|public
specifier|final
name|int
name|commitLockTimeout
decl_stmt|;
DECL|field|lockType
specifier|public
specifier|final
name|String
name|lockType
decl_stmt|;
DECL|field|mergePolicyClassName
specifier|public
specifier|final
name|String
name|mergePolicyClassName
decl_stmt|;
DECL|field|mergeSchedulerClassname
specifier|public
specifier|final
name|String
name|mergeSchedulerClassname
decl_stmt|;
DECL|field|luceneAutoCommit
specifier|public
specifier|final
name|boolean
name|luceneAutoCommit
decl_stmt|;
DECL|field|termIndexInterval
specifier|public
specifier|final
name|int
name|termIndexInterval
decl_stmt|;
DECL|field|infoStreamFile
specifier|public
name|String
name|infoStreamFile
init|=
literal|null
decl_stmt|;
DECL|method|SolrIndexConfig
specifier|public
name|SolrIndexConfig
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|prefix
parameter_list|,
name|SolrIndexConfig
name|def
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
name|prefix
operator|=
name|defaultsName
expr_stmt|;
if|if
condition|(
name|def
operator|==
literal|null
condition|)
name|def
operator|=
name|defaultDefaults
expr_stmt|;
name|useCompoundFile
operator|=
name|solrConfig
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/useCompoundFile"
argument_list|,
name|def
operator|.
name|useCompoundFile
argument_list|)
expr_stmt|;
name|maxBufferedDocs
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxBufferedDocs"
argument_list|,
name|def
operator|.
name|maxBufferedDocs
argument_list|)
expr_stmt|;
name|maxMergeDocs
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxMergeDocs"
argument_list|,
name|def
operator|.
name|maxMergeDocs
argument_list|)
expr_stmt|;
name|mergeFactor
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/mergeFactor"
argument_list|,
name|def
operator|.
name|mergeFactor
argument_list|)
expr_stmt|;
name|ramBufferSizeMB
operator|=
name|solrConfig
operator|.
name|getDouble
argument_list|(
name|prefix
operator|+
literal|"/ramBufferSizeMB"
argument_list|,
name|def
operator|.
name|ramBufferSizeMB
argument_list|)
expr_stmt|;
name|maxFieldLength
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxFieldLength"
argument_list|,
name|def
operator|.
name|maxFieldLength
argument_list|)
expr_stmt|;
name|writeLockTimeout
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/writeLockTimeout"
argument_list|,
name|def
operator|.
name|writeLockTimeout
argument_list|)
expr_stmt|;
name|commitLockTimeout
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/commitLockTimeout"
argument_list|,
name|def
operator|.
name|commitLockTimeout
argument_list|)
expr_stmt|;
name|lockType
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/lockType"
argument_list|,
name|def
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|mergePolicyClassName
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergePolicy"
argument_list|,
name|def
operator|.
name|mergePolicyClassName
argument_list|)
expr_stmt|;
name|mergeSchedulerClassname
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/mergeScheduler"
argument_list|,
name|def
operator|.
name|mergeSchedulerClassname
argument_list|)
expr_stmt|;
name|luceneAutoCommit
operator|=
name|solrConfig
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/luceneAutoCommit"
argument_list|,
name|def
operator|.
name|luceneAutoCommit
argument_list|)
expr_stmt|;
name|termIndexInterval
operator|=
name|solrConfig
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/termIndexInterval"
argument_list|,
name|def
operator|.
name|termIndexInterval
argument_list|)
expr_stmt|;
name|boolean
name|infoStreamEnabled
init|=
name|solrConfig
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/infoStream"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStreamEnabled
condition|)
block|{
name|infoStreamFile
operator|=
name|solrConfig
operator|.
name|get
argument_list|(
name|prefix
operator|+
literal|"/infoStream/@file"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"IndexWriter infoStream debug log is enabled: "
operator|+
name|infoStreamFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
