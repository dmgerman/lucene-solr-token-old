begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
import|;
end_import
begin_class
DECL|class|TermBuffer
specifier|final
class|class
name|TermBuffer
implements|implements
name|Cloneable
block|{
DECL|field|NO_CHARS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|NO_CHARS
init|=
operator|new
name|char
index|[
literal|0
index|]
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|text
specifier|private
name|char
index|[]
name|text
init|=
name|NO_CHARS
decl_stmt|;
DECL|field|textLength
specifier|private
name|int
name|textLength
decl_stmt|;
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
comment|// cached
DECL|method|compareTo
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
name|TermBuffer
name|other
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
name|other
operator|.
name|field
condition|)
comment|// fields are interned
return|return
name|compareChars
argument_list|(
name|text
argument_list|,
name|textLength
argument_list|,
name|other
operator|.
name|text
argument_list|,
name|other
operator|.
name|textLength
argument_list|)
return|;
else|else
return|return
name|field
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
DECL|method|compareChars
specifier|private
specifier|static
specifier|final
name|int
name|compareChars
parameter_list|(
name|char
index|[]
name|v1
parameter_list|,
name|int
name|len1
parameter_list|,
name|char
index|[]
name|v2
parameter_list|,
name|int
name|len2
parameter_list|)
block|{
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|len1
argument_list|,
name|len2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|end
condition|;
name|k
operator|++
control|)
block|{
name|char
name|c1
init|=
name|v1
index|[
name|k
index|]
decl_stmt|;
name|char
name|c2
init|=
name|v2
index|[
name|k
index|]
decl_stmt|;
if|if
condition|(
name|c1
operator|!=
name|c2
condition|)
block|{
return|return
name|c1
operator|-
name|c2
return|;
block|}
block|}
return|return
name|len1
operator|-
name|len2
return|;
block|}
DECL|method|setTextLength
specifier|private
specifier|final
name|void
name|setTextLength
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
if|if
condition|(
name|text
operator|.
name|length
operator|<
name|newLength
condition|)
block|{
name|char
index|[]
name|newText
init|=
operator|new
name|char
index|[
name|newLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|newText
argument_list|,
literal|0
argument_list|,
name|textLength
argument_list|)
expr_stmt|;
name|text
operator|=
name|newText
expr_stmt|;
block|}
name|textLength
operator|=
name|newLength
expr_stmt|;
block|}
DECL|method|read
specifier|public
specifier|final
name|void
name|read
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|term
operator|=
literal|null
expr_stmt|;
comment|// invalidate cache
name|int
name|start
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|totalLength
init|=
name|start
operator|+
name|length
decl_stmt|;
name|setTextLength
argument_list|(
name|totalLength
argument_list|)
expr_stmt|;
name|input
operator|.
name|readChars
argument_list|(
name|this
operator|.
name|text
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|input
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|set
specifier|public
specifier|final
name|void
name|set
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// copy text into the buffer
name|setTextLength
argument_list|(
name|term
operator|.
name|text
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|term
operator|.
name|text
argument_list|()
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|term
operator|.
name|text
argument_list|()
operator|.
name|length
argument_list|()
argument_list|,
name|text
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
DECL|method|set
specifier|public
specifier|final
name|void
name|set
parameter_list|(
name|TermBuffer
name|other
parameter_list|)
block|{
name|setTextLength
argument_list|(
name|other
operator|.
name|textLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|text
argument_list|,
literal|0
argument_list|,
name|text
argument_list|,
literal|0
argument_list|,
name|textLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|other
operator|.
name|field
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|other
operator|.
name|term
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|field
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|textLength
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|term
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|toTerm
specifier|public
name|Term
name|toTerm
parameter_list|()
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
comment|// unset
return|return
literal|null
return|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
name|term
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
operator|new
name|String
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|textLength
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|term
return|;
block|}
DECL|method|clone
specifier|protected
name|Object
name|clone
parameter_list|()
block|{
name|TermBuffer
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|TermBuffer
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{}
name|clone
operator|.
name|text
operator|=
operator|new
name|char
index|[
name|text
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|clone
operator|.
name|text
argument_list|,
literal|0
argument_list|,
name|textLength
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class
end_unit
