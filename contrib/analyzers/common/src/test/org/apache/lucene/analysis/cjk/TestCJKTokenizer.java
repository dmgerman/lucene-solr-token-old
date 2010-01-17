begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|Analyzer
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
name|Version
import|;
end_import
begin_class
DECL|class|TestCJKTokenizer
specifier|public
class|class
name|TestCJKTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|class|TestToken
class|class
name|TestToken
block|{
DECL|field|termText
name|String
name|termText
decl_stmt|;
DECL|field|start
name|int
name|start
decl_stmt|;
DECL|field|end
name|int
name|end
decl_stmt|;
DECL|field|type
name|String
name|type
decl_stmt|;
block|}
DECL|method|newToken
specifier|public
name|TestToken
name|newToken
parameter_list|(
name|String
name|termText
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|TestToken
name|token
init|=
operator|new
name|TestToken
argument_list|()
decl_stmt|;
name|token
operator|.
name|termText
operator|=
name|termText
expr_stmt|;
name|token
operator|.
name|type
operator|=
name|CJKTokenizer
operator|.
name|TOKEN_TYPE_NAMES
index|[
name|type
index|]
expr_stmt|;
name|token
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|token
operator|.
name|end
operator|=
name|end
expr_stmt|;
return|return
name|token
return|;
block|}
DECL|method|checkCJKToken
specifier|public
name|void
name|checkCJKToken
parameter_list|(
specifier|final
name|String
name|str
parameter_list|,
specifier|final
name|TestToken
index|[]
name|out_tokens
parameter_list|)
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|CJKAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|String
name|terms
index|[]
init|=
operator|new
name|String
index|[
name|out_tokens
operator|.
name|length
index|]
decl_stmt|;
name|int
name|startOffsets
index|[]
init|=
operator|new
name|int
index|[
name|out_tokens
operator|.
name|length
index|]
decl_stmt|;
name|int
name|endOffsets
index|[]
init|=
operator|new
name|int
index|[
name|out_tokens
operator|.
name|length
index|]
decl_stmt|;
name|String
name|types
index|[]
init|=
operator|new
name|String
index|[
name|out_tokens
operator|.
name|length
index|]
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
name|out_tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|termText
expr_stmt|;
name|startOffsets
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|start
expr_stmt|;
name|endOffsets
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|end
expr_stmt|;
name|types
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|type
expr_stmt|;
block|}
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
name|str
argument_list|,
name|terms
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
name|types
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|checkCJKTokenReusable
specifier|public
name|void
name|checkCJKTokenReusable
parameter_list|(
specifier|final
name|Analyzer
name|a
parameter_list|,
specifier|final
name|String
name|str
parameter_list|,
specifier|final
name|TestToken
index|[]
name|out_tokens
parameter_list|)
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|CJKAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|String
name|terms
index|[]
init|=
operator|new
name|String
index|[
name|out_tokens
operator|.
name|length
index|]
decl_stmt|;
name|int
name|startOffsets
index|[]
init|=
operator|new
name|int
index|[
name|out_tokens
operator|.
name|length
index|]
decl_stmt|;
name|int
name|endOffsets
index|[]
init|=
operator|new
name|int
index|[
name|out_tokens
operator|.
name|length
index|]
decl_stmt|;
name|String
name|types
index|[]
init|=
operator|new
name|String
index|[
name|out_tokens
operator|.
name|length
index|]
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
name|out_tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|terms
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|termText
expr_stmt|;
name|startOffsets
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|start
expr_stmt|;
name|endOffsets
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|end
expr_stmt|;
name|types
index|[
name|i
index|]
operator|=
name|out_tokens
index|[
name|i
index|]
operator|.
name|type
expr_stmt|;
block|}
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
name|str
argument_list|,
name|terms
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
name|types
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testJa1
specifier|public
name|void
name|testJa1
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|"\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u4e00\u4e8c"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e8c\u4e09"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e09\u56db"
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u56db\u4e94"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e94\u516d"
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u516d\u4e03"
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e03\u516b"
argument_list|,
literal|6
argument_list|,
literal|8
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u516b\u4e5d"
argument_list|,
literal|7
argument_list|,
literal|9
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e5d\u5341"
argument_list|,
literal|8
argument_list|,
literal|10
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
DECL|method|testJa2
specifier|public
name|void
name|testJa2
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|"\u4e00 \u4e8c\u4e09\u56db \u4e94\u516d\u4e03\u516b\u4e5d \u5341"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u4e00"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e8c\u4e09"
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e09\u56db"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e94\u516d"
argument_list|,
literal|6
argument_list|,
literal|8
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u516d\u4e03"
argument_list|,
literal|7
argument_list|,
literal|9
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u4e03\u516b"
argument_list|,
literal|8
argument_list|,
literal|10
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u516b\u4e5d"
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u5341"
argument_list|,
literal|12
argument_list|,
literal|13
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
DECL|method|testC
specifier|public
name|void
name|testC
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|"abc defgh ijklmn opqrstu vwxy z"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"defgh"
argument_list|,
literal|4
argument_list|,
literal|9
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ijklmn"
argument_list|,
literal|10
argument_list|,
literal|16
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"opqrstu"
argument_list|,
literal|17
argument_list|,
literal|24
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"vwxy"
argument_list|,
literal|25
argument_list|,
literal|29
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"z"
argument_list|,
literal|30
argument_list|,
literal|31
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,     }
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
DECL|method|testMix
specifier|public
name|void
name|testMix
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|"\u3042\u3044\u3046\u3048\u304aabc\u304b\u304d\u304f\u3051\u3053"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u3042\u3044"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3044\u3046"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3046\u3048"
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3048\u304a"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"abc"
argument_list|,
literal|5
argument_list|,
literal|8
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304b\u304d"
argument_list|,
literal|8
argument_list|,
literal|10
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304d\u304f"
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304f\u3051"
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3051\u3053"
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
DECL|method|testMix2
specifier|public
name|void
name|testMix2
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|"\u3042\u3044\u3046\u3048\u304aab\u3093c\u304b\u304d\u304f\u3051 \u3053"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u3042\u3044"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3044\u3046"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3046\u3048"
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3048\u304a"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ab"
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3093"
argument_list|,
literal|7
argument_list|,
literal|8
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"c"
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304b\u304d"
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304d\u304f"
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304f\u3051"
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3053"
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleChar
specifier|public
name|void
name|testSingleChar
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|"\u4e00"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u4e00"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,      }
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
comment|/*    * Full-width text is normalized to half-width     */
DECL|method|testFullWidth
specifier|public
name|void
name|testFullWidth
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"ï¼´ï½ï½ï½ ï¼ï¼ï¼ï¼"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"1234"
argument_list|,
literal|5
argument_list|,
literal|9
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
comment|/*    * Non-english text (not just CJK) is treated the same as CJK: C1C2 C2C3     */
DECL|method|testNonIdeographic
specifier|public
name|void
name|testNonIdeographic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"\u4e00 Ø±ÙØ¨Ø±Øª ÙÙÙØ±"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u4e00"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"Ø±Ù"
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙØ¨"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"Ø¨Ø±"
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"Ø±Øª"
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙÙ"
argument_list|,
literal|8
argument_list|,
literal|10
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙÙ"
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙØ±"
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
comment|/*    * Non-english text with nonletters (non-spacing marks,etc) is treated as C1C2 C2C3,    * except for words are split around non-letters.    */
DECL|method|testNonIdeographicNonLetter
specifier|public
name|void
name|testNonIdeographicNonLetter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"\u4e00 Ø±ÙÙØ¨Ø±Øª ÙÙÙØ±"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u4e00"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"Ø±"
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙØ¨"
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"Ø¨Ø±"
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"Ø±Øª"
argument_list|,
literal|6
argument_list|,
literal|8
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙÙ"
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙÙ"
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ÙØ±"
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKToken
argument_list|(
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|CJKAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"\u4e00\u4e01\u4e02"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u4e00\u4e01"
block|,
literal|"\u4e01\u4e02"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|CJKAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|String
name|str
init|=
literal|"\u3042\u3044\u3046\u3048\u304aabc\u304b\u304d\u304f\u3051\u3053"
decl_stmt|;
name|TestToken
index|[]
name|out_tokens
init|=
block|{
name|newToken
argument_list|(
literal|"\u3042\u3044"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3044\u3046"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3046\u3048"
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3048\u304a"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"abc"
argument_list|,
literal|5
argument_list|,
literal|8
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304b\u304d"
argument_list|,
literal|8
argument_list|,
literal|10
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304d\u304f"
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304f\u3051"
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3051\u3053"
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKTokenReusable
argument_list|(
name|analyzer
argument_list|,
name|str
argument_list|,
name|out_tokens
argument_list|)
expr_stmt|;
name|str
operator|=
literal|"\u3042\u3044\u3046\u3048\u304aab\u3093c\u304b\u304d\u304f\u3051 \u3053"
expr_stmt|;
name|TestToken
index|[]
name|out_tokens2
init|=
block|{
name|newToken
argument_list|(
literal|"\u3042\u3044"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3044\u3046"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3046\u3048"
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3048\u304a"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ab"
argument_list|,
literal|5
argument_list|,
literal|7
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3093"
argument_list|,
literal|7
argument_list|,
literal|8
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"c"
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304b\u304d"
argument_list|,
literal|9
argument_list|,
literal|11
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304d\u304f"
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u304f\u3051"
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"\u3053"
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
decl_stmt|;
name|checkCJKTokenReusable
argument_list|(
name|analyzer
argument_list|,
name|str
argument_list|,
name|out_tokens2
argument_list|)
expr_stmt|;
block|}
comment|/**    * LUCENE-2207: wrong offset calculated by end()     */
DECL|method|testFinalOffset
specifier|public
name|void
name|testFinalOffset
parameter_list|()
throws|throws
name|IOException
block|{
name|checkCJKToken
argument_list|(
literal|"ãã"
argument_list|,
operator|new
name|TestToken
index|[]
block|{
name|newToken
argument_list|(
literal|"ãã"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|checkCJKToken
argument_list|(
literal|"ãã   "
argument_list|,
operator|new
name|TestToken
index|[]
block|{
name|newToken
argument_list|(
literal|"ãã"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|checkCJKToken
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestToken
index|[]
block|{
name|newToken
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|checkCJKToken
argument_list|(
literal|"test   "
argument_list|,
operator|new
name|TestToken
index|[]
block|{
name|newToken
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|checkCJKToken
argument_list|(
literal|"ããtest"
argument_list|,
operator|new
name|TestToken
index|[]
block|{
name|newToken
argument_list|(
literal|"ãã"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"test"
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|checkCJKToken
argument_list|(
literal|"testãã    "
argument_list|,
operator|new
name|TestToken
index|[]
block|{
name|newToken
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
name|CJKTokenizer
operator|.
name|SINGLE_TOKEN_TYPE
argument_list|)
block|,
name|newToken
argument_list|(
literal|"ãã"
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|,
name|CJKTokenizer
operator|.
name|DOUBLE_TOKEN_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
