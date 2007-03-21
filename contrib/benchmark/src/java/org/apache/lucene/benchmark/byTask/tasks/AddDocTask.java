begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|DocMaker
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
begin_comment
comment|/**  * Add a document, optionally with of a certain size.  *<br>Other side effects: none.  *<br>Relevant properties:<code>doc.add.log.step</code>.  *<br>Takes optional param: document size.   */
end_comment
begin_class
DECL|class|AddDocTask
specifier|public
class|class
name|AddDocTask
extends|extends
name|PerfTask
block|{
comment|/**    * Default value for property<code>doc.add.log.step<code> - indicating how often     * an "added N docs" message should be logged.      */
DECL|field|DEFAULT_ADD_DOC_LOG_STEP
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_ADD_DOC_LOG_STEP
init|=
literal|500
decl_stmt|;
DECL|method|AddDocTask
specifier|public
name|AddDocTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|field|logStep
specifier|private
specifier|static
name|int
name|logStep
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docSize
specifier|private
name|int
name|docSize
init|=
literal|0
decl_stmt|;
comment|// volatile data passed between setup(), doLogic(), tearDown().
DECL|field|doc
specifier|private
name|Document
name|doc
init|=
literal|null
decl_stmt|;
comment|/*    *  (non-Javadoc)    * @see PerfTask#setup()    */
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|DocMaker
name|docMaker
init|=
name|getRunData
argument_list|()
operator|.
name|getDocMaker
argument_list|()
decl_stmt|;
if|if
condition|(
name|docSize
operator|>
literal|0
condition|)
block|{
name|doc
operator|=
name|docMaker
operator|.
name|makeDocument
argument_list|(
name|docSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
name|docMaker
operator|.
name|makeDocument
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)    * @see PerfTask#tearDown()    */
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|DocMaker
name|docMaker
init|=
name|getRunData
argument_list|()
operator|.
name|getDocMaker
argument_list|()
decl_stmt|;
name|log
argument_list|(
name|docMaker
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|getRunData
argument_list|()
operator|.
name|getIndexWriter
argument_list|()
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
DECL|method|log
specifier|private
name|void
name|log
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|logStep
operator|<
literal|0
condition|)
block|{
comment|// avoid sync although race possible here
name|logStep
operator|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"doc.add.log.step"
argument_list|,
name|DEFAULT_ADD_DOC_LOG_STEP
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|logStep
operator|>
literal|0
operator|&&
operator|(
name|count
operator|%
name|logStep
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> processed (add) "
operator|+
name|count
operator|+
literal|" docs"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the params (docSize only)    * @param params docSize, or 0 for no limit.    */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|docSize
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#supportsParams()    */
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
