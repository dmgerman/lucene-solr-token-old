begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
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
name|nio
operator|.
name|CharBuffer
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|CharFilter
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
name|MockCharFilter
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
name|MockTokenFilter
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
name|MockTokenizer
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
name|TokenFilter
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
name|Tokenizer
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
name|charfilter
operator|.
name|MappingCharFilter
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
name|charfilter
operator|.
name|NormalizeCharMap
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
name|commongrams
operator|.
name|CommonGramsFilter
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
name|miscellaneous
operator|.
name|WordDelimiterFilter
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
name|ngram
operator|.
name|EdgeNGramTokenizer
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
name|ngram
operator|.
name|NGramTokenFilter
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
name|shingle
operator|.
name|ShingleFilter
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
name|util
operator|.
name|CharArraySet
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
name|wikipedia
operator|.
name|WikipediaTokenizer
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
operator|.
name|SuppressCodecs
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
literal|"Direct"
argument_list|)
DECL|class|TestBugInSomething
specifier|public
class|class
name|TestBugInSomething
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CharArraySet
name|cas
init|=
operator|new
name|CharArraySet
argument_list|(
literal|3
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cas
operator|.
name|add
argument_list|(
literal|"jjp"
argument_list|)
expr_stmt|;
name|cas
operator|.
name|add
argument_list|(
literal|"wlmwoknt"
argument_list|)
expr_stmt|;
name|cas
operator|.
name|add
argument_list|(
literal|"tcgyreo"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizeCharMap
operator|.
name|Builder
name|builder
init|=
operator|new
name|NormalizeCharMap
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"mtqlpi"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"mwoknt"
argument_list|,
literal|"jjp"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"tcgyreo"
argument_list|,
literal|"zpfpajyws"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizeCharMap
name|map
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|t
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|,
literal|false
argument_list|,
operator|-
literal|65
argument_list|)
decl_stmt|;
name|TokenFilter
name|f
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|t
argument_list|,
name|cas
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|reader
operator|=
operator|new
name|MockCharFilter
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|MappingCharFilter
argument_list|(
name|map
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|TestRandomChains
operator|.
name|CheckThatYouDidntReadAnythingReaderWrapper
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
decl_stmt|;
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|false
argument_list|,
literal|"wmgddzunizdomqyj"
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|wrappedStream
name|CharFilter
name|wrappedStream
init|=
operator|new
name|CharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"bogus"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readAheadLimit
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"mark(int)"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"markSupported()"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"read()"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"read(char[])"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|CharBuffer
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"read(CharBuffer)"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|ready
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ready()"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"reset()"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"skip(long)"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"correct(int)"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"close()"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"read(char[], int, int)"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
DECL|method|testWrapping
specifier|public
name|void
name|testWrapping
parameter_list|()
throws|throws
name|Exception
block|{
name|CharFilter
name|cs
init|=
operator|new
name|TestRandomChains
operator|.
name|CheckThatYouDidntReadAnythingReaderWrapper
argument_list|(
name|wrappedStream
argument_list|)
decl_stmt|;
try|try
block|{
name|cs
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"mark(int)"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|markSupported
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"markSupported()"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|read
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"read()"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|read
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"read(char[])"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|read
argument_list|(
name|CharBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"read(CharBuffer)"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|reset
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"reset()"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|skip
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"skip(long)"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|correctOffset
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"correct(int)"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"close()"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cs
operator|.
name|read
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"read(char[], int, int)"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// todo: test framework?
DECL|class|SopTokenFilter
specifier|static
specifier|final
class|class
name|SopTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|method|SopTokenFilter
name|SopTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
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
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|input
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"->"
operator|+
name|this
operator|.
name|reflectAsString
argument_list|(
literal|false
argument_list|)
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
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|input
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".end()"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|input
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".close()"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|input
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".reset()"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-5269
annotation|@
name|Slow
DECL|method|testUnicodeShinglesAndNgrams
specifier|public
name|void
name|testUnicodeShinglesAndNgrams
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenizer
argument_list|(
literal|2
argument_list|,
literal|94
argument_list|)
decl_stmt|;
comment|//TokenStream stream = new SopTokenFilter(tokenizer);
name|TokenStream
name|stream
init|=
operator|new
name|ShingleFilter
argument_list|(
name|tokenizer
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|//stream = new SopTokenFilter(stream);
name|stream
operator|=
operator|new
name|NGramTokenFilter
argument_list|(
name|stream
argument_list|,
literal|55
argument_list|,
literal|83
argument_list|)
expr_stmt|;
comment|//stream = new SopTokenFilter(stream);
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|stream
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCuriousWikipediaString
specifier|public
name|void
name|testCuriousWikipediaString
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CharArraySet
name|protWords
init|=
operator|new
name|CharArraySet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"rrdpafa"
argument_list|,
literal|"pupmmlu"
argument_list|,
literal|"xlq"
argument_list|,
literal|"dyy"
argument_list|,
literal|"zqrxrrck"
argument_list|,
literal|"o"
argument_list|,
literal|"hsrlfvcha"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|byte
name|table
index|[]
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|57
block|,
literal|26
block|,
literal|1
block|,
literal|48
block|,
literal|63
block|,
operator|-
literal|23
block|,
literal|55
block|,
operator|-
literal|84
block|,
literal|18
block|,
literal|120
block|,
operator|-
literal|97
block|,
literal|103
block|,
literal|58
block|,
literal|13
block|,
literal|84
block|,
literal|89
block|,
literal|57
block|,
operator|-
literal|13
block|,
operator|-
literal|63
block|,
literal|5
block|,
literal|28
block|,
literal|97
block|,
operator|-
literal|54
block|,
operator|-
literal|94
block|,
literal|102
block|,
operator|-
literal|108
block|,
operator|-
literal|5
block|,
literal|5
block|,
literal|46
block|,
literal|40
block|,
literal|43
block|,
literal|78
block|,
literal|43
block|,
operator|-
literal|72
block|,
literal|36
block|,
literal|29
block|,
literal|124
block|,
operator|-
literal|106
block|,
operator|-
literal|22
block|,
operator|-
literal|51
block|,
literal|65
block|,
literal|5
block|,
literal|31
block|,
operator|-
literal|42
block|,
literal|6
block|,
operator|-
literal|99
block|,
literal|97
block|,
literal|14
block|,
literal|81
block|,
operator|-
literal|128
block|,
literal|74
block|,
literal|100
block|,
literal|54
block|,
operator|-
literal|55
block|,
operator|-
literal|25
block|,
literal|53
block|,
operator|-
literal|71
block|,
operator|-
literal|98
block|,
literal|44
block|,
literal|33
block|,
literal|86
block|,
literal|106
block|,
operator|-
literal|42
block|,
literal|47
block|,
literal|115
block|,
operator|-
literal|89
block|,
operator|-
literal|18
block|,
operator|-
literal|26
block|,
literal|22
block|,
operator|-
literal|95
block|,
operator|-
literal|43
block|,
literal|83
block|,
operator|-
literal|125
block|,
literal|105
block|,
operator|-
literal|104
block|,
operator|-
literal|24
block|,
literal|106
block|,
operator|-
literal|16
block|,
literal|126
block|,
literal|115
block|,
operator|-
literal|105
block|,
literal|97
block|,
literal|65
block|,
operator|-
literal|33
block|,
literal|57
block|,
literal|44
block|,
operator|-
literal|1
block|,
literal|123
block|,
operator|-
literal|68
block|,
literal|100
block|,
literal|13
block|,
operator|-
literal|41
block|,
operator|-
literal|64
block|,
operator|-
literal|119
block|,
literal|0
block|,
literal|92
block|,
literal|94
block|,
operator|-
literal|36
block|,
literal|53
block|,
operator|-
literal|9
block|,
operator|-
literal|102
block|,
operator|-
literal|18
block|,
literal|90
block|,
literal|94
block|,
operator|-
literal|26
block|,
literal|31
block|,
literal|71
block|,
operator|-
literal|20
block|}
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WikipediaTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|SopTokenFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|WordDelimiterFilter
argument_list|(
name|stream
argument_list|,
name|table
argument_list|,
operator|-
literal|50
argument_list|,
name|protWords
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|SopTokenFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|stream
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|false
argument_list|,
literal|"B\u28c3\ue0f8[ \ud800\udfc2</p> jb"
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
