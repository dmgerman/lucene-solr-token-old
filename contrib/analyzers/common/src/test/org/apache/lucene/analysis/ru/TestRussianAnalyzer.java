begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|InputStreamReader
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|TermAttribute
import|;
end_import
begin_comment
comment|/**  * Test case for RussianAnalyzer.  *  *  * @version   $Id$  */
end_comment
begin_class
DECL|class|TestRussianAnalyzer
specifier|public
class|class
name|TestRussianAnalyzer
extends|extends
name|TestCase
block|{
DECL|field|inWords
specifier|private
name|InputStreamReader
name|inWords
decl_stmt|;
DECL|field|sampleUnicode
specifier|private
name|InputStreamReader
name|sampleUnicode
decl_stmt|;
DECL|field|inWordsKOI8
specifier|private
name|Reader
name|inWordsKOI8
decl_stmt|;
DECL|field|sampleKOI8
specifier|private
name|Reader
name|sampleKOI8
decl_stmt|;
DECL|field|inWords1251
specifier|private
name|Reader
name|inWords1251
decl_stmt|;
DECL|field|sample1251
specifier|private
name|Reader
name|sample1251
decl_stmt|;
DECL|field|dataDir
specifier|private
name|File
name|dataDir
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnicode
specifier|public
name|void
name|testUnicode
parameter_list|()
throws|throws
name|IOException
block|{
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
decl_stmt|;
name|inWords
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/testUTF8.txt"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|sampleUnicode
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/resUTF8.htm"
argument_list|)
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|TokenStream
name|in
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|"all"
argument_list|,
name|inWords
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|sample
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|sampleUnicode
argument_list|,
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
decl_stmt|;
name|TermAttribute
name|text
init|=
operator|(
name|TermAttribute
operator|)
name|in
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TermAttribute
name|sampleText
init|=
operator|(
name|TermAttribute
operator|)
name|sample
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
operator|==
literal|false
condition|)
break|break;
name|boolean
name|nextSampleToken
init|=
name|sample
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unicode"
argument_list|,
name|text
operator|.
name|term
argument_list|()
argument_list|,
name|nextSampleToken
operator|==
literal|false
condition|?
literal|null
else|:
name|sampleText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inWords
operator|.
name|close
argument_list|()
expr_stmt|;
name|sampleUnicode
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testKOI8
specifier|public
name|void
name|testKOI8
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println(new java.util.Date());
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|RussianCharsets
operator|.
name|KOI8
argument_list|)
decl_stmt|;
comment|// KOI8
name|inWordsKOI8
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/testKOI8.txt"
argument_list|)
argument_list|)
argument_list|,
literal|"iso-8859-1"
argument_list|)
expr_stmt|;
name|sampleKOI8
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/resKOI8.htm"
argument_list|)
argument_list|)
argument_list|,
literal|"iso-8859-1"
argument_list|)
expr_stmt|;
name|TokenStream
name|in
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|"all"
argument_list|,
name|inWordsKOI8
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|sample
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|sampleKOI8
argument_list|,
name|RussianCharsets
operator|.
name|KOI8
argument_list|)
decl_stmt|;
name|TermAttribute
name|text
init|=
operator|(
name|TermAttribute
operator|)
name|in
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TermAttribute
name|sampleText
init|=
operator|(
name|TermAttribute
operator|)
name|sample
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
operator|==
literal|false
condition|)
break|break;
name|boolean
name|nextSampleToken
init|=
name|sample
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"KOI8"
argument_list|,
name|text
operator|.
name|term
argument_list|()
argument_list|,
name|nextSampleToken
operator|==
literal|false
condition|?
literal|null
else|:
name|sampleText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inWordsKOI8
operator|.
name|close
argument_list|()
expr_stmt|;
name|sampleKOI8
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|test1251
specifier|public
name|void
name|test1251
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 1251
name|inWords1251
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/test1251.txt"
argument_list|)
argument_list|)
argument_list|,
literal|"iso-8859-1"
argument_list|)
expr_stmt|;
name|sample1251
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"/org/apache/lucene/analysis/ru/res1251.htm"
argument_list|)
argument_list|)
argument_list|,
literal|"iso-8859-1"
argument_list|)
expr_stmt|;
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|RussianCharsets
operator|.
name|CP1251
argument_list|)
decl_stmt|;
name|TokenStream
name|in
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|inWords1251
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|sample
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|sample1251
argument_list|,
name|RussianCharsets
operator|.
name|CP1251
argument_list|)
decl_stmt|;
name|TermAttribute
name|text
init|=
operator|(
name|TermAttribute
operator|)
name|in
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TermAttribute
name|sampleText
init|=
operator|(
name|TermAttribute
operator|)
name|sample
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
operator|==
literal|false
condition|)
break|break;
name|boolean
name|nextSampleToken
init|=
name|sample
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1251"
argument_list|,
name|text
operator|.
name|term
argument_list|()
argument_list|,
name|nextSampleToken
operator|==
literal|false
condition|?
literal|null
else|:
name|sampleText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inWords1251
operator|.
name|close
argument_list|()
expr_stmt|;
name|sample1251
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDigitsInRussianCharset
specifier|public
name|void
name|testDigitsInRussianCharset
parameter_list|()
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"text 1000"
argument_list|)
decl_stmt|;
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TermAttribute
name|termText
init|=
operator|(
name|TermAttribute
operator|)
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|termText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RussianAnalyzer's tokenizer skips numbers from input text"
argument_list|,
literal|"1000"
argument_list|,
name|termText
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"unexpected IOException"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
