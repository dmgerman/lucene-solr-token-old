begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_class
DECL|class|HunspellDictionaryTest
specifier|public
class|class
name|HunspellDictionaryTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testHunspellDictionary_loadDicAff
specifier|public
name|void
name|testHunspellDictionary_loadDicAff
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.dic"
argument_list|)
decl_stmt|;
name|HunspellDictionary
name|dictionary
init|=
operator|new
name|HunspellDictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'s'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'o'
block|,
literal|'l'
block|,
literal|'r'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCompressedHunspellDictionary_loadDicAff
specifier|public
name|void
name|testCompressedHunspellDictionary_loadDicAff
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"testCompressed.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"testCompressed.dic"
argument_list|)
decl_stmt|;
name|HunspellDictionary
name|dictionary
init|=
operator|new
name|HunspellDictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'s'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'o'
block|,
literal|'l'
block|,
literal|'r'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHunspellDictionary_loadDicWrongAff
specifier|public
name|void
name|testHunspellDictionary_loadDicWrongAff
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|InputStream
name|affixStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"testWrongAffixRule.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.dic"
argument_list|)
decl_stmt|;
name|HunspellDictionary
name|dictionary
init|=
operator|new
name|HunspellDictionary
argument_list|(
name|affixStream
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|dictStream
argument_list|)
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dictionary
operator|.
name|lookupSuffix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'e'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'s'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dictionary
operator|.
name|lookupWord
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'o'
block|,
literal|'l'
block|,
literal|'r'
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//strict parsing disabled: malformed rule is not loaded
name|assertNull
argument_list|(
name|dictionary
operator|.
name|lookupPrefix
argument_list|(
operator|new
name|char
index|[]
block|{
literal|'a'
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|affixStream
operator|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"testWrongAffixRule.aff"
argument_list|)
expr_stmt|;
name|dictStream
operator|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.dic"
argument_list|)
expr_stmt|;
comment|//strict parsing enabled: malformed rule causes ParseException
try|try
block|{
name|dictionary
operator|=
operator|new
name|HunspellDictionary
argument_list|(
name|affixStream
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|dictStream
argument_list|)
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The affix file contains a rule with less than five elements"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|23
argument_list|,
name|e
operator|.
name|getErrorOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
