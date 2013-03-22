begin_unit
begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|CharacterIterator
import|;
end_import
begin_comment
comment|/** Just produces one single fragment for the entire text */
end_comment
begin_class
DECL|class|WholeBreakIterator
specifier|final
class|class
name|WholeBreakIterator
extends|extends
name|BreakIterator
block|{
DECL|field|text
specifier|private
name|CharacterIterator
name|text
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|private
name|int
name|end
decl_stmt|;
DECL|field|current
specifier|private
name|int
name|current
decl_stmt|;
annotation|@
name|Override
DECL|method|current
specifier|public
name|int
name|current
parameter_list|()
block|{
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|first
specifier|public
name|int
name|first
parameter_list|()
block|{
return|return
operator|(
name|current
operator|=
name|start
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|following
specifier|public
name|int
name|following
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
argument_list|<
name|start
operator|||
name|pos
argument_list|>
name|end
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"offset out of bounds"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|==
name|end
condition|)
block|{
comment|// this conflicts with the javadocs, but matches actual behavior (Oracle has a bug in something)
comment|// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=9000909
name|current
operator|=
name|end
expr_stmt|;
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|last
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getText
specifier|public
name|CharacterIterator
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
annotation|@
name|Override
DECL|method|last
specifier|public
name|int
name|last
parameter_list|()
block|{
return|return
operator|(
name|current
operator|=
name|end
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
name|end
condition|)
block|{
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|last
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|int
name|next
parameter_list|(
name|int
name|n
parameter_list|)
block|{
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|-
name|n
condition|;
name|i
operator|++
control|)
block|{
name|previous
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|next
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|preceding
specifier|public
name|int
name|preceding
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
argument_list|<
name|start
operator|||
name|pos
argument_list|>
name|end
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"offset out of bounds"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|==
name|start
condition|)
block|{
comment|// this conflicts with the javadocs, but matches actual behavior (Oracle has a bug in something)
comment|// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=9000909
name|current
operator|=
name|start
expr_stmt|;
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|first
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|previous
specifier|public
name|int
name|previous
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
name|start
condition|)
block|{
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|first
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setText
specifier|public
name|void
name|setText
parameter_list|(
name|CharacterIterator
name|newText
parameter_list|)
block|{
name|start
operator|=
name|newText
operator|.
name|getBeginIndex
argument_list|()
expr_stmt|;
name|end
operator|=
name|newText
operator|.
name|getEndIndex
argument_list|()
expr_stmt|;
name|text
operator|=
name|newText
expr_stmt|;
name|current
operator|=
name|newText
operator|.
name|getIndex
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
