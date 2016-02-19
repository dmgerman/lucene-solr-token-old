begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|Caverphone2
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|Metaphone
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
name|util
operator|.
name|ClasspathResourceLoader
import|;
end_import
begin_class
DECL|class|TestPhoneticFilterFactory
specifier|public
class|class
name|TestPhoneticFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Case: default    */
DECL|method|testFactoryDefaults
specifier|public
name|void
name|testFactoryDefaults
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Metaphone"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Metaphone
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
block|}
DECL|method|testInjectFalse
specifier|public
name|void
name|testInjectFalse
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Metaphone"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|INJECT
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|factory
operator|.
name|inject
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxCodeLength
specifier|public
name|void
name|testMaxCodeLength
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Metaphone"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|MAX_CODE_LENGTH
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|Metaphone
operator|)
name|factory
operator|.
name|getEncoder
argument_list|()
operator|)
operator|.
name|getMaxCodeLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Case: Failures and Exceptions    */
DECL|method|testMissingEncoder
specifier|public
name|void
name|testMissingEncoder
parameter_list|()
throws|throws
name|IOException
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|PhoneticFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Configuration Error: missing parameter 'encoder'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnknownEncoder
specifier|public
name|void
name|testUnknownEncoder
parameter_list|()
throws|throws
name|IOException
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"encoder"
argument_list|,
literal|"XXX"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Error loading encoder"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnknownEncoderReflection
specifier|public
name|void
name|testUnknownEncoderReflection
parameter_list|()
throws|throws
name|IOException
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"encoder"
argument_list|,
literal|"org.apache.commons.codec.language.NonExistence"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Error loading encoder"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Case: Reflection    */
DECL|method|testFactoryReflection
specifier|public
name|void
name|testFactoryReflection
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"org.apache.commons.codec.language.Metaphone"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Metaphone
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
block|}
comment|/**     * we use "Caverphone2" as it is registered in the REGISTRY as Caverphone,    * so this effectively tests reflection without package name    */
DECL|method|testFactoryReflectionCaverphone2
specifier|public
name|void
name|testFactoryReflectionCaverphone2
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Caverphone2"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Caverphone2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
block|}
DECL|method|testFactoryReflectionCaverphone
specifier|public
name|void
name|testFactoryReflectionCaverphone
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|PhoneticFilterFactory
operator|.
name|ENCODER
argument_list|,
literal|"Caverphone"
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|getEncoder
argument_list|()
operator|instanceof
name|Caverphone2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|inject
argument_list|)
expr_stmt|;
comment|// default
block|}
DECL|method|testAlgorithms
specifier|public
name|void
name|testAlgorithms
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAlgorithm
argument_list|(
literal|"Metaphone"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"aaa"
block|,
literal|"B"
block|,
literal|"bbb"
block|,
literal|"KKK"
block|,
literal|"ccc"
block|,
literal|"ESKS"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Metaphone"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"KKK"
block|,
literal|"ESKS"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"DoubleMetaphone"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"aaa"
block|,
literal|"PP"
block|,
literal|"bbb"
block|,
literal|"KK"
block|,
literal|"ccc"
block|,
literal|"ASKS"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"DoubleMetaphone"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"PP"
block|,
literal|"KK"
block|,
literal|"ASKS"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Soundex"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A000"
block|,
literal|"aaa"
block|,
literal|"B000"
block|,
literal|"bbb"
block|,
literal|"C000"
block|,
literal|"ccc"
block|,
literal|"E220"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Soundex"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A000"
block|,
literal|"B000"
block|,
literal|"C000"
block|,
literal|"E220"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"RefinedSoundex"
argument_list|,
literal|"true"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A0"
block|,
literal|"aaa"
block|,
literal|"B1"
block|,
literal|"bbb"
block|,
literal|"C3"
block|,
literal|"ccc"
block|,
literal|"E034034"
block|,
literal|"easgasg"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"RefinedSoundex"
argument_list|,
literal|"false"
argument_list|,
literal|"aaa bbb ccc easgasg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"A0"
block|,
literal|"B1"
block|,
literal|"C3"
block|,
literal|"E034034"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Caverphone"
argument_list|,
literal|"true"
argument_list|,
literal|"Darda Karleen Datha Carlene"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TTA1111111"
block|,
literal|"Darda"
block|,
literal|"KLN1111111"
block|,
literal|"Karleen"
block|,
literal|"TTA1111111"
block|,
literal|"Datha"
block|,
literal|"KLN1111111"
block|,
literal|"Carlene"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Caverphone"
argument_list|,
literal|"false"
argument_list|,
literal|"Darda Karleen Datha Carlene"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"TTA1111111"
block|,
literal|"KLN1111111"
block|,
literal|"TTA1111111"
block|,
literal|"KLN1111111"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"ColognePhonetic"
argument_list|,
literal|"true"
argument_list|,
literal|"Meier Schmitt Meir Schmidt"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"67"
block|,
literal|"Meier"
block|,
literal|"862"
block|,
literal|"Schmitt"
block|,
literal|"67"
block|,
literal|"Meir"
block|,
literal|"862"
block|,
literal|"Schmidt"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"ColognePhonetic"
argument_list|,
literal|"false"
argument_list|,
literal|"Meier Schmitt Meir Schmidt"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"67"
block|,
literal|"862"
block|,
literal|"67"
block|,
literal|"862"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Nysiis"
argument_list|,
literal|"true"
argument_list|,
literal|"Macintosh Knuth Bart Hurd"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"MCANT"
block|,
literal|"Macintosh"
block|,
literal|"NAT"
block|,
literal|"Knuth"
block|,
literal|"BAD"
block|,
literal|"Bart"
block|,
literal|"HAD"
block|,
literal|"Hurd"
block|}
argument_list|)
expr_stmt|;
name|assertAlgorithm
argument_list|(
literal|"Nysiis"
argument_list|,
literal|"false"
argument_list|,
literal|"Macintosh Knuth Bart Hurd"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"MCANT"
block|,
literal|"NAT"
block|,
literal|"BAD"
block|,
literal|"HAD"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|PhoneticFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"encoder"
argument_list|,
literal|"Metaphone"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAlgorithm
specifier|static
name|void
name|assertAlgorithm
parameter_list|(
name|String
name|algName
parameter_list|,
name|String
name|inject
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Tokenizer
name|tokenizer
init|=
name|whitespaceMockTokenizer
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"encoder"
argument_list|,
name|algName
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"inject"
argument_list|,
name|inject
argument_list|)
expr_stmt|;
name|PhoneticFilterFactory
name|factory
init|=
operator|new
name|PhoneticFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ClasspathResourceLoader
argument_list|(
name|factory
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
