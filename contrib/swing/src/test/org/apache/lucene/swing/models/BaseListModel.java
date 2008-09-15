begin_unit
begin_package
DECL|package|org.apache.lucene.swing.models
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|swing
operator|.
name|models
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Iterator
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
name|javax
operator|.
name|swing
operator|.
name|AbstractListModel
import|;
end_import
begin_class
DECL|class|BaseListModel
specifier|public
class|class
name|BaseListModel
extends|extends
name|AbstractListModel
block|{
DECL|field|data
specifier|private
name|List
name|data
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|method|BaseListModel
specifier|public
name|BaseListModel
parameter_list|(
name|Iterator
name|iterator
parameter_list|)
block|{
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|data
operator|.
name|add
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|data
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getElementAt
specifier|public
name|Object
name|getElementAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|data
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|addRow
specifier|public
name|void
name|addRow
parameter_list|(
name|Object
name|toAdd
parameter_list|)
block|{
name|data
operator|.
name|add
argument_list|(
name|toAdd
argument_list|)
expr_stmt|;
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|removeRow
specifier|public
name|void
name|removeRow
parameter_list|(
name|Object
name|toRemove
parameter_list|)
block|{
name|data
operator|.
name|remove
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
