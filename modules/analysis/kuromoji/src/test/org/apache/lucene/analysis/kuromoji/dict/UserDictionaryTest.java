begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji.dict
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|dict
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
name|FileNotFoundException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|kuromoji
operator|.
name|SegmenterTest
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
name|kuromoji
operator|.
name|dict
operator|.
name|UserDictionary
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
name|IOUtils
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|UserDictionaryTest
specifier|public
class|class
name|UserDictionaryTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|readDict
specifier|private
name|UserDictionary
name|readDict
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|SegmenterTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"userdict.txt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Cannot find userdict.txt in test classpath!"
argument_list|)
throw|;
try|try
block|{
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
decl_stmt|;
return|return
operator|new
name|UserDictionary
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLookup
specifier|public
name|void
name|testLookup
parameter_list|()
throws|throws
name|IOException
block|{
name|UserDictionary
name|dictionary
init|=
name|readDict
argument_list|()
decl_stmt|;
name|String
name|s
init|=
literal|"é¢è¥¿å½éç©ºæ¸¯ã«è¡ã£ã"
decl_stmt|;
name|int
index|[]
index|[]
name|dictionaryEntryResult
init|=
name|dictionary
operator|.
name|lookup
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// Length should be three é¢è¥¿, å½é, ç©ºæ¸¯
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionaryEntryResult
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Test positions
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dictionaryEntryResult
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// index of é¢è¥¿
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dictionaryEntryResult
index|[
literal|1
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// index of å½é
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|dictionaryEntryResult
index|[
literal|2
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// index of ç©ºæ¸¯
comment|// Test lengths
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dictionaryEntryResult
index|[
literal|0
index|]
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// length of é¢è¥¿
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dictionaryEntryResult
index|[
literal|1
index|]
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// length of å½é
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dictionaryEntryResult
index|[
literal|2
index|]
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// length of ç©ºæ¸¯
name|s
operator|=
literal|"é¢è¥¿å½éç©ºæ¸¯ã¨é¢è¥¿å½éç©ºæ¸¯ã«è¡ã£ã"
expr_stmt|;
name|int
index|[]
index|[]
name|dictionaryEntryResult2
init|=
name|dictionary
operator|.
name|lookup
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// Length should be six
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|dictionaryEntryResult2
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadings
specifier|public
name|void
name|testReadings
parameter_list|()
throws|throws
name|IOException
block|{
name|UserDictionary
name|dictionary
init|=
name|readDict
argument_list|()
decl_stmt|;
name|int
index|[]
index|[]
name|result
init|=
name|dictionary
operator|.
name|lookup
argument_list|(
literal|"æ¥æ¬çµæ¸æ°è"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|wordIdNihon
init|=
name|result
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
comment|// wordId of æ¥æ¬ in æ¥æ¬çµæ¸æ°è
name|assertEquals
argument_list|(
literal|"ããã³"
argument_list|,
name|dictionary
operator|.
name|getReading
argument_list|(
name|wordIdNihon
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|dictionary
operator|.
name|lookup
argument_list|(
literal|"æéé¾"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|wordIdAsashoryu
init|=
name|result
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
comment|// wordId for æéé¾
name|assertEquals
argument_list|(
literal|"ã¢ãµã·ã§ã¦ãªã¥ã¦"
argument_list|,
name|dictionary
operator|.
name|getReading
argument_list|(
name|wordIdAsashoryu
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPartOfSpeech
specifier|public
name|void
name|testPartOfSpeech
parameter_list|()
throws|throws
name|IOException
block|{
name|UserDictionary
name|dictionary
init|=
name|readDict
argument_list|()
decl_stmt|;
name|int
index|[]
index|[]
name|result
init|=
name|dictionary
operator|.
name|lookup
argument_list|(
literal|"æ¥æ¬çµæ¸æ°è"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|wordIdKeizai
init|=
name|result
index|[
literal|1
index|]
index|[
literal|0
index|]
decl_stmt|;
comment|// wordId of çµæ¸ in æ¥æ¬çµæ¸æ°è
name|assertEquals
argument_list|(
literal|"ã«ã¹ã¿ã åè©"
argument_list|,
name|dictionary
operator|.
name|getPartOfSpeech
argument_list|(
name|wordIdKeizai
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRead
specifier|public
name|void
name|testRead
parameter_list|()
throws|throws
name|IOException
block|{
name|UserDictionary
name|dictionary
init|=
name|readDict
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
