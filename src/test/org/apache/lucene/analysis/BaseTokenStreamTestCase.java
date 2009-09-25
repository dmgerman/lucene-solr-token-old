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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|tokenattributes
operator|.
name|*
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
begin_comment
comment|/**   * Base class for all Lucene unit tests that use TokenStreams.    *<p>  * This class runs all tests twice, one time with {@link TokenStream#setOnlyUseNewAPI}<code>false</code>  * and after that one time with<code>true</code>.  */
end_comment
begin_class
DECL|class|BaseTokenStreamTestCase
specifier|public
specifier|abstract
class|class
name|BaseTokenStreamTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|onlyUseNewAPI
specifier|private
name|boolean
name|onlyUseNewAPI
init|=
literal|false
decl_stmt|;
DECL|field|testWithNewAPI
specifier|private
specifier|final
name|Set
name|testWithNewAPI
decl_stmt|;
DECL|method|BaseTokenStreamTestCase
specifier|public
name|BaseTokenStreamTestCase
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|testWithNewAPI
operator|=
literal|null
expr_stmt|;
comment|// run all tests also with onlyUseNewAPI
block|}
DECL|method|BaseTokenStreamTestCase
specifier|public
name|BaseTokenStreamTestCase
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|testWithNewAPI
operator|=
literal|null
expr_stmt|;
comment|// run all tests also with onlyUseNewAPI
block|}
DECL|method|BaseTokenStreamTestCase
specifier|public
name|BaseTokenStreamTestCase
parameter_list|(
name|Set
name|testWithNewAPI
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|testWithNewAPI
operator|=
name|testWithNewAPI
expr_stmt|;
block|}
DECL|method|BaseTokenStreamTestCase
specifier|public
name|BaseTokenStreamTestCase
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
name|testWithNewAPI
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|testWithNewAPI
operator|=
name|testWithNewAPI
expr_stmt|;
block|}
comment|// @Override
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
name|TokenStream
operator|.
name|setOnlyUseNewAPI
argument_list|(
name|onlyUseNewAPI
argument_list|)
expr_stmt|;
block|}
comment|// @Override
DECL|method|runBare
specifier|public
name|void
name|runBare
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// Do the test with onlyUseNewAPI=false (default)
try|try
block|{
name|onlyUseNewAPI
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|runBare
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test failure of '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' occurred with onlyUseNewAPI=false"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
if|if
condition|(
name|testWithNewAPI
operator|==
literal|null
operator|||
name|testWithNewAPI
operator|.
name|contains
argument_list|(
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Do the test again with onlyUseNewAPI=true
try|try
block|{
name|onlyUseNewAPI
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|runBare
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test failure of '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' occurred with onlyUseNewAPI=true"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|// some helpers to test Analyzers and TokenStreams:
DECL|method|assertTokenStreamContents
specifier|public
specifier|static
name|void
name|assertTokenStreamContents
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|String
name|types
index|[]
parameter_list|,
name|int
name|posIncrements
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"has TermAttribute"
argument_list|,
name|ts
operator|.
name|hasAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|startOffsets
operator|!=
literal|null
operator|||
name|endOffsets
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
literal|"has OffsetAttribute"
argument_list|,
name|ts
operator|.
name|hasAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
operator|(
name|OffsetAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|TypeAttribute
name|typeAtt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
literal|"has TypeAttribute"
argument_list|,
name|ts
operator|.
name|hasAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
operator|(
name|TypeAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|PositionIncrementAttribute
name|posIncrAtt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|posIncrements
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
literal|"has PositionIncrementAttribute"
argument_list|,
name|ts
operator|.
name|hasAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|ts
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|reset
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
name|output
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// extra safety to enforce, that the state is not preserved and also assign bogus values
name|ts
operator|.
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
literal|"bogusTerm"
argument_list|)
expr_stmt|;
if|if
condition|(
name|offsetAtt
operator|!=
literal|null
condition|)
name|offsetAtt
operator|.
name|setOffset
argument_list|(
literal|14584724
argument_list|,
literal|24683243
argument_list|)
expr_stmt|;
if|if
condition|(
name|typeAtt
operator|!=
literal|null
condition|)
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"bogusType"
argument_list|)
expr_stmt|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|45987657
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token "
operator|+
name|i
operator|+
literal|" exists"
argument_list|,
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"term "
operator|+
name|i
argument_list|,
name|output
index|[
name|i
index|]
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|startOffsets
operator|!=
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"startOffset "
operator|+
name|i
argument_list|,
name|startOffsets
index|[
name|i
index|]
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|endOffsets
operator|!=
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"endOffset "
operator|+
name|i
argument_list|,
name|endOffsets
index|[
name|i
index|]
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|types
operator|!=
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"type "
operator|+
name|i
argument_list|,
name|types
index|[
name|i
index|]
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|posIncrements
operator|!=
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"posIncrement "
operator|+
name|i
argument_list|,
name|posIncrements
index|[
name|i
index|]
argument_list|,
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"end of stream"
argument_list|,
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertTokenStreamContents
specifier|public
specifier|static
name|void
name|assertTokenStreamContents
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
index|[]
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTokenStreamContents
specifier|public
specifier|static
name|void
name|assertTokenStreamContents
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|String
index|[]
name|types
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|types
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTokenStreamContents
specifier|public
specifier|static
name|void
name|assertTokenStreamContents
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
index|[]
name|posIncrements
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTokenStreamContents
specifier|public
specifier|static
name|void
name|assertTokenStreamContents
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTokenStreamContents
specifier|public
specifier|static
name|void
name|assertTokenStreamContents
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|int
index|[]
name|posIncrements
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
literal|null
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
specifier|static
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|String
name|types
index|[]
parameter_list|,
name|int
name|posIncrements
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
name|types
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
specifier|static
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
specifier|static
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|String
index|[]
name|types
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|types
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
specifier|static
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
index|[]
name|posIncrements
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
specifier|static
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesTo
specifier|public
specifier|static
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|int
index|[]
name|posIncrements
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
literal|null
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesToReuse
specifier|public
specifier|static
name|void
name|assertAnalyzesToReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|String
name|types
index|[]
parameter_list|,
name|int
name|posIncrements
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTokenStreamContents
argument_list|(
name|a
operator|.
name|reusableTokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
name|types
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesToReuse
specifier|public
specifier|static
name|void
name|assertAnalyzesToReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesToReuse
specifier|public
specifier|static
name|void
name|assertAnalyzesToReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|String
index|[]
name|types
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|types
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesToReuse
specifier|public
specifier|static
name|void
name|assertAnalyzesToReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
index|[]
name|posIncrements
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesToReuse
specifier|public
specifier|static
name|void
name|assertAnalyzesToReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAnalyzesToReuse
specifier|public
specifier|static
name|void
name|assertAnalyzesToReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|,
name|int
name|startOffsets
index|[]
parameter_list|,
name|int
name|endOffsets
index|[]
parameter_list|,
name|int
index|[]
name|posIncrements
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
name|output
argument_list|,
name|startOffsets
argument_list|,
name|endOffsets
argument_list|,
literal|null
argument_list|,
name|posIncrements
argument_list|)
expr_stmt|;
block|}
comment|// simple utility method for testing stemmers
DECL|method|checkOneTerm
specifier|public
specifier|static
name|void
name|checkOneTerm
parameter_list|(
name|Analyzer
name|a
parameter_list|,
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
name|expected
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|checkOneTermReuse
specifier|public
specifier|static
name|void
name|checkOneTermReuse
parameter_list|(
name|Analyzer
name|a
parameter_list|,
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
name|expected
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
