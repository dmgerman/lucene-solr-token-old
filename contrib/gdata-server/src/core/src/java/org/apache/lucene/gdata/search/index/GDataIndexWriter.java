begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchema
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LogDocMergePolicy
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
begin_comment
comment|/**  * Configurable decorator for a lucene {@link IndexWriter}  *   *  *   */
end_comment
begin_class
DECL|class|GDataIndexWriter
specifier|public
class|class
name|GDataIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|GDataIndexWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serviceName
specifier|private
name|String
name|serviceName
decl_stmt|;
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|IndexSchema
name|config
parameter_list|)
block|{
name|this
operator|.
name|serviceName
operator|=
name|config
operator|.
name|getName
argument_list|()
expr_stmt|;
name|setUseCompoundFile
argument_list|(
name|config
operator|.
name|isUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getMaxBufferedDocs
argument_list|()
operator|!=
name|IndexSchema
operator|.
name|NOT_SET_VALUE
condition|)
name|setMaxBufferedDocs
argument_list|(
name|config
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getMaxMergeDocs
argument_list|()
operator|!=
name|IndexSchema
operator|.
name|NOT_SET_VALUE
operator|&&
name|getMergePolicy
argument_list|()
operator|instanceof
name|LogDocMergePolicy
condition|)
name|setMaxMergeDocs
argument_list|(
name|config
operator|.
name|getMaxMergeDocs
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getMergeFactor
argument_list|()
operator|!=
name|IndexSchema
operator|.
name|NOT_SET_VALUE
condition|)
name|setMergeFactor
argument_list|(
name|config
operator|.
name|getMergeFactor
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getMaxFieldLength
argument_list|()
operator|!=
name|IndexSchema
operator|.
name|NOT_SET_VALUE
condition|)
name|setMaxFieldLength
argument_list|(
name|config
operator|.
name|getMaxFieldLength
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getWriteLockTimeout
argument_list|()
operator|!=
name|IndexSchema
operator|.
name|NOT_SET_VALUE
condition|)
name|setWriteLockTimeout
argument_list|(
name|config
operator|.
name|getWriteLockTimeout
argument_list|()
argument_list|)
expr_stmt|;
comment|//no commit lock anymore
comment|//TODO fix this
comment|//        if (config.getCommitLockTimeout() != IndexSchema.NOT_SET_VALUE)
comment|//            setCommitLockTimeout(config.getCommitLockTimeout());
block|}
comment|/**      * Creates and configures a new GdataIndexWriter      *       * @param arg0 -      *            the index directory      * @param arg1 -      *            create index      * @param arg2 -      *            the index schema configuration including all parameter to set      *            up the index writer      * @throws IOException      *             -if the directory cannot be read/written to, or if it does      *             not exist, and<code>create</code> is<code>false</code>      */
DECL|method|GDataIndexWriter
specifier|protected
name|GDataIndexWriter
parameter_list|(
name|Directory
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|,
name|IndexSchema
name|arg2
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*          * Use Schema Analyzer rather than service analyzer.           * Schema analyzer returns either the service analyzer or a per field analyzer if configured.          */
name|super
argument_list|(
name|arg0
argument_list|,
operator|(
name|arg2
operator|==
literal|null
condition|?
operator|new
name|StandardAnalyzer
argument_list|()
else|:
name|arg2
operator|.
name|getSchemaAnalyzer
argument_list|()
operator|)
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
if|if
condition|(
name|arg2
operator|==
literal|null
condition|)
block|{
comment|/*              * if no schema throw exception - schema is mandatory for the index writer.              */
try|try
block|{
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"configuration must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|initialize
argument_list|(
name|arg2
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.index.IndexWriter#close()      */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing GdataIndexWriter for service "
operator|+
name|this
operator|.
name|serviceName
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
