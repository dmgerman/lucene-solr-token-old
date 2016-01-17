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
comment|/*  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|document
operator|.
name|DocumentStoredFieldVisitor
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
begin_comment
comment|/**  * Search and Traverse and Retrieve docs task using a  * FieldVisitor loading only the requested fields.  *  *<p>Note: This task reuses the reader if it is already open.  * Otherwise a reader is opened at start and closed at the end.  *  *<p>Takes optional param: comma separated list of Fields to load.</p>  *   *<p>Other side effects: counts additional 1 (record) for each traversed hit,   * and 1 more for each retrieved (non null) document.</p>  */
end_comment
begin_class
DECL|class|SearchTravRetLoadFieldSelectorTask
specifier|public
class|class
name|SearchTravRetLoadFieldSelectorTask
extends|extends
name|SearchTravTask
block|{
DECL|field|fieldsToLoad
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsToLoad
decl_stmt|;
DECL|method|SearchTravRetLoadFieldSelectorTask
specifier|public
name|SearchTravRetLoadFieldSelectorTask
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
annotation|@
name|Override
DECL|method|withRetrieve
specifier|public
name|boolean
name|withRetrieve
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|retrieveDoc
specifier|protected
name|Document
name|retrieveDoc
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldsToLoad
operator|==
literal|null
condition|)
block|{
return|return
name|ir
operator|.
name|document
argument_list|(
name|id
argument_list|)
return|;
block|}
else|else
block|{
name|DocumentStoredFieldVisitor
name|visitor
init|=
operator|new
name|DocumentStoredFieldVisitor
argument_list|(
name|fieldsToLoad
argument_list|)
decl_stmt|;
name|ir
operator|.
name|document
argument_list|(
name|id
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|getDocument
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
comment|// cannot just call super.setParams(), b/c its params differ.
name|fieldsToLoad
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|params
argument_list|,
literal|","
argument_list|)
init|;
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|;
control|)
block|{
name|String
name|s
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|fieldsToLoad
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)   * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#supportsParams()   */
annotation|@
name|Override
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
