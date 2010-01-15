begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|Token
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
name|WhitespaceTokenizer
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
begin_class
DECL|class|TestPrefixAwareTokenFilter
specifier|public
class|class
name|TestPrefixAwareTokenFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|PrefixAwareTokenFilter
name|ts
decl_stmt|;
name|ts
operator|=
operator|new
name|PrefixAwareTokenFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
name|createToken
argument_list|(
literal|"a"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SingleTokenTokenStream
argument_list|(
name|createToken
argument_list|(
literal|"b"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
comment|// prefix and suffix using 2x prefix
name|ts
operator|=
operator|new
name|PrefixAwareTokenFilter
argument_list|(
operator|new
name|SingleTokenTokenStream
argument_list|(
name|createToken
argument_list|(
literal|"^"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|,
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|PrefixAwareTokenFilter
argument_list|(
name|ts
argument_list|,
operator|new
name|SingleTokenTokenStream
argument_list|(
name|createToken
argument_list|(
literal|"$"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"^"
block|,
literal|"hello"
block|,
literal|"world"
block|,
literal|"$"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|6
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|11
block|,
literal|11
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|createToken
specifier|private
specifier|static
name|Token
name|createToken
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|start
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|token
operator|.
name|setTermBuffer
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
block|}
end_class
end_unit
