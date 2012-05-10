begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
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
name|OffsetAttribute
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
name|PositionIncrementAttribute
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
name|CharTermAttribute
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
name|Arrays
import|;
end_import
begin_comment
comment|/** Simple tests to ensure this factory is working */
end_comment
begin_class
DECL|class|TestRemoveDuplicatesTokenFilterFactory
specifier|public
class|class
name|TestRemoveDuplicatesTokenFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|tok
specifier|public
specifier|static
name|Token
name|tok
parameter_list|(
name|int
name|pos
parameter_list|,
name|String
name|t
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|Token
name|tok
init|=
operator|new
name|Token
argument_list|(
name|t
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|tok
operator|.
name|setPositionIncrement
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|tok
return|;
block|}
DECL|method|tok
specifier|public
specifier|static
name|Token
name|tok
parameter_list|(
name|int
name|pos
parameter_list|,
name|String
name|t
parameter_list|)
block|{
return|return
name|tok
argument_list|(
name|pos
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|testDups
specifier|public
name|void
name|testDups
parameter_list|(
specifier|final
name|String
name|expected
parameter_list|,
specifier|final
name|Token
modifier|...
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Iterator
argument_list|<
name|Token
argument_list|>
name|toks
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|tokens
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|RemoveDuplicatesTokenFilterFactory
name|factory
init|=
operator|new
name|RemoveDuplicatesTokenFilterFactory
argument_list|()
decl_stmt|;
specifier|final
name|TokenStream
name|ts
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|TokenStream
argument_list|()
block|{
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
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|toks
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|Token
name|tok
init|=
name|toks
operator|.
name|next
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|tok
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|tok
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tok
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|tok
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|expected
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleDups
specifier|public
name|void
name|testSimpleDups
parameter_list|()
throws|throws
name|Exception
block|{
name|testDups
argument_list|(
literal|"A B C D E"
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"A"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"B"
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"B"
argument_list|,
literal|11
argument_list|,
literal|15
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"C"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|0
argument_list|,
literal|"D"
argument_list|,
literal|16
argument_list|,
literal|20
argument_list|)
argument_list|,
name|tok
argument_list|(
literal|1
argument_list|,
literal|"E"
argument_list|,
literal|21
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
