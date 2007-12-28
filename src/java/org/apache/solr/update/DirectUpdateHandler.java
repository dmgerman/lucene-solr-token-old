begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  */
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|TermDocs
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
name|Term
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
name|document
operator|.
name|Document
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
name|Query
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
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
name|search
operator|.
name|SolrIndexSearcher
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
name|search
operator|.
name|QueryParsing
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
name|update
operator|.
name|UpdateHandler
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
begin_comment
comment|/**  *<code>DirectUpdateHandler</code> implements an UpdateHandler where documents are added  * directly to the main lucene index as opposed to adding to a separate smaller index.  * For this reason, not all combinations to/from pending and committed are supported.  *  * @version $Id$  * @since solr 0.9  */
end_comment
begin_class
DECL|class|DirectUpdateHandler
specifier|public
class|class
name|DirectUpdateHandler
extends|extends
name|UpdateHandler
block|{
comment|// the set of ids in the "pending set" (those docs that have been added, but
comment|// that are not yet visible.
DECL|field|pset
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|pset
decl_stmt|;
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|searcher
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|numAdds
name|int
name|numAdds
init|=
literal|0
decl_stmt|;
comment|// number of docs added to the pending set
DECL|field|numPending
name|int
name|numPending
init|=
literal|0
decl_stmt|;
comment|// number of docs currently in this pending set
DECL|field|numDeleted
name|int
name|numDeleted
init|=
literal|0
decl_stmt|;
comment|// number of docs deleted or
DECL|method|DirectUpdateHandler
specifier|public
name|DirectUpdateHandler
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|pset
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
literal|256
argument_list|)
expr_stmt|;
block|}
DECL|method|openWriter
specifier|protected
name|void
name|openWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
name|createMainIndexWriter
argument_list|(
literal|"DirectUpdateHandler"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeWriter
specifier|protected
name|void
name|closeWriter
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// TODO: if an exception causes the writelock to not be
comment|// released, we could delete it here.
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|openSearcher
specifier|protected
name|void
name|openSearcher
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
block|{
name|searcher
operator|=
name|core
operator|.
name|newSearcher
argument_list|(
literal|"DirectUpdateHandler"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeSearcher
specifier|protected
name|void
name|closeSearcher
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// TODO: if an exception causes the writelock to not be
comment|// released, we could delete it here.
name|searcher
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|doAdd
specifier|protected
name|void
name|doAdd
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|closeSearcher
argument_list|()
expr_stmt|;
name|openWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|existsInIndex
specifier|protected
name|boolean
name|existsInIndex
parameter_list|(
name|String
name|indexedId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Operation requires schema to have a unique key field"
argument_list|)
throw|;
name|closeWriter
argument_list|()
expr_stmt|;
name|openSearcher
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|TermDocs
name|tdocs
init|=
literal|null
decl_stmt|;
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
try|try
block|{
name|tdocs
operator|=
name|ir
operator|.
name|termDocs
argument_list|(
name|idTerm
argument_list|(
name|indexedId
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|tdocs
operator|.
name|next
argument_list|()
condition|)
name|exists
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|tdocs
operator|!=
literal|null
condition|)
name|tdocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
block|}
return|return
name|exists
return|;
block|}
DECL|method|deleteInIndex
specifier|protected
name|int
name|deleteInIndex
parameter_list|(
name|String
name|indexedId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Operation requires schema to have a unique key field"
argument_list|)
throw|;
name|closeWriter
argument_list|()
expr_stmt|;
name|openSearcher
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|TermDocs
name|tdocs
init|=
literal|null
decl_stmt|;
name|int
name|num
init|=
literal|0
decl_stmt|;
try|try
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|,
name|indexedId
argument_list|)
decl_stmt|;
name|num
operator|=
name|ir
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|.
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|core
operator|.
name|log
operator|.
name|finest
argument_list|(
name|core
operator|.
name|getLogId
argument_list|()
operator|+
literal|"deleted "
operator|+
name|num
operator|+
literal|" docs matching id "
operator|+
name|idFieldType
operator|.
name|indexedToReadable
argument_list|(
name|indexedId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|tdocs
operator|!=
literal|null
condition|)
name|tdocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
block|}
return|return
name|num
return|;
block|}
DECL|method|overwrite
specifier|protected
name|void
name|overwrite
parameter_list|(
name|String
name|indexedId
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexedId
operator|==
literal|null
condition|)
name|indexedId
operator|=
name|getIndexedId
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|deleteInIndex
argument_list|(
name|indexedId
argument_list|)
expr_stmt|;
name|doAdd
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/************** Direct update handler - pseudo code ***********   def add(doc, id, allowDups, overwritePending, overwriteCommitted):     if not overwritePending and not overwriteCommitted:       #special case... no need to check pending set, and we don't keep       #any state around about this addition       if allowDups:         committed[id]=doc  #100         return       else:         #if no dups allowed, we must check the *current* index (pending and committed)         if not committed[id]: committed[id]=doc  #000         return     #001  (searchd addConditionally)     if not allowDups and not overwritePending and pending[id]: return     del committed[id]  #delete from pending and committed  111 011     committed[id]=doc     pending[id]=True   ****************************************************************/
comment|// could return the number of docs deleted, but is that always possible to know???
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|&&
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"meaningless command: "
operator|+
name|cmd
argument_list|)
throw|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|||
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"operation not supported"
operator|+
name|cmd
argument_list|)
throw|;
name|String
name|indexedId
init|=
name|idFieldType
operator|.
name|toInternal
argument_list|(
name|cmd
operator|.
name|id
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|deleteInIndex
argument_list|(
name|indexedId
argument_list|)
expr_stmt|;
name|pset
operator|.
name|remove
argument_list|(
name|indexedId
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO - return number of docs deleted?
comment|// Depending on implementation, we may not be able to immediately determine num...
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|&&
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"meaningless command: "
operator|+
name|cmd
argument_list|)
throw|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|||
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"operation not supported"
operator|+
name|cmd
argument_list|)
throw|;
name|Query
name|q
init|=
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|cmd
operator|.
name|query
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|int
name|totDeleted
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|closeWriter
argument_list|()
expr_stmt|;
name|openSearcher
argument_list|()
expr_stmt|;
comment|// if we want to count the number of docs that were deleted, then
comment|// we need a new instance of the DeleteHitCollector
specifier|final
name|DeleteHitCollector
name|deleter
init|=
operator|new
name|DeleteHitCollector
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|deleter
argument_list|)
expr_stmt|;
name|totDeleted
operator|=
name|deleter
operator|.
name|deleted
expr_stmt|;
block|}
if|if
condition|(
name|core
operator|.
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|core
operator|.
name|log
operator|.
name|fine
argument_list|(
name|core
operator|.
name|getLogId
argument_list|()
operator|+
literal|"docs deleted:"
operator|+
name|totDeleted
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**************** old hit collector... new one is in base class   // final DeleteHitCollector deleter = new DeleteHitCollector();   class DeleteHitCollector extends HitCollector {     public int deleted=0;     public void collect(int doc, float score) {       try {         searcher.getReader().delete(doc);         deleted++;       } catch (IOException e) {         try { closeSearcher(); } catch (Exception ee) { SolrException.log(SolrCore.log,ee); }         SolrException.log(SolrCore.log,e);         throw new SolrException( SolrException.StatusCode.SERVER_ERROR,"Error deleting doc# "+doc,e);       }     }   }   ***************************/
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|Future
index|[]
name|waitSearcher
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|waitSearcher
condition|)
block|{
name|waitSearcher
operator|=
operator|new
name|Future
index|[
literal|1
index|]
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|pset
operator|.
name|clear
argument_list|()
expr_stmt|;
name|closeSearcher
argument_list|()
expr_stmt|;
comment|// flush any deletes
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|openWriter
argument_list|()
expr_stmt|;
comment|// writer needs to be open to optimize
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
name|closeWriter
argument_list|()
expr_stmt|;
name|callPostCommitCallbacks
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|callPostOptimizeCallbacks
argument_list|()
expr_stmt|;
block|}
name|core
operator|.
name|getSearcher
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|waitSearcher
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|waitSearcher
operator|!=
literal|null
operator|&&
name|waitSearcher
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|waitSearcher
index|[
literal|0
index|]
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return;
block|}
comment|///////////////////////////////////////////////////////////////////
comment|/////////////////// helper method for each add type ///////////////
comment|///////////////////////////////////////////////////////////////////
DECL|method|addNoOverwriteNoDups
specifier|protected
name|int
name|addNoOverwriteNoDups
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|indexedId
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|indexedId
operator|=
name|getIndexedId
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|existsInIndex
argument_list|(
name|cmd
operator|.
name|indexedId
argument_list|)
condition|)
return|return
literal|0
return|;
name|doAdd
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
DECL|method|addConditionally
specifier|protected
name|int
name|addConditionally
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|indexedId
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|indexedId
operator|=
name|getIndexedId
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|pset
operator|.
name|contains
argument_list|(
name|cmd
operator|.
name|indexedId
argument_list|)
condition|)
return|return
literal|0
return|;
comment|// since case 001 is currently the only case to use pset, only add
comment|// to it in that instance.
name|pset
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|indexedId
argument_list|)
expr_stmt|;
name|overwrite
argument_list|(
name|cmd
operator|.
name|indexedId
argument_list|,
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
comment|// overwrite both pending and committed
DECL|method|overwriteBoth
specifier|protected
specifier|synchronized
name|int
name|overwriteBoth
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|overwrite
argument_list|(
name|cmd
operator|.
name|indexedId
argument_list|,
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
comment|// add without checking
DECL|method|allowDups
specifier|protected
specifier|synchronized
name|int
name|allowDups
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|doAdd
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
DECL|method|addDoc
specifier|public
name|int
name|addDoc
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if there is no ID field, use allowDups
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|allowDups
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|overwriteCommitted
operator|=
literal|false
expr_stmt|;
name|cmd
operator|.
name|overwritePending
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
return|return
name|addNoOverwriteNoDups
argument_list|(
name|cmd
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
return|return
name|addConditionally
argument_list|(
name|cmd
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
comment|// return overwriteBoth(cmd);
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
return|return
name|overwriteBoth
argument_list|(
name|cmd
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
return|return
name|allowDups
argument_list|(
name|cmd
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
comment|// return overwriteBoth(cmd);
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
comment|// return overwriteBoth(cmd);
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
return|return
name|overwriteBoth
argument_list|(
name|cmd
argument_list|)
return|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|closeSearcher
argument_list|()
expr_stmt|;
name|closeWriter
argument_list|()
expr_stmt|;
block|}
block|}
comment|/////////////////////////////////////////////////////////////////////
comment|// SolrInfoMBean stuff: Statistics and Module Info
comment|/////////////////////////////////////////////////////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|DirectUpdateHandler
operator|.
name|class
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
literal|"Update handler that directly changes the on-disk main lucene index"
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
name|CORE
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
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
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
return|return
name|lst
return|;
block|}
block|}
end_class
end_unit
