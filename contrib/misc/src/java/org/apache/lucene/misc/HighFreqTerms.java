begin_unit
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
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
name|index
operator|.
name|TermEnum
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
name|FSDirectory
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
name|util
operator|.
name|PriorityQueue
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_comment
comment|/**  *<code>HighFreqTerms</code> class extracts terms and their frequencies out  * of an existing Lucene index.  */
end_comment
begin_class
DECL|class|HighFreqTerms
specifier|public
class|class
name|HighFreqTerms
block|{
comment|// The top numTerms will be displayed
DECL|field|numTerms
specifier|public
specifier|static
specifier|final
name|int
name|numTerms
init|=
literal|100
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|FSDirectory
name|dir
init|=
literal|null
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|field
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|TermInfoQueue
name|tiq
init|=
operator|new
name|TermInfoQueue
argument_list|(
name|numTerms
argument_list|)
decl_stmt|;
name|TermEnum
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|terms
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|tiq
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|TermInfo
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|,
name|terms
operator|.
name|docFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
name|tiq
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|TermInfo
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|,
name|terms
operator|.
name|docFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
name|tiq
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|TermInfo
name|termInfo
init|=
name|tiq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|termInfo
operator|.
name|term
operator|+
literal|" "
operator|+
name|termInfo
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|usage
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n\n"
operator|+
literal|"java org.apache.lucene.misc.HighFreqTerms<index dir> [field]\n\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|TermInfo
specifier|final
class|class
name|TermInfo
block|{
DECL|method|TermInfo
name|TermInfo
parameter_list|(
name|Term
name|t
parameter_list|,
name|int
name|df
parameter_list|)
block|{
name|term
operator|=
name|t
expr_stmt|;
name|docFreq
operator|=
name|df
expr_stmt|;
block|}
DECL|field|docFreq
name|int
name|docFreq
decl_stmt|;
DECL|field|term
name|Term
name|term
decl_stmt|;
block|}
end_class
begin_class
DECL|class|TermInfoQueue
specifier|final
class|class
name|TermInfoQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TermInfo
argument_list|>
block|{
DECL|method|TermInfoQueue
name|TermInfoQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|TermInfo
name|termInfoA
parameter_list|,
name|TermInfo
name|termInfoB
parameter_list|)
block|{
return|return
name|termInfoA
operator|.
name|docFreq
operator|<
name|termInfoB
operator|.
name|docFreq
return|;
block|}
block|}
end_class
end_unit
