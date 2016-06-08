begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_comment
comment|/**  * A collection of standard params used by Update handlers  *  * @version $Id$  * @since solr 1.2  */
end_comment
begin_interface
DECL|interface|UpdateParams
specifier|public
interface|interface
name|UpdateParams
block|{
comment|/** wait till the command has flushed */
DECL|field|WAIT_FLUSH
specifier|public
specifier|static
name|String
name|WAIT_FLUSH
init|=
literal|"waitFlush"
decl_stmt|;
comment|/** wait for the search to warm up */
DECL|field|WAIT_SEARCHER
specifier|public
specifier|static
name|String
name|WAIT_SEARCHER
init|=
literal|"waitSearcher"
decl_stmt|;
comment|/** overwrite indexing fields */
DECL|field|OVERWRITE
specifier|public
specifier|static
name|String
name|OVERWRITE
init|=
literal|"overwrite"
decl_stmt|;
comment|/** Commit everything after the command completes */
DECL|field|COMMIT
specifier|public
specifier|static
name|String
name|COMMIT
init|=
literal|"commit"
decl_stmt|;
comment|/** Optimize the index and commit everything after the command completes */
DECL|field|OPTIMIZE
specifier|public
specifier|static
name|String
name|OPTIMIZE
init|=
literal|"optimize"
decl_stmt|;
comment|/** Rollback update commands */
DECL|field|ROLLBACK
specifier|public
specifier|static
name|String
name|ROLLBACK
init|=
literal|"rollback"
decl_stmt|;
comment|/** Select the update processor to use.  A RequestHandler may or may not respect this parameter */
DECL|field|UPDATE_PROCESSOR
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_PROCESSOR
init|=
literal|"update.processor"
decl_stmt|;
comment|/**    * If optimizing, set the maximum number of segments left in the index after optimization.  1 is the default (and is equivalent to calling IndexWriter.optimize() in Lucene).    */
DECL|field|MAX_OPTIMIZE_SEGMENTS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_OPTIMIZE_SEGMENTS
init|=
literal|"maxSegments"
decl_stmt|;
block|}
end_interface
end_unit
