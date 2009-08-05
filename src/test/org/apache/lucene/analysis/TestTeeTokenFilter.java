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
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|StandardFilter
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
name|StandardTokenizer
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
name|English
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
name|LuceneTestCase
import|;
end_import
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
name|StringReader
import|;
end_import
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
name|List
import|;
end_import
begin_comment
comment|/**  * tests for the TeeTokenFilter and SinkTokenizer  */
end_comment
begin_class
DECL|class|TestTeeTokenFilter
specifier|public
class|class
name|TestTeeTokenFilter
extends|extends
name|LuceneTestCase
block|{
DECL|field|buffer1
specifier|protected
name|StringBuffer
name|buffer1
decl_stmt|;
DECL|field|buffer2
specifier|protected
name|StringBuffer
name|buffer2
decl_stmt|;
DECL|field|tokens1
specifier|protected
name|String
index|[]
name|tokens1
decl_stmt|;
DECL|field|tokens2
specifier|protected
name|String
index|[]
name|tokens2
decl_stmt|;
DECL|method|TestTeeTokenFilter
specifier|public
name|TestTeeTokenFilter
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|tokens1
operator|=
operator|new
name|String
index|[]
block|{
literal|"The"
block|,
literal|"quick"
block|,
literal|"Burgundy"
block|,
literal|"Fox"
block|,
literal|"jumped"
block|,
literal|"over"
block|,
literal|"the"
block|,
literal|"lazy"
block|,
literal|"Red"
block|,
literal|"Dogs"
block|}
expr_stmt|;
name|tokens2
operator|=
operator|new
name|String
index|[]
block|{
literal|"The"
block|,
literal|"Lazy"
block|,
literal|"Dogs"
block|,
literal|"should"
block|,
literal|"stay"
block|,
literal|"on"
block|,
literal|"the"
block|,
literal|"porch"
block|}
expr_stmt|;
name|buffer1
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tokens1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer1
operator|.
name|append
argument_list|(
name|tokens1
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buffer2
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tokens2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer2
operator|.
name|append
argument_list|(
name|tokens2
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|SinkTokenizer
name|sink1
init|=
operator|new
name|SinkTokenizer
argument_list|(
literal|null
argument_list|)
block|{
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|t
operator|.
name|term
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"The"
argument_list|)
condition|)
block|{
name|super
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|TokenStream
name|source
init|=
operator|new
name|TeeTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|sink1
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|source
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|source
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|tokens1
index|[
name|i
index|]
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
name|tokens1
index|[
name|i
index|]
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|i
operator|+
literal|" does not equal: "
operator|+
name|tokens1
operator|.
name|length
argument_list|,
name|i
operator|==
name|tokens1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sink1 Size: "
operator|+
name|sink1
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|sink1
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Token
name|token
init|=
name|sink1
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|token
operator|!=
literal|null
condition|;
name|token
operator|=
name|sink1
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|token
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"The"
argument_list|,
name|token
operator|.
name|term
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"The"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|i
operator|+
literal|" does not equal: "
operator|+
name|sink1
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|i
operator|==
name|sink1
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleSources
specifier|public
name|void
name|testMultipleSources
parameter_list|()
throws|throws
name|Exception
block|{
name|SinkTokenizer
name|theDetector
init|=
operator|new
name|SinkTokenizer
argument_list|(
literal|null
argument_list|)
block|{
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|t
operator|.
name|term
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"The"
argument_list|)
condition|)
block|{
name|super
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|SinkTokenizer
name|dogDetector
init|=
operator|new
name|SinkTokenizer
argument_list|(
literal|null
argument_list|)
block|{
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|t
operator|.
name|term
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Dogs"
argument_list|)
condition|)
block|{
name|super
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|TokenStream
name|source1
init|=
operator|new
name|CachingTokenFilter
argument_list|(
operator|new
name|TeeTokenFilter
argument_list|(
operator|new
name|TeeTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|theDetector
argument_list|)
argument_list|,
name|dogDetector
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|source2
init|=
operator|new
name|TeeTokenFilter
argument_list|(
operator|new
name|TeeTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer2
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|theDetector
argument_list|)
argument_list|,
name|dogDetector
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|source1
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|source1
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|tokens1
index|[
name|i
index|]
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
name|tokens1
index|[
name|i
index|]
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|i
operator|+
literal|" does not equal: "
operator|+
name|tokens1
operator|.
name|length
argument_list|,
name|i
operator|==
name|tokens1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"theDetector Size: "
operator|+
name|theDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|theDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"dogDetector Size: "
operator|+
name|dogDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|dogDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|source2
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|source2
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|tokens2
index|[
name|i
index|]
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
name|tokens2
index|[
name|i
index|]
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|i
operator|+
literal|" does not equal: "
operator|+
name|tokens2
operator|.
name|length
argument_list|,
name|i
operator|==
name|tokens2
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"theDetector Size: "
operator|+
name|theDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|theDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"dogDetector Size: "
operator|+
name|dogDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|dogDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|theDetector
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|theDetector
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"The"
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"The"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|i
operator|+
literal|" does not equal: "
operator|+
name|theDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|i
operator|==
name|theDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|dogDetector
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|dogDetector
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"Dogs"
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Dogs"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|i
operator|+
literal|" does not equal: "
operator|+
name|dogDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|i
operator|==
name|dogDetector
operator|.
name|getTokens
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|source1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|TokenStream
name|lowerCasing
init|=
operator|new
name|LowerCaseFilter
argument_list|(
name|source1
argument_list|)
decl_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|lowerCasing
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|lowerCasing
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|tokens1
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|nextToken
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
name|tokens1
index|[
name|i
index|]
operator|.
name|toLowerCase
argument_list|()
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|i
operator|+
literal|" does not equal: "
operator|+
name|tokens1
operator|.
name|length
argument_list|,
name|i
operator|==
name|tokens1
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Not an explicit test, just useful to print out some info on performance    *    * @throws Exception    */
DECL|method|performance
specifier|public
name|void
name|performance
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|tokCount
init|=
block|{
literal|100
block|,
literal|500
block|,
literal|1000
block|,
literal|2000
block|,
literal|5000
block|,
literal|10000
block|}
decl_stmt|;
name|int
index|[]
name|modCounts
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|5
block|,
literal|10
block|,
literal|20
block|,
literal|50
block|,
literal|100
block|,
literal|200
block|,
literal|500
block|}
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
name|tokCount
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-----Tokens: "
operator|+
name|tokCount
index|[
name|k
index|]
operator|+
literal|"-----"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tokCount
index|[
name|k
index|]
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
comment|//make sure we produce the same tokens
name|ModuloSinkTokenizer
name|sink
init|=
operator|new
name|ModuloSinkTokenizer
argument_list|(
name|tokCount
index|[
name|k
index|]
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|TeeTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
operator|new
name|StandardTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|sink
argument_list|)
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
operator|!=
literal|null
condition|)
block|{       }
name|stream
operator|=
operator|new
name|ModuloTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
operator|new
name|StandardTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|List
name|tmp
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|nextToken
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
name|sinkList
init|=
name|sink
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tmp Size: "
operator|+
name|tmp
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|sinkList
operator|.
name|size
argument_list|()
argument_list|,
name|tmp
operator|.
name|size
argument_list|()
operator|==
name|sinkList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tmp
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|tfTok
init|=
operator|(
name|Token
operator|)
name|tmp
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Token
name|sinkTok
init|=
operator|(
name|Token
operator|)
name|sinkList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tfTok
operator|.
name|term
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|sinkTok
operator|.
name|term
argument_list|()
operator|+
literal|" at token: "
operator|+
name|i
argument_list|,
name|tfTok
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
name|sinkTok
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
comment|//simulate two fields, each being analyzed once, for 20 documents
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|modCounts
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|tfPos
init|=
literal|0
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|stream
operator|=
operator|new
name|StandardFilter
argument_list|(
operator|new
name|StandardTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|tfPos
operator|+=
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
name|stream
operator|=
operator|new
name|ModuloTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
operator|new
name|StandardTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|modCounts
index|[
name|j
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|tfPos
operator|+=
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|finish
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ModCount: "
operator|+
name|modCounts
index|[
name|j
index|]
operator|+
literal|" Two fields took "
operator|+
operator|(
name|finish
operator|-
name|start
operator|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|int
name|sinkPos
init|=
literal|0
decl_stmt|;
comment|//simulate one field with one sink
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|sink
operator|=
operator|new
name|ModuloSinkTokenizer
argument_list|(
name|tokCount
index|[
name|k
index|]
argument_list|,
name|modCounts
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|TeeTokenFilter
argument_list|(
operator|new
name|StandardFilter
argument_list|(
operator|new
name|StandardTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|sink
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|sinkPos
operator|+=
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("Modulo--------");
name|stream
operator|=
name|sink
expr_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|sinkPos
operator|+=
name|nextToken
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
name|finish
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ModCount: "
operator|+
name|modCounts
index|[
name|j
index|]
operator|+
literal|" Tee fields took "
operator|+
operator|(
name|finish
operator|-
name|start
operator|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sinkPos
operator|+
literal|" does not equal: "
operator|+
name|tfPos
argument_list|,
name|sinkPos
operator|==
name|tfPos
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"- End Tokens: "
operator|+
name|tokCount
index|[
name|k
index|]
operator|+
literal|"-----"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ModuloTokenFilter
class|class
name|ModuloTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|modCount
name|int
name|modCount
decl_stmt|;
DECL|method|ModuloTokenFilter
name|ModuloTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|mc
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|modCount
operator|=
name|mc
expr_stmt|;
block|}
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|//return every 100 tokens
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|nextToken
init|=
literal|null
decl_stmt|;
for|for
control|(
name|nextToken
operator|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
operator|&&
name|count
operator|%
name|modCount
operator|!=
literal|0
condition|;
name|nextToken
operator|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
return|return
name|nextToken
return|;
block|}
block|}
DECL|class|ModuloSinkTokenizer
class|class
name|ModuloSinkTokenizer
extends|extends
name|SinkTokenizer
block|{
DECL|field|count
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|modCount
name|int
name|modCount
decl_stmt|;
DECL|method|ModuloSinkTokenizer
name|ModuloSinkTokenizer
parameter_list|(
name|int
name|numToks
parameter_list|,
name|int
name|mc
parameter_list|)
block|{
name|modCount
operator|=
name|mc
expr_stmt|;
name|lst
operator|=
operator|new
name|ArrayList
argument_list|(
name|numToks
operator|%
name|mc
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|count
operator|%
name|modCount
operator|==
literal|0
condition|)
block|{
name|super
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
