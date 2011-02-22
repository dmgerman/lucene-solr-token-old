begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collection
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
name|TokenStream
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
name|tokenattributes
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * @version $Id:$  */
end_comment
begin_class
DECL|class|TestTrimFilter
specifier|public
class|class
name|TestTrimFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testTrim
specifier|public
name|void
name|testTrim
parameter_list|()
throws|throws
name|Exception
block|{
name|char
index|[]
name|a
init|=
literal|" a "
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|b
init|=
literal|"b   "
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|ccc
init|=
literal|"cCc"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|whitespace
init|=
literal|"   "
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|empty
init|=
literal|""
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|IterTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|,
literal|6
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
name|ccc
argument_list|,
literal|0
argument_list|,
name|ccc
operator|.
name|length
argument_list|,
literal|11
argument_list|,
literal|15
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
name|whitespace
argument_list|,
literal|0
argument_list|,
name|whitespace
operator|.
name|length
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
name|empty
argument_list|,
literal|0
argument_list|,
name|empty
operator|.
name|length
argument_list|,
literal|21
argument_list|,
literal|21
argument_list|)
argument_list|)
decl_stmt|;
name|ts
operator|=
operator|new
name|TrimFilter
argument_list|(
name|ts
argument_list|,
literal|false
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
block|,
literal|"cCc"
block|,
literal|""
block|,
literal|""
block|}
argument_list|)
expr_stmt|;
name|a
operator|=
literal|" a"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|b
operator|=
literal|"b "
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|ccc
operator|=
literal|" c "
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|whitespace
operator|=
literal|"   "
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|ts
operator|=
operator|new
name|IterTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
name|ccc
argument_list|,
literal|0
argument_list|,
name|ccc
operator|.
name|length
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
operator|new
name|Token
argument_list|(
name|whitespace
argument_list|,
literal|0
argument_list|,
name|whitespace
operator|.
name|length
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|TrimFilter
argument_list|(
name|ts
argument_list|,
literal|true
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
block|,
literal|"c"
block|,
literal|""
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated (3.0) does not support custom attributes    */
annotation|@
name|Deprecated
DECL|class|IterTokenStream
specifier|private
specifier|static
class|class
name|IterTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|tokens
specifier|final
name|Token
name|tokens
index|[]
decl_stmt|;
DECL|field|index
name|int
name|index
init|=
literal|0
decl_stmt|;
DECL|field|termAtt
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAtt
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|flagsAtt
name|FlagsAttribute
name|flagsAtt
init|=
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeAtt
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|payloadAtt
name|PayloadAttribute
name|payloadAtt
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|IterTokenStream
specifier|public
name|IterTokenStream
parameter_list|(
name|Token
modifier|...
name|tokens
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
DECL|method|IterTokenStream
specifier|public
name|IterTokenStream
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|)
block|{
name|this
argument_list|(
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|Token
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|index
operator|>=
name|tokens
operator|.
name|length
condition|)
return|return
literal|false
return|;
else|else
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|Token
name|token
init|=
name|tokens
index|[
name|index
operator|++
index|]
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
name|token
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|token
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|token
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
