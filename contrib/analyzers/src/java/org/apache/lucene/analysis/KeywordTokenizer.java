begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|KeywordTokenizer
specifier|public
class|class
name|KeywordTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|256
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|char
index|[]
name|buffer
decl_stmt|;
DECL|method|KeywordTokenizer
specifier|public
name|KeywordTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|KeywordTokenizer
specifier|public
name|KeywordTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|char
index|[
name|bufferSize
index|]
expr_stmt|;
name|this
operator|.
name|done
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|done
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|length
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|length
operator|=
name|input
operator|.
name|read
argument_list|(
name|this
operator|.
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
break|break;
name|buffer
operator|.
name|append
argument_list|(
name|this
operator|.
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|String
name|text
init|=
name|buffer
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|Token
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
