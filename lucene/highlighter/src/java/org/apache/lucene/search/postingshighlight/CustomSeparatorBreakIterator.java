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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link BreakIterator} that breaks the text whenever a certain separator, provided as a constructor argument, is found.  */
end_comment
begin_class
DECL|class|CustomSeparatorBreakIterator
specifier|public
specifier|final
class|class
name|CustomSeparatorBreakIterator
extends|extends
name|BreakIterator
block|{
DECL|field|separator
specifier|private
specifier|final
name|char
name|separator
decl_stmt|;
DECL|field|text
specifier|private
name|CharacterIterator
name|text
decl_stmt|;
DECL|field|current
specifier|private
name|int
name|current
decl_stmt|;
DECL|method|CustomSeparatorBreakIterator
specifier|public
name|CustomSeparatorBreakIterator
parameter_list|(
name|char
name|separator
parameter_list|)
block|{
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
block|}
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
name|text
operator|.
name|setIndex
argument_list|(
name|text
operator|.
name|getBeginIndex
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
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
name|text
operator|.
name|setIndex
argument_list|(
name|text
operator|.
name|getEndIndex
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
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
name|text
operator|.
name|getIndex
argument_list|()
operator|==
name|text
operator|.
name|getEndIndex
argument_list|()
condition|)
block|{
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|advanceForward
argument_list|()
return|;
block|}
block|}
DECL|method|advanceForward
specifier|private
name|int
name|advanceForward
parameter_list|()
block|{
name|char
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|text
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|CharacterIterator
operator|.
name|DONE
condition|)
block|{
if|if
condition|(
name|c
operator|==
name|separator
condition|)
block|{
return|return
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
operator|+
literal|1
return|;
block|}
block|}
assert|assert
name|text
operator|.
name|getIndex
argument_list|()
operator|==
name|text
operator|.
name|getEndIndex
argument_list|()
assert|;
return|return
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
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
name|text
operator|.
name|getBeginIndex
operator|(
operator|)
operator|||
name|pos
argument_list|>
name|text
operator|.
name|getEndIndex
argument_list|()
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
name|text
operator|.
name|getEndIndex
argument_list|()
condition|)
block|{
comment|// this conflicts with the javadocs, but matches actual behavior (Oracle has a bug in something)
comment|// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=9000909
name|text
operator|.
name|setIndex
argument_list|(
name|text
operator|.
name|getEndIndex
argument_list|()
argument_list|)
expr_stmt|;
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
expr_stmt|;
return|return
name|DONE
return|;
block|}
else|else
block|{
name|text
operator|.
name|setIndex
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
expr_stmt|;
return|return
name|advanceForward
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
name|text
operator|.
name|getIndex
argument_list|()
operator|==
name|text
operator|.
name|getBeginIndex
argument_list|()
condition|)
block|{
return|return
name|DONE
return|;
block|}
else|else
block|{
return|return
name|advanceBackward
argument_list|()
return|;
block|}
block|}
DECL|method|advanceBackward
specifier|private
name|int
name|advanceBackward
parameter_list|()
block|{
name|char
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|text
operator|.
name|previous
argument_list|()
operator|)
operator|!=
name|CharacterIterator
operator|.
name|DONE
condition|)
block|{
if|if
condition|(
name|c
operator|==
name|separator
condition|)
block|{
return|return
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
operator|+
literal|1
return|;
block|}
block|}
assert|assert
name|text
operator|.
name|getIndex
argument_list|()
operator|==
name|text
operator|.
name|getBeginIndex
argument_list|()
assert|;
return|return
name|current
operator|=
name|text
operator|.
name|getIndex
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
name|text
operator|.
name|getBeginIndex
operator|(
operator|)
operator|||
name|pos
argument_list|>
name|text
operator|.
name|getEndIndex
argument_list|()
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
name|text
operator|.
name|getBeginIndex
argument_list|()
condition|)
block|{
comment|// this conflicts with the javadocs, but matches actual behavior (Oracle has a bug in something)
comment|// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=9000909
name|text
operator|.
name|setIndex
argument_list|(
name|text
operator|.
name|getBeginIndex
argument_list|()
argument_list|)
expr_stmt|;
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
expr_stmt|;
return|return
name|DONE
return|;
block|}
else|else
block|{
name|text
operator|.
name|setIndex
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|current
operator|=
name|text
operator|.
name|getIndex
argument_list|()
expr_stmt|;
return|return
name|advanceBackward
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
DECL|method|setText
specifier|public
name|void
name|setText
parameter_list|(
name|CharacterIterator
name|newText
parameter_list|)
block|{
name|text
operator|=
name|newText
expr_stmt|;
name|current
operator|=
name|text
operator|.
name|getBeginIndex
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
