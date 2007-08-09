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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
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
name|Token
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
literal|"/org/apache/lucene/analysis/ru/testUnicode.txt"
argument_list|)
argument_list|)
argument_list|,
literal|"Unicode"
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
literal|"/org/apache/lucene/analysis/ru/resUnicode.htm"
argument_list|)
argument_list|)
argument_list|,
literal|"Unicode"
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
for|for
control|(
init|;
condition|;
control|)
block|{
name|Token
name|token
init|=
name|in
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|Token
name|sampleToken
init|=
name|sample
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unicode"
argument_list|,
name|token
operator|.
name|termText
argument_list|()
argument_list|,
name|sampleToken
operator|==
literal|null
condition|?
literal|null
else|:
name|sampleToken
operator|.
name|termText
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
for|for
control|(
init|;
condition|;
control|)
block|{
name|Token
name|token
init|=
name|in
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|Token
name|sampleToken
init|=
name|sample
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"KOI8"
argument_list|,
name|token
operator|.
name|termText
argument_list|()
argument_list|,
name|sampleToken
operator|==
literal|null
condition|?
literal|null
else|:
name|sampleToken
operator|.
name|termText
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
for|for
control|(
init|;
condition|;
control|)
block|{
name|Token
name|token
init|=
name|in
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|Token
name|sampleToken
init|=
name|sample
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1251"
argument_list|,
name|token
operator|.
name|termText
argument_list|()
argument_list|,
name|sampleToken
operator|==
literal|null
condition|?
literal|null
else|:
name|sampleToken
operator|.
name|termText
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
block|}
end_class
end_unit
